// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.lang.ref.SoftReference;

/**
 *
 * @author Zqq
 */
public final class BufferPeers {

    private final static ThreadLocal<SoftReference<BufferPeers>> bufferPeersLocal = new ThreadLocal<SoftReference<BufferPeers>>();

    public final static int DEFAULT_SIZE = 1 << 8; //256
    public final static int DEFAULT_MIN_SIZE = 1 << 5; //32
    private char[] chars;
    private byte[] bytes;
    public final static int MAX_SIZE = 1 << 13; //8192

    private BufferPeers() {
        this(DEFAULT_SIZE, DEFAULT_SIZE);
    }

    private BufferPeers(int size) {
        this(size, size);
    }

    private BufferPeers(int charArraySize, int byteArraySize) {
        this.chars = new char[charArraySize];
        this.bytes = new byte[byteArraySize];
    }

    public char[] getChars(int len) {
        char[] buf;
        return (buf = chars).length >= len ? buf : (this.chars = new char[getAllocateLength(buf.length, len)]);
    }

    public byte[] getBytes(int len) {
        byte[] buf;
        return (buf = bytes).length >= len ? buf : (this.bytes = new byte[getAllocateLength(buf.length, len)]);
    }

    private static int getAllocateLength(final int init, final int length) {
        int value = init;
        for (;;) {
            if (value >= length) {
                return value;
            }
            if ((value <<= 1) > MAX_SIZE) {
                return length;
            }
        }
    }

    public static BufferPeers getNormalSizePeers() {
        return createBufferPeersIfAbsent(DEFAULT_SIZE);
    }

    public static BufferPeers getMiniSizePeers() {
        return createBufferPeersIfAbsent(DEFAULT_MIN_SIZE);
    }

    private static BufferPeers createBufferPeersIfAbsent(final int length) {
        SoftReference<BufferPeers> ref;
        BufferPeers peers;
        if ((ref = bufferPeersLocal.get()) == null
                || (peers = ref.get()) == null) {
            bufferPeersLocal.set(new SoftReference<BufferPeers>(peers = new BufferPeers(length)));
        }
        return peers;
    }
}
