package com.randalloveson;

public class ComposedEncoder_copy11 implements Encoder {

    private final Encoder encoder;
    private final Encoder next;

    public ComposedEncoder_copy11(Encoder encoder, Encoder next) {
        this.encoder = encoder;
        this.next = next;
    }

    @Override
    public void encode(Object[] row, int srcIndex, Cursor cursor) {
        encoder.encode(row, srcIndex, cursor);
        next.encode(row, srcIndex + 1, cursor);
    }
}
