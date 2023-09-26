package com.randalloveson;

import java.nio.BufferOverflowException;

import static com.randalloveson.UnsafeAccess.UNSAFE;

public enum IntEncoder implements Encoder {

    INSTANCE;

    @Override
    public void encode(Object[] row, int srcIndex, Cursor cursor) {
        if (cursor.limit - cursor.position < 4) {
            throw new BufferOverflowException();
        }
        UNSAFE.putInt(cursor.position, (Integer) row[srcIndex]);
        cursor.position += 4;
    }
}
