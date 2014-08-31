// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

/**
 *
 * @author Zqq
 */
public interface CFG {

   String DEFAULT_WIM = "/default.wim";
   String WIM_FILE_LIST  = "$$WIMS";
   String DEFAULT_ENCODING = "DEFAULT_ENCODING";
   
   String ENGINE           = "engine";

   String LOADER           = ENGINE + ".resourceLoader";
   String OUT_ENCODING     = ENGINE + ".encoding";
   String FILTER           = ENGINE + ".filter";
   String LOGGER           = ENGINE + ".logger";
   String LOOSE_VAR        = ENGINE + ".looseVar";
   String SHARE_ROOT       = ENGINE + ".shareRootData";
   String TRIM_CODE_LINE   = ENGINE + ".trimCodeBlockBlankLine";
   String VARS             = ENGINE + ".vars";
   String SUFFIX           = ENGINE + ".suffix";
   String INIT_TEMPLATES   = ENGINE + ".initTemplates";
   String TEXT_FACTORY     = ENGINE + ".textStatementFactory";
   String APPEND_LOST_SUFFIX    = ENGINE + ".appendLostSuffix";
   
   String SIMPLE_TEXT_FACTORY      = "webit.script.core.text.impl.SimpleTextStatementFactory";
   String BYTE_ARRAY_TEXT_FACTORY  = "webit.script.core.text.impl.ByteArrayTextStatementFactory";
   String CHAR_ARRAY_TEXT_FACTORY  = "webit.script.core.text.impl.CharArrayTextStatementFactory";
   
   String RESOLVERS        = "resolverManager.resolvers";
   String SECURITY_LIST    = "nativeSecurity.list";
   
   String LOADER_ENCODING  = "loader.encoding";
   String LOADER_ROOT      = "loader.root";
   
   String SERVLET_CONTEXT  = "servlet.servletContext";
}
