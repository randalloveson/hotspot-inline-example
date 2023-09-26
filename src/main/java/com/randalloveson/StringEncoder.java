package com.randalloveson;

import sun.misc.Unsafe;

import java.nio.BufferOverflowException;

import static com.randalloveson.UnsafeAccess.UNSAFE;

public enum StringEncoder implements Encoder {

    INSTANCE;

    @Override
    public void encode(Object[] row, int srcIndex, Cursor cursor) {
        byte[] string = ((byte[]) row[srcIndex]);
        if (cursor.limit - cursor.position < 4 + string.length) {
            throw new BufferOverflowException();
        }
        UNSAFE.putInt(cursor.position, string.length);
        UNSAFE.copyMemory(string, Unsafe.ARRAY_BYTE_BASE_OFFSET, null, cursor.position + 4, string.length);
        cursor.position += 4 + string.length;
    }
}
