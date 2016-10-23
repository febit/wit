package org.febit.wit_shaded.asm;

/**
 * bytes buffer.
 */
final class ByteBuffer {

    byte[] data;
    int length;

    ByteBuffer() {
        data = new byte[64];
    }

    ByteBuffer(final int initialSize) {
        data = new byte[initialSize];
    }

    ByteBuffer putByte(final int b) {
        int index = this.length;
        ensure(index + 1);
        data[index] = (byte) b;
        this.length = index + 1;
        return this;
    }

    ByteBuffer putBB(final int b1, final int b2) {
        int index = this.length;
        ensure(index + 2);
        final byte[] data = this.data;
        data[index] = (byte) b1;
        data[index + 1] = (byte) b2;
        this.length = index + 2;
        return this;
    }

    ByteBuffer putBS(final int b, final int s) {
        int index = this.length;
        ensure(index + 3);
        final byte[] data = this.data;
        data[index] = (byte) b;
        data[index + 1] = (byte) (s >>> 8);
        data[index + 2] = (byte) s;
        this.length = index + 3;
        return this;
    }

    ByteBuffer putShort(final int s) {
        int index = this.length;
        ensure(index + 2);
        final byte[] data = this.data;
        data[index] = (byte) (s >>> 8);
        data[index + 1] = (byte) s;
        this.length = index + 2;
        return this;
    }

    ByteBuffer putInt(final int i) {
        int index = this.length;
        ensure(index + 4);
        final byte[] data = this.data;
        data[index++] = (byte) (i >>> 24);
        data[index++] = (byte) (i >>> 16);
        data[index++] = (byte) (i >>> 8);
        data[index++] = (byte) i;
        this.length = index;
        return this;
    }

    ByteBuffer putLong(final long l) {
        int index = this.length;
        ensure(index + 8);
        final byte[] data = this.data;
        int i = (int) (l >>> 32);
        data[index++] = (byte) (i >>> 24);
        data[index++] = (byte) (i >>> 16);
        data[index++] = (byte) (i >>> 8);
        data[index++] = (byte) i;
        i = (int) l;
        data[index++] = (byte) (i >>> 24);
        data[index++] = (byte) (i >>> 16);
        data[index++] = (byte) (i >>> 8);
        data[index++] = (byte) i;
        this.length = index;
        return this;
    }

    ByteBuffer putUTF8(final String s) {
        final int charLength = s.length();
        int byteLength = 0;
        for (int i = 0; i < charLength; ++i) {
            char c = s.charAt(i);
            if (c >= '\001' && c <= '\177') {
                byteLength++;
            } else if (c > '\u07FF') {
                byteLength += 3;
            } else {
                byteLength += 2;
            }
        }
        if (byteLength > 65535) {
            throw new IllegalArgumentException();
        }
        int index = this.length;
        ensure(index + 2 + byteLength);
        final byte[] data = this.data;
        data[index++] = (byte) (byteLength >>> 8);
        data[index++] = (byte) (byteLength);
        for (int i = 0; i < charLength; i++) {
            char c = s.charAt(i);
            if (c >= '\001' && c <= '\177') {
                data[index++] = (byte) c;
                continue;
            }
            if (c > '\u07FF') {
                data[index++] = (byte) (0xE0 | c >> 12 & 0xF);
                data[index++] = (byte) (0x80 | c >> 6 & 0x3F);
                data[index++] = (byte) (0x80 | c & 0x3F);
                continue;
            }
            data[index++] = (byte) (0xC0 | c >> 6 & 0x1F);
            data[index++] = (byte) (0x80 | c & 0x3F);
        }
        this.length = index;
        return this;
    }

//    ByteBuffer put(final byte[] b, final int off, final int len) {
//        ensure(length + len);
//        if (b != null) {
//            System.arraycopy(b, off, data, length, len);
//        }
//        length += len;
//        return this;
//    }
    ByteBuffer put(final ByteBuffer buffer) {
        int len = buffer.length;
        ensure(length + len);
        System.arraycopy(buffer.data, 0, data, length, len);
        length += len;
        return this;
    }

    private void ensure(final int size) {
        if (size > data.length) {
            int len = data.length << 1;
            System.arraycopy(data, 0, data = new byte[len > size ? len : size], 0, length);
        }
    }
}
