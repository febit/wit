// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit;

/**
 *
 * @author zqq90
 */
public interface CFG {

    String DEFAULT_WIM = "/default.wim";
    String DEFAULT_ENCODING = "DEFAULT_ENCODING";

    String OUT_ENCODING = "engine.encoding";
    String LOOSE_VAR = "engine.looseVar";
    String SHARE_ROOT = "engine.shareRootData";
    String TRIM_CODE_LINE = "engine.trimCodeBlockBlankLine";
    String VARS = "engine.vars";

    String RESOLVERS = "resolverManager.resolvers";
    String SECURITY_LIST = "defaultNativeSecurity.list";

    String APPEND_LOST_SUFFIX = "pathLoader.appendLostSuffix";
    String LOADER_ENCODING = "loader.encoding";
    String LOADER_ROOT = "loader.root";

    String SERVLET_CONTEXT = "servlet.servletContext";
}
