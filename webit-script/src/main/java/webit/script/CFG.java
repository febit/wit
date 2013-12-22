// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

/**
 *
 * @author Zqq
 */
public interface CFG {

    public static final String PROPS_FILE_LIST = "$$propsFiles";
    public static final String DEFAULT_PROPERTIES = "/webit-script-default.props";
    
    public static final String DEFAULT_ENCODING = "DEFAULT_ENCODING";
    public static final String ENGINE           = "webit.script.Engine";

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
    public static final String SECURITY_LIST    = "webit.script.security.impl.DefaultNativeSecurityManager.list";
    
    public static final String SERVLET_LOADER   = "webit.script.web.loaders.ServletLoader";
    public static final String SERVLET_LOADER_ENCODING  = SERVLET_LOADER + ".encoding";
    public static final String SERVLET_LOADER_ROOT      = SERVLET_LOADER + ".root";
    public static final String SERVLET_LOADER_SERVLETCONTEXT    = SERVLET_LOADER + ".servletContext";
    
    
    public static final String CLASSPATH_LOADER   = "webit.script.loaders.impl.ClasspathLoader";
    public static final String CLASSPATH_LOADER_ENCODING  = CLASSPATH_LOADER + ".encoding";
    public static final String CLASSPATH_LOADER_ROOT      = CLASSPATH_LOADER + ".root";

}
