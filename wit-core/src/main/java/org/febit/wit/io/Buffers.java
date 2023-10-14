// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.io;

import java.lang.ref.WeakReference;

/**
 * @author zqq90
 */
public final class Buffers {

    private static final ThreadLocal<WeakReference<Buffers>> CACHE = new ThreadLocal<>();

    private char[] chars;
    private byte[] bytes;

    private Buffers(int size) {
        this.chars = new char[size];
        this.bytes = new byte[size];
    }

    public char[] getChars(final int len) {
        var buf = chars;
        return buf.length >= len ? buf : upgradeChars(len);
    }

    @SuppressWarnings({
            "squid:AssignmentInSubExpressionCheck"
    })
    private char[] upgradeChars(final int len) {
        return this.chars = new char[getAllocateLength(this.chars.length, len)];
    }

    public byte[] getBytes(final int len) {
        var buf = bytes;
        return buf.length >= len ? buf : upgradeBytes(len);
    }

    @SuppressWarnings({
            "squid:AssignmentInSubExpressionCheck"
    })
    private byte[] upgradeBytes(final int len) {
        return this.bytes = new byte[getAllocateLength(this.bytes.length, len)];
    }

    private static int getAllocateLength(final int init, final int length) {
        int value = init;
        for (; ; ) {
            if (value >= length) {
                return value;
            }
            value <<= 1;
            if (value > (1 << 13)) {
                // MAX_SIZE 8192
                return length;
            }
        }
    }

    public static Buffers getNormalPeers() {
        return createIfAbsent(1 << 8); //DEFAULT_SIZE 256
    }

    public static Buffers getMiniPeers() {
        return createIfAbsent(1 << 5); //DEFAULT_MIN_SIZE 32
    }

    private static Buffers createIfAbsent(final int length) {
        Buffers peers;
        var ref = CACHE.get();
        if (ref == null
                || (peers = ref.get()) == null) {
            if (ref != null) {
                ref.clear();
            }
            peers = new Buffers(length);
            CACHE.set(new WeakReference<>(peers));
        }
        return peers;
    }
}
