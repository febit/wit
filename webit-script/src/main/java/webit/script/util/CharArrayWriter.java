// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import java.io.Writer;

public class CharArrayWriter extends Writer {

    private final int defaultBufferSize;
    private char[][] buffers;
    private char[] currentBuffer;
    int currentBufferIndex;
    int offset;
    private int size;

    public CharArrayWriter() {
        this(256);
    }

    public CharArrayWriter(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        this.defaultBufferSize = size;
        this.buffers = new char[16][];
        this.currentBufferIndex = 0;
        this.buffers[0] = this.currentBuffer = new char[size];
    }

    private char[] needNewBuffer(int newSize) {
        final int index = ++currentBufferIndex;
        char[][] buffers = this.buffers;
        if (index == buffers.length) {
            System.arraycopy(buffers, 0, this.buffers = buffers = new char[index << 1][], 0, index);
        }
        offset = 0;
        return buffers[index] = currentBuffer = new char[Math.max(defaultBufferSize, newSize - size)];
    }

    @Override
    public void write(char[] array, int off, int len) {
        if (len == 0) {
            return;
        }
        int end = off + len;
        if ((off < 0) || (len < 0) || (end > array.length)) {
            throw new IndexOutOfBoundsException();
        }
        int part;

        char[] buffer = currentBuffer;
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
        char[] buffer = currentBuffer;
        if (offset == buffer.length) {
            buffer = needNewBuffer(size + 1);
        }
        buffer[offset++] = (char) b;
        size++;
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

    public CharArrayWriter append(char[] array, int off, int len) {
        write(array, off, len);
        return this;
    }

    public CharArrayWriter append(char[] array) {
        return append(array, 0, array.length);
    }

    public CharArrayWriter append(String array) {
        return append(array.toCharArray());
    }

    @Override
    public CharArrayWriter append(char c) {
        write(c);
        return this;
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

    public char[] toArray() {
        int pos = 0;
        final char[] array = new char[size];
        final int currentBufferIndex = this.currentBufferIndex;
        final char[][] buffers = this.buffers;
        for (int i = 0; i < currentBufferIndex; i++) {
            int len = buffers[i].length;
            System.arraycopy(buffers[i], 0, array, pos, len);
            pos += len;
        }
        System.arraycopy(buffers[currentBufferIndex], 0, array, pos, offset);
        return array;
    }

    public char[] toArraySkipIfLeftNewLine() {
        final int size = this.size;
        if (size == 0) {
            return new char[0];
        }
        final char[][] buffers = this.buffers;
        final int skip;
        char[] first = buffers[0];
        if (first[0] == '\n') {
            skip = 1;
        } else if (first[0] == '\r') {
            if (size > 1) {
                if (first[1] == '\n') {
                    if (size == 2) {
                        return new char[0];
                    }
                    skip = 2;
                } else {
                    skip = 1;
                }
            } else {
                return new char[0];
            }
        } else {
            return toArray();
        }
        int pos = 0;
        final char[] array = new char[size - skip];
        final int currentBufferIndex = this.currentBufferIndex;
        if (currentBufferIndex == 0) {
            System.arraycopy(buffers[0], skip, array, 0, offset - skip);
        } else {
            System.arraycopy(buffers[0], skip, array, 0, pos = buffers[0].length - skip);
            for (int i = 1; i < currentBufferIndex; i++) {
                int len = buffers[i].length;
                System.arraycopy(buffers[i], 0, array, pos, len);
                pos += len;
            }
            System.arraycopy(buffers[currentBufferIndex], 0, array, pos, offset);
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
            int pos = lastNotWhitespaceOrNewLine(tmp_buf, 0, tmp_offset);
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

    private static int lastNotWhitespaceOrNewLine(final char[] buf, final int from, final int end) {
        int pos;

        for (pos = end - 1; pos >= from; pos--) {
            switch (buf[pos]) {
                case ' ':
                case '\t':
                case '\b':
                case '\f':
                    continue;
                default:
                    // not a blank line
                    return pos;
            }
        }
        return pos;
    }
}
