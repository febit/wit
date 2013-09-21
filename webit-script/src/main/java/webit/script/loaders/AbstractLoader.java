// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loaders;

import webit.script.Initable;
import webit.script.Engine;
import webit.script.util.StringUtil;
import webit.script.util.UnixStyleFileNameUtil;

/**
 *
 * @author Zqq
 */
public abstract class AbstractLoader implements Loader, Initable {

    protected String encoding;
    protected String root = null;

    public String concat(final String parent, final String name) {
        if (parent != null) {
            return UnixStyleFileNameUtil.concat(UnixStyleFileNameUtil.getPath(parent), name);
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
        return name != null ? UnixStyleFileNameUtil.normalize(StringUtil.prefixChar(name, '/')) : null;
    }

    public void setRoot(String root) {
        root = UnixStyleFileNameUtil.normalizeNoEndSeparator(root);
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
