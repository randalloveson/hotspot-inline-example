package com.randalloveson;

public enum Type {
    LONG {
        @Override
        public Encoder getEncoder() {
            return LongEncoder.INSTANCE;
        }
    },
    TIMESTAMP {
        @Override
        public Encoder getEncoder() {
            return TimestampEncoder.INSTANCE;
        }
    },
    INT {
        @Override
        public Encoder getEncoder() {
            return IntEncoder.INSTANCE;
        }
    },
    DOUBLE {
        @Override
        public Encoder getEncoder() {
            return DoubleEncoder.INSTANCE;
        }
    },
    STRING {
        @Override
        public Encoder getEncoder() {
            return StringEncoder.INSTANCE;
        }
    };

    public abstract Encoder getEncoder();
}
