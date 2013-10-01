// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.io.charset.impl;

import java.lang.ref.SoftReference;

public class ThreadLocalCache {

    public final static int CACH_MIN_LEN = 32;
    public final static int CHARS_CACH_INIT_SIZE = 256;
    public final static int CHARS_CACH_MAX_SIZE = 1024 * 2;
    private final static ThreadLocal<SoftReference<char[]>> charsBufLocal = new ThreadLocal<SoftReference<char[]>>();

    public static void clearChars() {
        charsBufLocal.set(null);
    }

    public static char[] getChars(final int length) {
        SoftReference<char[]> ref;

        if ((ref = charsBufLocal.get()) == null) {
            return allocate(length);
        }

        char[] chars;

        if ((chars = ref.get()) == null) {
            return allocate(length);
        }

        if (chars.length < length) {
            chars = allocate(length);
        }

        return chars;
    }

    private static char[] allocate(final int length) {
        int allocateLength;

        if ((allocateLength = getAllocateLength(CHARS_CACH_INIT_SIZE, CHARS_CACH_MAX_SIZE, length)) <= CHARS_CACH_MAX_SIZE) {
            char[] chars;
            charsBufLocal.set(new SoftReference<char[]>(chars = new char[allocateLength]));
            return chars;
        }

        return new char[length];
    }

    private static int getAllocateLength(final int init, final int max, final int length) {
        int value = init;
        for (;;) {
            if (value >= length) {
                return value;
            }

            value *= 2;

            if (value > max) {
                break;
            }
        }

        return length;
    }
    // /////////
    public final static int BYTES_CACH_INIT_SIZE = 256;
    public final static int BYTeS_CACH_MAX_SIZE = 1024 * 3;
    private final static ThreadLocal<SoftReference<byte[]>> bytesBufLocal = new ThreadLocal<SoftReference<byte[]>>();

    public static void clearBytes() {
        bytesBufLocal.set(null);
    }

    public static byte[] getBytes(final int length) {
        SoftReference<byte[]> ref;

        if ((ref = bytesBufLocal.get()) == null) {
            return allocateBytes(length);
        }

        byte[] bytes;

        if ((bytes = ref.get()) == null) {
            return allocateBytes(length);
        }

        if (bytes.length < length) {
            bytes = allocateBytes(length);
        }

        return bytes;
    }

    private static byte[] allocateBytes(final int length) {
        int allocateLength;

        if ((allocateLength = getAllocateLength(CHARS_CACH_INIT_SIZE, CHARS_CACH_MAX_SIZE, length)) <= CHARS_CACH_MAX_SIZE) {
            byte[] bytes;
            bytesBufLocal.set(new SoftReference<byte[]>(bytes = new byte[allocateLength]));
            return bytes;
        }

        return new byte[length];
    }
}
