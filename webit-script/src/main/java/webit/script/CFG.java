// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

/**
 *
 * @author Zqq
 */
public interface CFG {

    String DEFAULT_WIM = "/default.wim";
    String WIM_FILE_LIST = "$$WIMS";
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
