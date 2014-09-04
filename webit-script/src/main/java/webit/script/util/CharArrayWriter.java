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
        char[][] myBuffers = this.buffers;
        if (index == myBuffers.length) {
            System.arraycopy(myBuffers, 0, this.buffers = myBuffers = new char[index << 1][], 0, index);
        }
        offset = 0;
        return myBuffers[index] = currentBuffer = new char[Math.max(defaultBufferSize, newSize - size)];
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
    public CharArrayWriter append(char c) {
        write(c);
        return this;
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

    public void reset() {
        size = 0;
        offset = 0;
        currentBufferIndex = 0;
        currentBuffer = buffers[0];
    }

    public char[] toArray() {
        int pos = 0;
        final char[] array = new char[size];
        final int bufferIndex = this.currentBufferIndex;
        final char[][] myBuffers = this.buffers;
        for (int i = 0; i < bufferIndex; i++) {
            int len = myBuffers[i].length;
            System.arraycopy(myBuffers[i], 0, array, pos, len);
            pos += len;
        }
        System.arraycopy(myBuffers[bufferIndex], 0, array, pos, offset);
        return array;
    }

    public char[] toArraySkipIfLeftNewLine() {
        final int mySize = this.size;
        if (mySize == 0) {
            return new char[0];
        }
        final char[][] myBuffers = this.buffers;
        final int skip;
        char[] first = myBuffers[0];
        if (first[0] == '\n') {
            skip = 1;
        } else if (first[0] == '\r') {
            if (mySize > 1) {
                if (first[1] == '\n') {
                    if (mySize == 2) {
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
        final char[] array = new char[mySize - skip];
        final int bufferIndex = this.currentBufferIndex;
        if (bufferIndex == 0) {
            System.arraycopy(myBuffers[0], skip, array, 0, offset - skip);
        } else {
            int pos;
            System.arraycopy(myBuffers[0], skip, array, 0, pos = myBuffers[0].length - skip);
            for (int i = 1; i < bufferIndex; i++) {
                int len = myBuffers[i].length;
                System.arraycopy(myBuffers[i], 0, array, pos, len);
                pos += len;
            }
            System.arraycopy(myBuffers[bufferIndex], 0, array, pos, offset);
        }
        return array;
    }

    public void trimRightBlankToNewLine() {
        int tmpSize = this.size;
        boolean notLastBuffer = false;
        int bufferIndex = this.currentBufferIndex;
        for (; bufferIndex >= 0; bufferIndex--) {
            final char[] buffer = buffers[bufferIndex];
            final int currentBuffeValidSize;
            if (notLastBuffer) {
                currentBuffeValidSize = buffer.length;
            } else {
                currentBuffeValidSize = this.offset;
                notLastBuffer = true;
            }
            int pos = currentBuffeValidSize - 1;
            char c;
            while (pos >= 0) {
                c = buffer[pos];
                if (c != ' ' && c != '\t') {
                    break;
                }
                pos--;
            }
            if (pos < 0) {
                //All blank
                tmpSize -= currentBuffeValidSize;
                continue;
            }
            c = buffer[pos];
            if (c == '\n' || c == '\r') {
                //do trim
                this.offset = pos + 1;
                this.size = tmpSize - currentBuffeValidSize + pos + 1;
                this.currentBufferIndex = bufferIndex;
                this.currentBuffer = buffer;
            }
            //else not need to trim
            return;
        }
    }

    public int size() {
        return this.size;
    }

    @Override
    public void close() {
    }

    @Override
    public void flush() {
    }

    @Override
    public String toString() {
        return new String(toArray());
    }
}
