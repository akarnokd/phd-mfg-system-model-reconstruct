package com.github.akarnokd.msmr.util;

import java.nio.ByteBuffer;

import hu.akarnokd.utils.lang.Action0E;

public final class Util {
    private Util() { throw new IllegalStateException("No instances!"); }

    public static long measureTimeMillis(Runnable run) {
        long n = System.currentTimeMillis();
        run.run();
        return System.currentTimeMillis() - n;
    }
    public static long measureTimeNanos(Runnable run) {
        long n = System.nanoTime();
        run.run();
        return System.nanoTime() - n;
    }
    public static long measureTimeNanosIO(Action0E<Exception> action) {
        try {
            long n = System.nanoTime();
            action.call();
            return System.nanoTime() - n;
        } catch (Exception ex) {
            if (ex instanceof RuntimeException) {
                throw (RuntimeException)ex;
            }
            throw new RuntimeException(ex);
        }
    }
    public static void getChars(ByteBuffer buf, int offset, int maxLen, char[] out, int start, int len) {
        int n = Math.min(maxLen, len);
        for (int i = 0; i < n; i++) {
            out[start + i] = buf.getChar(offset + i * 2);
        }
    }
    public static void getAnsiChars(ByteBuffer buf, int offset, int maxLen, char[] out, int start, int len) {
        int n = Math.min(maxLen, len);
        for (int i = 0; i < n; i++) {
            out[start + i] = (char)buf.get(offset + i);
        }
    }}
