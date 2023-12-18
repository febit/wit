// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loaders;

import org.febit.wit.lang.Resource;

public interface Loader {

    /**
     * get template's Resource by it's name.
     *
     * @param name template's name
     * @return Resource
     */
    Resource get(String name);

    /**
     * get child template name by parent template name and relative name.
     *
     * @param parent parent template's name
     * @param name   relative name
     * @return child template's name
     */
    String concat(String parent, String name);

    /**
     * normalize a template's name.
     *
     * @param name template's name
     * @return normalized name
     */
    String normalize(String name);

    /**
     * if this template need to be cached.
     *
     * @param name template's name
     * @return boolean
     */
    boolean isEnableCache(String name);
}
