// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loaders;

import webit.script.Engine;
import webit.script.Initable;
import webit.script.util.StringUtil;
import webit.script.util.UnixStyleFileNameUtil;

/**
 *
 * @author Zqq
 */
public abstract class AbstractLoader implements Loader, Initable {

    protected String encoding;
    protected String root = null;
    protected boolean dontAppendLostFileNameExtension;
    protected String fileNameExtension;
    //
    private boolean dontAppendLostFileNameExtensionSettedFlag = false;

    public String concat(final String parent, final String name) {
        return parent != null ? UnixStyleFileNameUtil.concat(UnixStyleFileNameUtil.getPath(parent), name) : name;
    }

    protected String getRealPath(final String name) {
        return root != null ? (root.concat(name)) : name.substring(1, name.length());
    }

    public String normalize(String name) {
        if (name != null) {
            final String newName;
            newName = UnixStyleFileNameUtil.normalize(StringUtil.prefixChar(name, '/'));
            if (dontAppendLostFileNameExtension || newName.charAt(newName.length() - 1) == '/' || newName.endsWith(fileNameExtension)) {
                return newName;
            } else {
                return newName.concat(fileNameExtension);
            }
        }
        return null;
    }

    public void setRoot(String root) {
        root = UnixStyleFileNameUtil.normalizeNoEndSeparator(root);
        if (root != null && root.length() == 0) {
            root = null;
        }
        this.root = root;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setAppendLostFileNameExtension(boolean appendLostFileNameExtension) {
        this.dontAppendLostFileNameExtension = !appendLostFileNameExtension;
        dontAppendLostFileNameExtensionSettedFlag = true;
    }

    public void setFileNameExtension(String fileNameExtension) {
        this.fileNameExtension = fileNameExtension;
    }

    public void init(Engine engine) {
        if (encoding == null) {
            encoding = engine.getEncoding();
        }
        if (dontAppendLostFileNameExtensionSettedFlag == false) {
            dontAppendLostFileNameExtension = !engine.isAppendLostFileNameExtension();
        }
        if (fileNameExtension == null) {
            fileNameExtension = engine.getFileNameExtension();
        }
    }
}
