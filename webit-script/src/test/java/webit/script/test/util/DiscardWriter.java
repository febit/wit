// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.test.util;

import java.io.IOException;
import java.io.Writer;

public class DiscardWriter extends Writer {

    @Override
    public Writer append(char c) throws IOException {
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end) throws IOException {
        return this;
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        return this;
    }

    @Override
    public void write(char[] cbuf) throws IOException {
    }

    @Override
    public void write(int c) throws IOException {
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
    }

    @Override
    public void write(String str) throws IOException {
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
