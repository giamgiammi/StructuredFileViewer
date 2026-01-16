package com.github.giamgiammi.StructuredFileViewer.utils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * A simple lock utility class that ensures only one thread can execute a task at a time.
 * If the lock is already held, the subsequent access attempts are logged as warnings.
 */
@Slf4j
public class SimpleLock {
    public static final String TRIED_TO_ACQUIRE_LOCK_WHILE_ALREADY_LOCKED = "Tried to acquire lock while already locked";
    private final AtomicBoolean locked = new AtomicBoolean(false);

    /**
     * Executes the provided {@code Runnable} with a simple lock mechanism to ensure
     * that only one thread can execute a task at a time. If the lock is already held,
     * a warning is logged, and the task is not executed.
     *
     * @param runnable the {@code Runnable} to be executed; must not be null
     */
    public void execute(@NonNull Runnable runnable) {
        if (locked.compareAndSet(false, true)) {
            try {
                runnable.run();
            } finally {
                locked.set(false);
            }
        } else {
            log.warn(TRIED_TO_ACQUIRE_LOCK_WHILE_ALREADY_LOCKED);
        }
    }

    /**
     * Executes the provided {@code Supplier} with a simple lock mechanism to ensure
     * that only one thread can execute a task at a time. If the lock is already held,
     * a warning is logged, and the default value is returned.
     *
     * @param <T> the type of the value supplied by the {@code Supplier}
     * @param supplier the {@code Supplier} to invoke to produce a value; must not be null
     * @param defaultValue the default value to return if the lock is already held
     * @return the value produced by the {@code Supplier} if the lock is acquired successfully,
     *         or the default value if the lock is already held
     */
    public <T> T execute(@NonNull Supplier<T> supplier, T defaultValue) {
        if (locked.compareAndSet(false, true)) {
            try {
                return supplier.get();
            } finally {
                locked.set(false);
            }
        } else {
            log.warn(TRIED_TO_ACQUIRE_LOCK_WHILE_ALREADY_LOCKED);
            return defaultValue;
        }
    }

    /**
     * Executes the provided {@code Supplier} with a simple lock mechanism to ensure
     * that only one thread can execute a task at a time. If the lock is already held,
     * a warning is logged, and a {@code CannotAcquireLockException} is thrown.
     *
     * @param <T> the type of the value supplied by the {@code Supplier}
     * @param supplier the {@code Supplier} to be executed; must not be null
     * @return the value produced by the {@code Supplier} if the lock is acquired successfully
     * @throws CannotAcquireLockException if the lock is already held
     */
    public <T> T execute(@NonNull Supplier<T> supplier) throws CannotAcquireLockException {
        if (locked.compareAndSet(false, true)) {
            try {
                return supplier.get();
            } finally {
                locked.set(false);
            }
        } else {
            log.warn(TRIED_TO_ACQUIRE_LOCK_WHILE_ALREADY_LOCKED);
            throw new CannotAcquireLockException(TRIED_TO_ACQUIRE_LOCK_WHILE_ALREADY_LOCKED);
        }
    }
}
