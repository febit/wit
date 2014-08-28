// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loaders;

import webit.script.Engine;
import webit.script.Initable;
import webit.script.util.StringUtil;
import webit.script.util.FileNameUtil;

/**
 *
 * @author Zqq
 */
public abstract class AbstractLoader implements Loader, Initable {

    protected String encoding;
    protected String root = null;
    protected boolean appendLostSuffix;
    protected String suffix;
    protected String[] assistantSuffixs;
    protected boolean enableCache = true;
    //
    protected boolean appendLostSuffixSettedFlag = false;

    public void init(Engine engine) {
        if (this.encoding == null) {
            this.encoding = engine.getEncoding();
        }
        if (!this.appendLostSuffixSettedFlag) {
            this.appendLostSuffix = engine.isAppendLostSuffix();
        }
        if (this.suffix == null) {
            this.suffix = engine.getSuffix();
        }
        if (this.assistantSuffixs == null) {
            this.assistantSuffixs = engine.getAssistantSuffixs();
            if (this.assistantSuffixs == null) {
                this.assistantSuffixs = StringUtil.EMPTY_ARRAY;
            }
        }
    }

    /**
     * get child template name by parent template name and relative name.
     *
     * <pre>
     * example:
     * /path/to/tmpl1.wit , tmpl2.wit => /path/to/tmpl2.wit
     * /path/to/tmpl1.wit , /tmpl2.wit => /tmpl2.wit
     * /path/to/tmpl1.wit , ./tmpl2.wit => /path/to/tmpl2.wit
     * /path/to/tmpl1.wit , ../tmpl2.wit => /path/tmpl2.wit
     * </pre>
     *
     * @param parent parent template's name
     * @param name relative name
     * @return child template's name
     */
    public String concat(final String parent, final String name) {
        return parent != null ? FileNameUtil.concat(FileNameUtil.getPath(parent), name) : name;
    }

    /**
     * get real path from name.
     *
     * @param name
     * @return
     */
    protected String getRealPath(final String name) {
        return this.root != null ? (this.root.concat(name)) : name.substring(1, name.length());
    }

    /**
     * normalize a template's name.
     *
     * <pre>
     * example:
     * path/to/tmpl.wit  /path/to/tmpl.wit
     * /path/to/./tmpl.wit  /path/to/tmpl.wit
     * /path/to/../tmpl.wit  /path/tmpl.wit
     * \path\to\..\tmpl.wit  /path/tmpl.wit
     * \path\to\..\..\tmpl.wit  /tmpl.wit
     * \path\to\..\..\..\tmpl.wit  null
     * </pre>
     *
     * @param name template's name
     * @return normalized name
     */
    public String normalize(String name) {
        if (name == null) {
            return null;
        }
        if (name.length() == 0) {
            return "/";
        }
        if (name.charAt(0) != '/' && name.charAt(0) != '\\') {
            name = "/".concat(name);
        }
        name = FileNameUtil.normalize(name);
        if (name == null) {
            return null;
        }
        if (!this.appendLostSuffix
                || name.endsWith(this.suffix)
                || name.charAt(name.length() - 1) == '/') {
            return name;
        } else {
            for (String item: this.assistantSuffixs) {
                if (name.endsWith(item)) {
                    return name;
                }
            }
            return name.concat(this.suffix);
        }
    }

    public void setRoot(String root) {
        root = FileNameUtil.normalize(root);
        if (root != null && root.length() == 0) {
            root = null;
        }
        this.root = root;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setAppendLostSuffix(boolean appendLostSuffix) {
        this.appendLostSuffix = appendLostSuffix;
        this.appendLostSuffixSettedFlag = true;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setAssistantSuffixs(String assistantSuffixs) {
        this.assistantSuffixs = StringUtil.splitAndRemoveBlank(assistantSuffixs);
    }

    public boolean isEnableCache(String name) {
        return enableCache;
    }

    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }
}
