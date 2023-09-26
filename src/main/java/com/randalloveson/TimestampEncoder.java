package com.randalloveson;

import java.nio.BufferOverflowException;
import java.time.Instant;

import static com.randalloveson.UnsafeAccess.UNSAFE;

public enum TimestampEncoder implements Encoder {

    INSTANCE;

    @Override
    public void encode(Object[] row, int srcIndex, Cursor cursor) {
        if (cursor.limit - cursor.position < 12) {
            throw new BufferOverflowException();
        }
        Instant obj = (Instant) row[srcIndex];
        UNSAFE.putLong(cursor.position, obj.getEpochSecond());
        UNSAFE.putInt(cursor.position + 8, obj.getNano());
        cursor.position += 12;
    }
}
