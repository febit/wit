// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.
package webit.script.util;

import java.io.Writer;

public class CharArrayWriter extends Writer {

    private char[][] buffers = new char[16][];
    private char[] currentBuffer;
    protected int currentBufferIndex = -1;
    protected int offset;
    private int size;
    private final int minChunkLen;

    /**
     * Creates a new <code>char</code> buffer. The buffer capacity is initially
     * 1024 bytes, though its size increases if necessary.
     */
    public CharArrayWriter() {
        this.minChunkLen = 1024;
    }

    /**
     * Creates a new <code>char</code> buffer, with a buffer capacity of the
     * specified size, in bytes.
     *
     * @param size the initial size.
     * @throws IllegalArgumentException if size is negative.
     */
    public CharArrayWriter(int size) {
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
        int delta = newSize - size;
        int newBufferSize = Math.max(minChunkLen, delta);

        currentBufferIndex++;
        currentBuffer = new char[newBufferSize];
        offset = 0;

        // add buffer
        if (currentBufferIndex >= buffers.length) {
            int newLen = buffers.length << 1;
            char[][] newBuffers = new char[newLen][];
            System.arraycopy(buffers, 0, newBuffers, 0, buffers.length);
            buffers = newBuffers;
        }
        buffers[currentBufferIndex] = currentBuffer;
    }

    public CharArrayWriter append(char[] array, int off, int len) {
        int end = off + len;
        if ((off < 0)
                || (len < 0)
                || (end > array.length)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return this;
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

        return this;
    }

    @Override
    public void write(char[] b, int off, int len) {
        append(b, off, len);
    }

    @Override
    public void write(int b) {
        append((char) b);
    }

    @Override
    public void write(String s, int off, int len) {
        write(s.toCharArray(), off, len);
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    public CharArrayWriter append(char[] array) {
        return append(array, 0, array.length);
    }

    @Override
    public CharArrayWriter append(char element) {
        if ((currentBuffer == null) || (offset == currentBuffer.length)) {
            needNewBuffer(size + 1);
        }

        currentBuffer[offset] = element;
        offset++;
        size++;

        return this;
    }

    public int size() {
        return size;
    }

    public void reset() {
        size = 0;
        offset = 0;
        currentBufferIndex = -1;
        currentBuffer = null;
    }

    public char[] toArray() {
        int pos = 0;
        char[] array = new char[size];

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

    public char[] toArraySkipIfLeftNewLine() {
        if (this.size == 0) {
            return new char[0];
        }
        final int skip;
        char[] first = buffers[0];
        if (first[0] == '\n') {
            skip = 1;
        } else if (first[0] == '\r') {
            if (this.size > 1) {
                if (first[1] == '\n') {
                    skip = 2;
                } else {
                    skip = 1;
                }
            } else {
                return new char[0];
            }
        } else {
            skip = 0;
        }
        if (skip == 0) {
            return toArray();
        }
        int remaining = this.size - skip;
        int pos = 0;
        char[] array = new char[remaining];
        //first
        int c = Math.min(first.length - skip, remaining);
        System.arraycopy(first, skip, array, pos, c);
        pos += c;
        remaining -= c;
        for (int i = 1; remaining > 0 && i < buffers.length;) {
            char[] buf = buffers[i++];
            c = Math.min(buf.length, remaining);
            System.arraycopy(buf, 0, array, pos, c);
            pos += c;
            remaining -= c;
        }
        return array;
    }

    public void trimRightBlankToNewLine() {
        int tmp_offset;
        int tmp_count = this.size;
        char[] tmp_buf; // = this.currentBuffer;
        int tmp_currentBufferIndex = this.currentBufferIndex;
        boolean notLastOne = false;
        for (; tmp_currentBufferIndex >= 0; tmp_currentBufferIndex--) {
            tmp_buf = buffers[tmp_currentBufferIndex];
            if (notLastOne) {
                tmp_offset = tmp_buf.length;
            } else {
                tmp_offset = this.offset;
                notLastOne = true;
            }
            int pos = CharUtil.lastNotWhitespaceOrNewLine(tmp_buf, 0, tmp_offset);
            if (pos < 0) {
                //All blank
                tmp_count -= tmp_offset;
            } else if (tmp_buf[pos] == '\n' || tmp_buf[pos] == '\r') {
                this.size = tmp_count - tmp_offset + pos + 1;
                offset = pos + 1;
                currentBufferIndex = tmp_currentBufferIndex;
                currentBuffer = tmp_buf;
                return;
            } else {
                //Not new Line
                break;
            }
        }
    }

    @Override
    public String toString() {
        return new String(toArray());
    }
}
