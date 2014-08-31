// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.
package webit.script.util;

import java.io.OutputStream;

public final class ByteArrayOutputStream extends OutputStream {

    private byte[][] buffers = new byte[16][];
    private int currentBufferIndex = -1;
    private byte[] currentBuffer;
    private int offset;
    private int size;
    private final int minChunkLen;

    public ByteArrayOutputStream() {
        this.minChunkLen = 1024;
    }

    public ByteArrayOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        this.minChunkLen = size;
    }

    /**
     * Prepares next chunk to match new size. The minimal length of new chunk is
     * <code>minChunkLen</code>.
     */
    private void needNewBuffer(int newSize) {

        if (currentBufferIndex >= buffers.length) {
            int newLen = buffers.length << 1;
            byte[][] newBuffers = new byte[newLen][];
            System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
            buffers = newBuffers;
        }
        currentBufferIndex++;
        offset = 0;
        buffers[currentBufferIndex] = currentBuffer = new byte[Math.max(minChunkLen, newSize - size)];
    }

    @Override
    public void write(byte[] array, int off, int len) {
        int end = off + len;
        if ((off < 0)
                || (len < 0)
                || (end > array.length)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return;
        }
        int newSize = size + len;
        int remaining = len;

        if (currentBuffer != null) {
            // first try to fill current buffer
            int part = Math.min(remaining, currentBuffer.length - offset);
            System.arraycopy(array, end - remaining, currentBuffer, offset, part);
            remaining -= part;
            offset += part;
            size += part;
        }

        if (remaining > 0) {
            // still some data left
            // ask for new buffer
            needNewBuffer(newSize);
            // then copy remaining
            // but this time we are sure that it will fit
            int part = Math.min(remaining, currentBuffer.length - offset);
            System.arraycopy(array, end - remaining, currentBuffer, offset, part);
            offset += part;
            size += part;
        }
    }

    @Override
    public void write(int b) {
        if ((currentBuffer == null) || (offset == currentBuffer.length)) {
            needNewBuffer(size + 1);
        }

        currentBuffer[offset] = (byte) b;
        offset++;
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

    /**
     * Resets the buffer content.
     */
    public void reset() {
        size = 0;
        offset = 0;
        currentBufferIndex = -1;
        currentBuffer = null;
    }

    public byte[] toArray() {
        int pos = 0;
        byte[] array = new byte[size];

        if (currentBufferIndex == -1) {
            return array;
        }

        for (int i = 0; i < currentBufferIndex; i++) {
            int len = buffers[i].length;
            System.arraycopy(buffers[i], 0, array, pos, len);
            pos += len;
        }

        System.arraycopy(buffers[currentBufferIndex], 0, array, pos, offset);

        return array;
    }
}
