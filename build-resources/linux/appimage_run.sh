#!/bin/sh
# This script ensures that java will follow HOME and TMPDIR env
# unless instructed otherwise (by setting JAVA_FOLLOW_ENV to 0)

if [ -z "$JAVA_FOLLOW_ENV" ]; then
    JAVA_FOLLOW_ENV=1
fi

if [ "$JAVA_FOLLOW_ENV" = "1" ]; then
    if [ -n "$HOME" ] && [ -d "$HOME" ]; then
        echo "Replacing user.home with: $HOME"
        _JAVA_OPTIONS="$_JAVA_OPTIONS -Duser.home=\"$HOME\""
    fi

    if [ -n "$TMPDIR" ] && [ -d "$TMPDIR" ]; then
        echo "Replacing java.io.tmpdir with: $TMPDIR"
        _JAVA_OPTIONS="$_JAVA_OPTIONS -Djava.io.tmpdir=\"$TMPDIR\""
    fi
fi

if [ -n "$_JAVA_OPTIONS" ]; then
    export "_JAVA_OPTIONS"
fi

DIR="$(dirname "$0")"

exec "$DIR/bin/StructuredFileViewer" "$@"