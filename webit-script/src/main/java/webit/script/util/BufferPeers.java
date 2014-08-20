// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.lang.ref.WeakReference;

/**
 *
 * @author Zqq
 */
public final class BufferPeers {

    private static final ThreadLocal<WeakReference<BufferPeers>> bufferPeersLocal = new ThreadLocal<WeakReference<BufferPeers>>();

    private char[] chars;
    private byte[] bytes;

    private BufferPeers(int size) {
        this.chars = new char[size];
        this.bytes = new byte[size];
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
            if ((value <<= 1) > (1 << 13)) {
                //MAX_SIZE 8192
                return length;
            }
        }
    }

    public static BufferPeers getNormalSizePeers() {
        return createBufferPeersIfAbsent(1 << 8); //DEFAULT_SIZE 256
    }

    public static BufferPeers getMiniSizePeers() {
        return createBufferPeersIfAbsent(1 << 5); //DEFAULT_MIN_SIZE 32
    }

    private static BufferPeers createBufferPeersIfAbsent(final int length) {
        WeakReference<BufferPeers> ref;
        BufferPeers peers;
        if ((ref = bufferPeersLocal.get()) == null
                || (peers = ref.get()) == null) {
            if (ref != null) {
                ref.clear();
            }
            bufferPeersLocal.set(new WeakReference<BufferPeers>(peers = new BufferPeers(length)));
        }
        return peers;
    }
}
