// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loaders;

import jodd.io.FileNameUtil;
import jodd.util.StringUtil;
import webit.script.Initable;
import webit.script.Engine;

/**
 *
 * @author Zqq
 */
public abstract class AbstractLoader implements Loader, Initable {

    protected String encoding;
    protected String root = null;

    public String concat(final String parent, final String name) {
        if (parent != null) {
            return FileNameUtil.concat(FileNameUtil.getPath(parent), name, true);
        } else {
            return name;
        }
    }

    protected String getRealPath(final String name) {
        return root != null ? (root + name) : name.substring(1, name.length());
    }

    public String getRoot() {
        return root;
    }

    public String normalize(String name) {
        return name != null ? FileNameUtil.normalize(StringUtil.prefix(name, "/"), true) : null;
    }

    public void setRoot(String root) {
        root = FileNameUtil.normalizeNoEndSeparator(root, true);
        if (root != null && root.length() == 0) {
            root = null;
        }
        this.root = root;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void init(Engine engine) {
        if (encoding == null) {
            encoding = engine.getEncoding();
        }
    }
}
