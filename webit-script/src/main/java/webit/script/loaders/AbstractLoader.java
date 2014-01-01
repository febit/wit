// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
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

    /**
     * get child template name by parent template name and relative name.
     *
     * <pre>
     * example:
     * /path/to/tmpl1.wtl , tmpl2.wtl => /path/to/tmpl2.wtl
     * /path/to/tmpl1.wtl , /tmpl2.wtl => /tmpl2.wtl
     * /path/to/tmpl1.wtl , ./tmpl2.wtl => /path/to/tmpl2.wtl
     * /path/to/tmpl1.wtl , ../tmpl2.wtl => /path/tmpl2.wtl
     * </pre>
     *
     * @param parent parent template's name
     * @param name relative name
     * @return child template's name
     */
    public String concat(final String parent, final String name) {
        return parent != null ? UnixStyleFileNameUtil.concat(UnixStyleFileNameUtil.getPath(parent), name) : name;
    }

    protected String getRealPath(final String name) {
        return this.root != null ? (this.root.concat(name)) : name.substring(1, name.length());
    }

    /**
     * normalize a template's name.
     *
     * <pre>
     * example:
     * /path/to/./tmpl1.wtl  /path/to/tmpl2.wtl
     * /path/to/../tmpl1.wtl  /path/tmpl2.wtl
     * \path\to\..\tmpl1.wtl  /path/tmpl2.wtl
     * \path\to\..\..\tmpl1.wtl  /tmpl2.wtl
     * \path\to\..\..\..\tmpl1.wtl  null
     * </pre>
     *
     * @param name template's name
     * @return normalized name
     */
    public String normalize(String name) {
        if (name != null) {
            final String newName;
            newName = UnixStyleFileNameUtil.normalize(StringUtil.prefixChar(name, '/'));
            if (this.dontAppendLostFileNameExtension || newName.charAt(newName.length() - 1) == '/' || newName.endsWith(this.fileNameExtension)) {
                return newName;
            } else {
                return newName.concat(this.fileNameExtension);
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
        this.dontAppendLostFileNameExtensionSettedFlag = true;
    }

    public void setFileNameExtension(String fileNameExtension) {
        this.fileNameExtension = fileNameExtension;
    }

    public void init(Engine engine) {
        if (this.encoding == null) {
            this.encoding = engine.getEncoding();
        }
        if (this.dontAppendLostFileNameExtensionSettedFlag == false) {
            this.dontAppendLostFileNameExtension = !engine.isAppendLostSuffix();
        }
        if (this.fileNameExtension == null) {
            this.fileNameExtension = engine.getSuffix();
        }
    }
}
