package com.github.giamgiammi.StructuredFileViewer.service;

import com.github.giamgiammi.StructuredFileViewer.model.InstanceMessage;
import com.github.giamgiammi.StructuredFileViewer.utils.AppProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import tools.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

/**
 * A service that ensures only a single instance of an application is running.
 * It facilitates inter-process communication through a UNIX domain socket.
 * The service can act as either a server (when the instance is primary)
 * or a client (when another instance is already running).
 *
 * This class uses a lock file to determine if it should run as a server or client.
 * When acting as a client, it sends messages to the single running server instance.
 * When acting as a server, it listens for messages from other instances and processes them.
 *
 * Key features:
 * - Ensures single application instance enforcement.
 * - Facilitates communication through messages between the client and server.
 * - Provides pluggable message handlers for server-side processing.
 *
 * Threading considerations:
 * - The server operates on a virtual thread for message listening.
 * - Blocking calls in the message handler may affect performance.
 *
 * Throws:
 * - IOException if the temporary directory is not writable or other file system issues occur.
 * - IllegalStateException if the client attempts to send a message while running as a server.
 *
 * Requirements:
 * - Support for UNIX domain sockets in the runtime environment.
 */
@Slf4j
public class SingleInstanceService {
    private final ObjectMapper mapper = new ObjectMapper();

    private final UnixDomainSocketAddress socketAddress;
    private ServerSocketChannel serverSocketChannel;
    @Getter
    private final boolean server;
    @SuppressWarnings("FieldCanBeLocal")//must stay open
    private final FileChannel lockFileChannel;

    @Setter
    private Consumer<InstanceMessage> messageHandler;

    public SingleInstanceService() throws IOException {
        val folder = Path.of(System.getProperty(AppProperty.TMP_DIR));

        Files.createDirectories(folder);

        val lockFile = folder.resolve("app.lock");
        if (!Files.exists(lockFile)) Files.createFile(lockFile);

        lockFileChannel = FileChannel.open(lockFile, StandardOpenOption.WRITE);

        val socketFile = folder.resolve("app.socket");

        if (lockFileChannel.tryLock() != null) {
            Files.deleteIfExists(socketFile);
            server = true;
        } else {
            server = false;
        }
        socketAddress = UnixDomainSocketAddress.of(socketFile);

        if (server) {
            serverSocketChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
            serverSocketChannel.bind(socketAddress);

            Thread.ofVirtual().name("signle_instance_server_thread").start(() -> {
                while (true) {
                    try {
                        val channel = serverSocketChannel.accept();
                        val message = parseMessage(channel);
                        log.info("Received message: {}", message);
                        if (messageHandler != null) messageHandler.accept(message);
                        else log.warn("No message handler registered, discarding message");
                    } catch (IOException e) {
                        log.error("Failed to read message", e);
                    }
                }
            });
        }
    }

    private InstanceMessage parseMessage(SocketChannel channel) throws IOException {
        try (val in = Channels.newInputStream(channel);
             val reader = new InputStreamReader(in, StandardCharsets.UTF_8);
             val bufferedReader = new BufferedReader(reader)) {
            return mapper.readValue(bufferedReader, InstanceMessage.class);
        }
    }

    /**
     * Sends a message to a Unix domain socket.
     * This method establishes a connection to a specified socket address and transmits
     * the serialized representation of the provided {@code InstanceMessage}. It is intended
     * for use in single-instance applications where communication between multiple process
     * instances is required.
     *
     * @param message the {@code InstanceMessage} to be sent, which contains relevant data
     *                such as files to open.
     *                Must not be {@code null}.
     * @throws IOException if an I/O error occurs while establishing a connection or writing
     *                     the message.
     * @throws IllegalStateException if this instance is configured as a server, preventing
     *                               it from sending messages.
     */
    public void sendMessage(@NonNull InstanceMessage message) throws IOException {
        if (server) throw new IllegalStateException("Cannot send message when running as server");
        try (val channel = SocketChannel.open(StandardProtocolFamily.UNIX)) {
            channel.connect(socketAddress);
            try (val out = Channels.newOutputStream(channel);
                 val writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                 val bufferedWriter = new BufferedWriter(writer)) {
                mapper.writeValue(bufferedWriter, message);
            }
        }
    }

    public boolean isClient() {
        return !server;
    }
}
