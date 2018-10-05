// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders.impl.resources;

import org.febit.wit.loaders.Resource;
import org.febit.wit.loaders.ResourceOffset;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * @author zqq90
 */
public class StringResource implements Resource, ResourceOffset {

    protected final String text;
    protected final boolean codeFirst;

    protected int offsetLine = 0;
    protected int offsetColumnOfFirstLine = 0;

    public StringResource(String text) {
        this(text, false);
    }

    /**
     * @since 2.0.0
     */
    public StringResource(String text, boolean codeFirst) {
        this.text = text;
        this.codeFirst = codeFirst;
    }

    @Override
    public Reader openReader() throws IOException {
        return new StringReader(this.text);
    }

    /**
     * @since 1.4.1
     */
    @Override
    public boolean exists() {
        return this.text != null;
    }

    /**
     * @since 1.5.0
     */
    public StringResource setOffset(int offsetLine, int offsetColumnOfFirstLine) {
        this.offsetLine = offsetLine;
        this.offsetColumnOfFirstLine = offsetColumnOfFirstLine;
        return this;
    }

    /**
     * @since 2.0.0
     */
    @Override
    public boolean isCodeFirst() {
        return codeFirst;
    }

    /**
     * @since 1.5.0
     */
    @Override
    public int getOffsetLine() {
        return offsetLine;
    }

    /**
     * @since 1.5.0
     */
    @Override
    public int getOffsetColumnOfFirstLine() {
        return offsetColumnOfFirstLine;
    }

    @Override
    public long version() {
        return 0L;
    }
}
