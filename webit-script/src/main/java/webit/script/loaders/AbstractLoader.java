// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.loaders;

import jodd.io.FileNameUtil;
import jodd.util.StringUtil;

/**
 *
 * @author Zqq
 */
public abstract class AbstractLoader implements Loader {

    protected String encoding;
    protected String root = null;

    public String concat(String parent, String name) {
        if (parent != null) {
            String folder = FileNameUtil.getPath(parent);
            return FileNameUtil.concat(folder, name, true);
        } else {
            return name;
        }
    }

    protected String getRealPath(String name) {
        return root != null ? (root + name) : name.substring(1, name.length());
    }

    public String getRoot() {
        return root;
    }

    public String normalize(String name) {
        if (name == null) {
            return null;
        }
        name = StringUtil.prefix(name, "/");
        name = FileNameUtil.normalize(name, true);
        return name;
    }

    public void setRoot(String root) {
        root = FileNameUtil.normalizeNoEndSeparator(root, true);
        if (root != null && root.isEmpty()) {
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
}
