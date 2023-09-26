package com.randalloveson;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.nio.BufferOverflowException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static com.randalloveson.Type.*;
import static com.randalloveson.UnsafeAccess.UNSAFE;
import static java.time.Instant.now;

public class Benchmark {

    private static final long ALLOC = UNSAFE.allocateMemory(4096);

    private static final Type[] SCHEMA = {
            LONG,
            LONG,
            LONG,
            LONG,
            INT,
            LONG,
            DOUBLE,
            STRING,
            LONG,
            TIMESTAMP,
            TIMESTAMP,
            INT,
            LONG,
            DOUBLE,
            STRING,
            STRING,
            INT
    };

    private static byte[] u8(String s) {
        return s.getBytes(StandardCharsets.UTF_8);
    }

    private static final Object[][] ROWS = {
            { 2987L, 897L, 98L, 27863L, 9837, 983274L, 3928.837D, u8("str"), 873L, now(), now(), 3487, 19827L, 0D, u8(""), u8("a"), 0 },
            { 12860L, 10770L, 9971L, 37736L, 19710, 993147L, 13801.837D, u8("sfd"), 10746L, now(), now(), 13360, 29700L, 9873D, u8(""), u8("1"), 0 },
            { 90611L, 88521L, 87722L, 115487L, 97461, 1070898L, 91552.88461D, u8("his"), 88497L, now(), now(), 91111, 107451L, 87624D, u8(""), u8("3"), 0 },
            { -6886L, -8976L, -9775L, 17990L, -36, 973401L, -5945.837D, u8("ish"), -9000L, now(), now(), -6386, 9954L, -9873D, u8(""), u8("9"), 0 },
            { 3374L, 1284L, 485L, 28250L, 10611, 983661L, 4315.1224D, u8("iu"), 1260L, now(), now(), 3874, 20214L, 387D, u8(""), u8("b"), 387 },
            { 3860L, 1770L, 971L, 28736L, 10710, 984147L, 4801.1710D, u8(""), 1746L, now(), now(), 4360, 20700L, 873D, u8(""), u8("z"), 873 },
            { 2600L, 510L, -289L, 27476L, 9450, 982887L, 3928.837D, u8("sdiuh"), 486L, now(), now(), 3100, 19440L, 0D, u8(""), u8("x"), 0 },
            { 3374L, 1284L, 485L, 28250L, 10224, 983661L, 4315.1224D, u8("asoin"), 1260L, now(), now(), 3874, 20214L, 387D, u8(""), u8("k"), 387 },
            { 3074L, 984L, 185L, 27950L, 9924, 983361L, 4015.924D, u8("skh"), 960L, now(), now(), 3574, 19914L, 87D, u8(""), u8("s"), 87 },
            { 3374L, 1284L, 485L, 28250L, 10224, 983661L, 4315.1224D, u8("xnj"), 1260L, now(), now(), 3874, 20214L, 387D, u8(""), u8("y"), 387 },
            { 3374L, 1284L, 485L, 28250L, 10224, 983661L, 4315.1224D, u8("sin"), 1260L, now(), now(), 3874, 20214L, 387D, u8(""), u8("x"), 387 },
            { 3373L, 896L, 97L, 27862L, 9836, 983273L, 3927.836D, u8("ap"), 872L, now(), now(), 3486, 19826L, -1D, u8(""), u8("d"), -1 },
            { 101650L, 99273L, 98474L, 126239L, 108213, 1081650L, 102304.99213D, u8("3987"), 99249L, now(), now(), 101863, 118203L, 98376D, u8(""), u8("a"), 0 },
            { 2905L, 815L, 16L, 27781L, 9755, 983192L, 3846.755D, u8("cos"), 791L, now(), now(), 3405, 19745L, -82D, u8(""), u8("b"), -82 },
    };

    private static final Encoder ENCODER;
    static {
        Encoder encoder = SCHEMA[SCHEMA.length - 1].getEncoder();
        for (int i = SCHEMA.length - 2; i >= 0; i--) {
            encoder = new ComposedEncoder(SCHEMA[i].getEncoder(), encoder);
        }
        ENCODER = encoder;
    }

    static void encode(Object[] row, Cursor cursor) throws BufferOverflowException {
        ENCODER.encode(row, 0, cursor);
    }

    @org.openjdk.jmh.annotations.Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 0, jvmArgsAppend = {
            "-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining",
//            "-XX:+LogCompilation", "-XX:+PrintAssembly",
    })
    public void benchEncoderBaseline() {
        for (int i = 0; i < 3; i++) {
            Object[] row = ROWS[i];
            encode(row, new Cursor(ALLOC, ALLOC + 4096));
        }
    }

    private static final Encoder ENCODER_USING_COPIES;
    static {
        Encoder encoder = SCHEMA[SCHEMA.length - 1].getEncoder();
        for (int i = SCHEMA.length - 2; i >= 0; i--) {
            try {
                Class<?> encoderClass = Class.forName("com.randalloveson.ComposedEncoder_copy" + i);
                encoder = (Encoder) encoderClass.getConstructor(Encoder.class, Encoder.class)
                        .newInstance(SCHEMA[i].getEncoder(), encoder);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        ENCODER_USING_COPIES = encoder;
    }

    static void encodeUsingCopies(Object[] row, Cursor cursor) throws BufferOverflowException {
        ENCODER_USING_COPIES.encode(row, 0, cursor);
    }

    @org.openjdk.jmh.annotations.Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @BenchmarkMode(Mode.Throughput)
    @Fork(value = 1, warmups = 0, jvmArgsAppend = {
            "-XX:+UnlockDiagnosticVMOptions", "-XX:+PrintInlining",
//            "-XX:+LogCompilation", "-XX:+PrintAssembly",
    })
    public void benchEncoderUsingCopies() {
        for (int i = 0; i < 3; i++) {
            Object[] row = ROWS[i];
            encodeUsingCopies(row, new Cursor(ALLOC, ALLOC + 4096));
        }
    }

    public static void main(String[] args) {
        System.out.println("main");
    }
}
