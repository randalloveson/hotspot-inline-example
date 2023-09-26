package com.randalloveson;

import java.nio.BufferOverflowException;

import static com.randalloveson.UnsafeAccess.UNSAFE;

public enum LongEncoder implements Encoder {

    INSTANCE;

    @Override
    public void encode(Object[] row, int srcIndex, Cursor cursor) {
        if (cursor.limit - cursor.position < 8) {
            throw new BufferOverflowException();
        }
        UNSAFE.putLong(cursor.position, (Long) row[srcIndex]);
        cursor.position += 8;
    }
}
