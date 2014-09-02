// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.io.OutputStream;

public final class ByteArrayOutputStream extends OutputStream {

    private final int defaultBufferSize;
    private byte[][] buffers;
    private byte[] currentBuffer;
    private int currentBufferIndex;
    private int offset;
    private int size;

    public ByteArrayOutputStream() {
        this(256);
    }

    public ByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        this.defaultBufferSize = size;
        this.buffers = new byte[16][];
        this.currentBufferIndex = 0;
        this.buffers[0] = this.currentBuffer = new byte[size];
    }

    private byte[] needNewBuffer(int newSize) {
        final int index = ++currentBufferIndex;
        byte[][] myBuffers = this.buffers;
        if (index == myBuffers.length) {
            System.arraycopy(myBuffers, 0, this.buffers = myBuffers = new byte[index << 1][], 0, index);
        }
        offset = 0;
        return myBuffers[index] = currentBuffer = new byte[Math.max(defaultBufferSize, newSize - size)];
    }

    @Override
    public void write(byte[] array, int off, int len) {
        if (len == 0) {
            return;
        }
        int end = off + len;
        if ((off < 0) || (len < 0) || (end > array.length)) {
            throw new IndexOutOfBoundsException();
        }
        int part;

        byte[] buffer = currentBuffer;
        part = Math.min(len, buffer.length - offset);
        System.arraycopy(array, off, buffer, offset, part);
        offset += part;
        size += part;

        int remaining = len - part;
        if (remaining > 0) {
            buffer = needNewBuffer(size + remaining);
            //assert offset = 0
            part = Math.min(remaining, buffer.length);
            System.arraycopy(array, end - remaining, buffer, 0, part);
            offset = part;
            size += part;
        }
    }

    @Override
    public void write(int b) {
        byte[] buffer = currentBuffer;
        if (offset == buffer.length) {
            buffer = needNewBuffer(size + 1);
        }
        buffer[offset++] = (byte) b;
        size++;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    public int size() {
        return size;
    }

    public void reset() {
        size = 0;
        offset = 0;
        currentBufferIndex = 0;
        currentBuffer = buffers[0];
    }

    public byte[] toArray() {
        int pos = 0;
        final byte[] array = new byte[size];
        final int bufferIndex = this.currentBufferIndex;
        final byte[][] myBuffers = this.buffers;
        for (int i = 0; i < bufferIndex; i++) {
            int len = myBuffers[i].length;
            System.arraycopy(myBuffers[i], 0, array, pos, len);
            pos += len;
        }
        System.arraycopy(myBuffers[bufferIndex], 0, array, pos, offset);
        return array;
    }
}
