// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders.impl.resources;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import webit.script.loaders.Resource;

/**
 *
 * @author Zqq
 */
public class StringResource implements Resource {

    private final String text;

    public StringResource(String text) {
        this.text = text;
    }

    public boolean isModified() {
        return false;
    }

    public Reader openReader() throws IOException {
        return new StringReader(this.text);
    }

    /**
     * @since 1.4.1
     */
    public boolean exists() {
        return this.text != null;
    }
}
