// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

/**
 *
 * @author Zqq
 */
public interface CFG {

    public static final String DEFAULT_PROPERTIES = "/webit-script-default.props";
    public static final String PROPS_FILE_LIST  = "$$propsFiles";
    public static final String DEFAULT_ENCODING = "DEFAULT_ENCODING";
    
    public static final String ENGINE           = "engine";

    public static final String RESOLVERS        = ENGINE + ".resolvers";
    public static final String LOADER           = ENGINE + ".resourceLoaderClass";
    public static final String OUT_ENCODING     = ENGINE + ".encoding";
    public static final String FILTER           = ENGINE + ".filterClass";
    public static final String LOGGER           = ENGINE + ".loggerClass";
    public static final String LOOSE_VAR        = ENGINE + ".looseVar";
    public static final String SHARE_ROOT       = ENGINE + ".shareRootData";
    public static final String TRIM_CODE_LINE   = ENGINE + ".trimCodeBlockBlankLine";
    public static final String VARS             = ENGINE + ".vars";
    public static final String SUFFIX           = ENGINE + ".suffix";
    public static final String INIT_TEMPLATES   = ENGINE + ".initTemplates";
    public static final String TEXT_FACTORY     = ENGINE + ".textStatementFactoryClass";
    public static final String APPEND_LOST_SUFFIX    = ENGINE + ".appendLostSuffix";
    
    public static final String SIMPLE_TEXT_FACTORY      = "webit.script.core.text.impl.SimpleTextStatementFactory";
    public static final String BYTE_ARRAY_TEXT_FACTORY  = "webit.script.core.text.impl.ByteArrayTextStatementFactory";
    public static final String CHAR_ARRAY_TEXT_FACTORY  = "webit.script.core.text.impl.CharArrayTextStatementFactory";
    
    public static final String ASM_NATIVE       = "webit.script.core.NativeFactory.enableAsm";
    public static final String ASM_RESOLVER     = "webit.script.resolvers.ResolverManager.enableAsm";
    public static final String SECURITY_LIST    = "nativeSecurity.list";
    
    public static final String LOADER_ENCODING  = "loader.encoding";
    public static final String LOADER_ROOT      = "loader.root";
    
    public static final String SERVLET_CONTEXT  = "servlet.servletContext";
}
