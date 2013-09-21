// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jodd.props.Props;
import webit.script.core.text.TextStatmentFactory;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.filters.Filter;
import webit.script.io.charset.CoderFactory;
import webit.script.loaders.Loader;
import webit.script.loggers.Logger;
import webit.script.resolvers.Resolver;
import webit.script.resolvers.ResolverManager;
import webit.script.security.NativeSecurityManager;
import webit.script.util.Petite;
import webit.script.util.PropsUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public final class Engine {

    public static final String ENGINE = "engine";
    public static final String PETITE = "petite";
    //
    private static final String DEFAULT_PROPERTIES = "/webitl-default.props";
    //settings
    private Class resourceLoaderClass = webit.script.loaders.impl.ClasspathLoader.class;
    private Class textStatmentFactoryClass = webit.script.core.text.impl.SimpleTextStatmentFactory.class;
    private Class nativeSecurityManagerClass = webit.script.security.impl.DefaultNativeSecurityManager.class;
    private Class coderFactoryClass = webit.script.io.charset.impl.DefaultCoderFactory.class;
    private Class filterClass;
    private Class loggerClass = webit.script.loggers.impl.NOPLogger.class;
    private Class[] resolvers;
    private String encoding = "UTF-8";
    private boolean enableAsmNative = true;
    private boolean looseVar = false;
    //
    private Logger logger;
    private Loader resourceLoader;
    private TextStatmentFactory textStatmentFactory;
    private NativeSecurityManager nativeSecurityManager;
    private CoderFactory coderFactory;
    private Filter filter;
    //
    private final ResolverManager resolverManager;
    private final ConcurrentMap<String, Template> templateCache;
    private final Petite _petite;

    private Engine(Petite petite) {
        this._petite = petite;
        this.templateCache = new ConcurrentHashMap<String, Template>();
        this.resolverManager = new ResolverManager();
    }

    @SuppressWarnings("unchecked")
    private void init() throws Exception {

        this.logger = (Logger) getBean(this.loggerClass);

        this.resourceLoader = (Loader) getBean(this.resourceLoaderClass);
        this.textStatmentFactory = (TextStatmentFactory) getBean(this.textStatmentFactoryClass);
        this.nativeSecurityManager = (NativeSecurityManager) getBean(this.nativeSecurityManagerClass);
        this.coderFactory = (CoderFactory) getBean(this.coderFactoryClass);

        if (this.filterClass != null) {
            this.filter = (Filter) getBean(this.filterClass);
        }

        resolveBean(this.resolverManager);
        if (this.resolvers != null) {
            Resolver[] resolverInstances = new Resolver[this.resolvers.length];
            for (int i = 0; i < this.resolvers.length; i++) {
                resolverInstances[i] = (Resolver) getBean(this.resolvers[i]);
            }
            this.resolverManager.init(resolverInstances);
        }
    }

    public void resolveBean(Object bean) {
        _petite.wireBean(bean);
        if (bean instanceof Initable) {
            ((Initable) bean).init(this);
        }
    }

    @SuppressWarnings("unchecked")
    public <E> E getBean(Class<E> type) throws InstantiationException, IllegalAccessException {

        Object bean = type.newInstance();
        resolveBean(bean);
        return (E) bean;
    }

    public Template getTemplate(String parentName, String name) throws ResourceNotFoundException {
        return getTemplate(resourceLoader.concat(parentName, name));
    }

    public final Template getTemplate(String name) throws ResourceNotFoundException {
        String normalizedName = resourceLoader.normalize(name);
        if (normalizedName == null) {
            throw new ResourceNotFoundException("TODO: 不合法的模板名:" + name);
        }
        Template template = templateCache.get(normalizedName);
        if (template == null) {
            template = new Template(this, normalizedName, resourceLoader.get(normalizedName)); //fast
            Template oldTemplate = templateCache.putIfAbsent(normalizedName, template);
            if (oldTemplate != null) {
                template = oldTemplate;
            }
        }
        return template;
    }

    public final void setResourceLoaderClass(Class resourceLoaderClass) {
        this.resourceLoaderClass = resourceLoaderClass;
    }

    public final void setTextStatmentFactoryClass(Class textStatmentFactoryClass) {
        this.textStatmentFactoryClass = textStatmentFactoryClass;
    }

    public final void setResourceLoader(Loader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public final String getEncoding() {
        return encoding;
    }

    public final void setEncoding(String encoding) {
        if (encoding != null) {
            this.encoding = encoding.toLowerCase().intern();
        }
    }

    public final boolean isEnableAsmNative() {
        return enableAsmNative;
    }

    public final void setEnableAsmNative(boolean enableAsmNative) {
        this.enableAsmNative = enableAsmNative;
    }

    public final boolean isLooseVar() {
        return looseVar;
    }

    public final void setLooseVar(boolean looseVar) {
        this.looseVar = looseVar;
    }

    public final NativeSecurityManager getNativeSecurityManager() {
        return nativeSecurityManager;
    }

    public final ResolverManager getResolverManager() {
        return resolverManager;
    }

    public final TextStatmentFactory getTextStatmentFactory() {
        return textStatmentFactory;
    }

    public final CoderFactory getCoderFactory() {
        return coderFactory;
    }

    public final void setFilterClass(Class filterClass) {
        this.filterClass = filterClass;
    }

    public final Filter getFilter() {
        return filter;
    }

    public final void setLoggerClass(Class loggerClass) {
        this.loggerClass = loggerClass;
    }

    public final Logger getLogger() {
        return logger;
    }

    public static Engine createEngine(String configPath) {
        return createEngine(configPath, null);
    }

    public static Engine createEngine(String configPath, Map parameters) {

        final Props props = new Props();
        //props.loadSystemProperties("sys");
        //props.loadEnvironment("env");

        List<String> propsFiles;
        if (configPath != null) {
            propsFiles = PropsUtil.loadFromClasspath(props, DEFAULT_PROPERTIES, configPath);
        } else {
            propsFiles = PropsUtil.loadFromClasspath(props, DEFAULT_PROPERTIES);
        }

        if (parameters != null) {
            props.load(parameters);
        }

        Petite petite = new Petite();
        petite.defineParameters(props);

        final Engine engine = new Engine(petite);

        petite.wireBean(engine);

        try {
            engine.init();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        //Log props file name
        final Logger logger = engine.getLogger();
        petite.setLogger(logger);
        if (logger != null && logger.isInfoEnabled()) {
            logger.info("Loaded props files from classpath: {}", StringUtil.join(propsFiles, ", "));
        }

        return engine;
    }
}
