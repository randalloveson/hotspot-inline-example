package com.randalloveson;

import java.nio.BufferOverflowException;

public interface Encoder {

    void encode(Object[] row, int srcIndex, Cursor cursor) throws BufferOverflowException;

    default void encode(Object[] row, Cursor cursor) throws BufferOverflowException {
        encode(row, 0, cursor);
    }
}
