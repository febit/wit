// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders;

import org.febit.wit.Init;
import org.febit.wit.util.FileNameUtil;

/**
 *
 * @author zqq90
 */
public abstract class AbstractLoader implements Loader {

    protected String encoding;
    protected String root;
    protected boolean appendLostSuffix;
    protected String suffix;
    protected String[] assistantSuffixs;
    protected boolean enableCache = true;
    protected boolean codeFirst;

    @Init
    public void init() {
        root = FileNameUtil.normalize(root);
        if (root != null && root.isEmpty()) {
            root = null;
        }
    }

    /**
     * get child template name by parent template name and relative name.
     *
     * <pre>
     * example:
     * /path/to/tmpl1.wit , tmpl2.wit =&gt; /path/to/tmpl2.wit
     * /path/to/tmpl1.wit , /tmpl2.wit =&gt; /tmpl2.wit
     * /path/to/tmpl1.wit , ./tmpl2.wit =&gt; /path/to/tmpl2.wit
     * /path/to/tmpl1.wit , ../tmpl2.wit =&gt; /path/tmpl2.wit
     * </pre>
     *
     * @param parent parent template's name
     * @param name relative name
     * @return child template's name
     */
    @Override
    public String concat(final String parent, final String name) {
        return parent != null
                ? FileNameUtil.concat(FileNameUtil.getPath(parent), name)
                : name;
    }

    /**
     * get real path from name.
     *
     * @param name
     * @return
     */
    protected String getRealPath(final String name) {
        return this.root != null
                ? this.root.concat(name)
                : name.substring(1, name.length());
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
    @Override
    public String normalize(String name) {
        if (name == null) {
            return null;
        }
        if (name.isEmpty()) {
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
            for (String item : this.assistantSuffixs) {
                if (name.endsWith(item)) {
                    return name;
                }
            }
            return name.concat(this.suffix);
        }
    }

    @Override
    public boolean isEnableCache(String name) {
        return enableCache;
    }
}
