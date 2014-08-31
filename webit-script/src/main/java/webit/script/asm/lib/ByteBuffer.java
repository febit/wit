package webit.script.asm.lib;

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
        int length = this.length;
        ensure(length + 1);
        data[length] = (byte) b;
        this.length = length + 1;
        return this;
    }

    ByteBuffer putBB(final int b1, final int b2) {
        int length = this.length;
        ensure(length + 2);
        byte[] data = this.data;
        data[length] = (byte) b1;
        data[length + 1] = (byte) b2;
        this.length = length + 2;
        return this;
    }

    ByteBuffer putBS(final int b, final int s) {
        int length = this.length;
        ensure(length + 3);
        byte[] data = this.data;
        data[length] = (byte) b;
        data[length + 1] = (byte) (s >>> 8);
        data[length + 2] = (byte) s;
        this.length = length + 3;
        return this;
    }

    ByteBuffer putShort(final int s) {
        int length = this.length;
        ensure(length + 2);
        byte[] data = this.data;
        data[length] = (byte) (s >>> 8);
        data[length + 1] = (byte) s;
        this.length = length + 2;
        return this;
    }

    ByteBuffer putInt(final int i) {
        int length = this.length;
        ensure(length + 4);
        byte[] data = this.data;
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        this.length = length;
        return this;
    }

    ByteBuffer putLong(final long l) {
        int length = this.length;
        ensure(length + 8);
        byte[] data = this.data;
        int i = (int) (l >>> 32);
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        i = (int) l;
        data[length++] = (byte) (i >>> 24);
        data[length++] = (byte) (i >>> 16);
        data[length++] = (byte) (i >>> 8);
        data[length++] = (byte) i;
        this.length = length;
        return this;
    }

    ByteBuffer putUTF8(final String s) {
        int charLength = s.length();
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
        int length = this.length;
        ensure(length + 2 + byteLength);
        byte[] data = this.data;
        data[length++] = (byte) (byteLength >>> 8);
        data[length++] = (byte) (byteLength);
        for (int i = 0; i < charLength; ++i) {
            char c = s.charAt(i);
            if (c >= '\001' && c <= '\177') {
                data[length++] = (byte) c;
            } else if (c > '\u07FF') {
                data[length++] = (byte) (0xE0 | c >> 12 & 0xF);
                data[length++] = (byte) (0x80 | c >> 6 & 0x3F);
                data[length++] = (byte) (0x80 | c & 0x3F);
            } else {
                data[length++] = (byte) (0xC0 | c >> 6 & 0x1F);
                data[length++] = (byte) (0x80 | c & 0x3F);
            }
        }
        this.length = length;
        return this;
    }

    ByteBuffer put(final byte[] b, final int off, final int len) {
        ensure(length + len);
        if (b != null) {
            System.arraycopy(b, off, data, length, len);
        }
        length += len;
        return this;
    }

    ByteBuffer put(final ByteBuffer buffer) {
        return put(buffer.data, 0, buffer.length);
    }

    private void ensure(final int size) {
        if (size > data.length) {
            int len = data.length << 1;
            System.arraycopy(data, 0, data = new byte[len > size ? len : size], 0, length);
        }
    }
}
