// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl.resources;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import webit.script.loaders.Resource;
import webit.script.loaders.ResourceOffset;

/**
 *
 * @author Zqq
 */
public class StringResource implements Resource, ResourceOffset {

    private final String text;

    private int offsetLine = 0;
    private int offsetColumnOfFirstLine = 0;

    public StringResource(String text) {
        this.text = text;
    }

    @Override
    public boolean isModified() {
        return false;
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
}
