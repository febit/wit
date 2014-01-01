// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import webit.script.core.NativeFactory;
import webit.script.core.text.TextStatementFactory;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.filters.Filter;
import webit.script.global.GlobalManager;
import webit.script.io.Out;
import webit.script.io.charset.CoderFactory;
import webit.script.io.impl.DiscardOut;
import webit.script.loaders.Loader;
import webit.script.loggers.Logger;
import webit.script.resolvers.Resolver;
import webit.script.resolvers.ResolverManager;
import webit.script.security.NativeSecurityManager;
import webit.script.util.EncodingPool;
import webit.script.util.Petite;
import webit.script.util.PropsUtil;
import webit.script.util.keyvalues.KeyValues;
import webit.script.util.keyvalues.KeyValuesUtil;
import webit.script.util.props.Props;

/**
 *
 * @author Zqq
 */
public final class Engine {

    public final static String DEFAULT_SUFFIX = ".wit";

    public static Engine createEngine(final String configPath) {
        return createEngine(configPath, null);
    }

    public static Props createConfigProps(final String configPath) {
        return PropsUtil.loadFromClasspath(new Props(), CFG.DEFAULT_PROPERTIES, configPath);
    }

    public static Engine createEngine(final String configPath, final Map<String, Object> parameters) {
        return createEngine(createConfigProps(configPath), parameters);
    }

    public static Engine createEngine(final Props props, final Map<String, Object> parameters) {

        final Petite petite = new Petite();
        petite.defineParameters(props, parameters);

        final Engine engine;
        petite.wireBean(engine = new Engine(petite));

        try {
            engine.init();

            final Logger logger;
            if ((logger = engine.getLogger()).isInfoEnabled()) {
                logger.info("Loaded props: ".concat(String.valueOf(petite.getParameter(CFG.PROPS_FILE_LIST))));
            }

            engine.executeInitTemplates();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (ResourceNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        return engine;
    }

    //settings
    private Class resourceLoaderClass = webit.script.loaders.impl.ClasspathLoader.class;
    private Class textStatementFactoryClass = webit.script.core.text.impl.SimpleTextStatementFactory.class;
    private Class nativeSecurityManagerClass = webit.script.security.impl.DefaultNativeSecurityManager.class;
    private Class coderFactoryClass = webit.script.io.charset.impl.DefaultCoderFactory.class;
    private Class filterClass;
    private Class globalManagerClass = webit.script.global.DefaultGlobalManager.class;
    private Class loggerClass = webit.script.loggers.impl.NOPLogger.class;
    private Class[] resolvers;
    private String encoding = EncodingPool.UTF_8;
    private boolean looseVar = false;
    private boolean shareRootData = false;
    private boolean trimCodeBlockBlankLine = true;
    private boolean appendLostSuffix = false;
    private String suffix = DEFAULT_SUFFIX;
    private String[] vars = null;
    private String[] initTemplates = null;
    //
    private Logger logger;
    private Loader resourceLoader;
    private GlobalManager globalManager;
    private TextStatementFactory textStatementFactory;
    private NativeSecurityManager nativeSecurityManager;
    private CoderFactory coderFactory;
    private Filter filter;
    //
    private final NativeFactory nativeFactory;
    private final ResolverManager resolverManager;
    private final ConcurrentMap<String, Template> templateCache;
    private final Petite petite;

    private Engine(final Petite petite) {
        this.petite = petite;
        this.templateCache = new ConcurrentHashMap<String, Template>();
        this.resolverManager = new ResolverManager();
        this.nativeFactory = new NativeFactory();
    }

    @SuppressWarnings("unchecked")
    private void init() throws InstantiationException, IllegalAccessException {

        this.logger = (Logger) newInstance(this.loggerClass);
        this.coderFactory = (CoderFactory) newInstance(this.coderFactoryClass);
        this.nativeSecurityManager = (NativeSecurityManager) newInstance(this.nativeSecurityManagerClass);
        this.textStatementFactory = (TextStatementFactory) newInstance(this.textStatementFactoryClass);
        this.resourceLoader = (Loader) newInstance(this.resourceLoaderClass);
        this.globalManager = (GlobalManager) newInstance(this.globalManagerClass);

        if (this.filterClass != null) {
            this.filter = (Filter) newInstance(this.filterClass);
        }

        resolveBean(this.logger);
        this.petite.setLogger(this.logger);

        resolveBean(this.nativeFactory);
        resolveBean(this.resolverManager);
        resolveBean(this.coderFactory);
        resolveBean(this.nativeSecurityManager);
        resolveBean(this.textStatementFactory);
        resolveBean(this.resourceLoader);
        if (this.filter != null) {
            resolveBean(this.filter);
        }
        resolveBean(this.resolverManager);

        if (this.resolvers != null) {
            final int resolversLength;
            final Resolver[] resolverInstances = new Resolver[resolversLength = this.resolvers.length];
            for (int i = 0; i < resolversLength; i++) {
                resolverInstances[i] = (Resolver) getBean(this.resolvers[i]);
            }
            this.resolverManager.init(resolverInstances);
        }

        resolveBean(this.globalManager);
        globalManager.commit();
    }

    private void executeInitTemplates() throws ResourceNotFoundException {
        final int size;
        if (this.initTemplates != null && (size = this.initTemplates.length) > 0) {
            String templateName;
            final Out out = new DiscardOut();
            final KeyValues params = KeyValuesUtil.wrap(new String[]{
                "GLOBAL_MAP",
                "CONST_MAP"
            }, new Object[]{
                this.globalManager.getGlobalBag(),
                this.globalManager.getConstBag()
            });
            for (int i = 0; i < size; i++) {
                if ((templateName = this.initTemplates[i]) != null
                        && (templateName = templateName.trim()).length() != 0) {
                    this.getTemplate(templateName)
                            .merge(params, out);
                    //Commit Global
                    this.globalManager.commit();
                }
            }
        }
    }

    public void resolveBean(final Object bean) {
        this.petite.wireBean(bean);
        if (bean instanceof Initable) {
            ((Initable) bean).init(this);
        }
    }

    /**
     * get config by key form engine
     *
     * @since 1.3.2
     * @param key
     * @return config value
     */
    public Object getConfig(String key) {
        return this.petite.getParameter(key);
    }

    private Object newInstance(final Class type) throws InstantiationException, IllegalAccessException {
        return type.newInstance();
    }

    public Object getBean(final Class type) throws InstantiationException, IllegalAccessException {
        Object bean;
        resolveBean(bean = newInstance(type));
        return bean;
    }

    /**
     * get template by parent template's name and it's relative name.
     *
     * @param parentName parent template's name
     * @param name template's relative name
     * @return Template
     * @throws ResourceNotFoundException
     */
    public Template getTemplate(final String parentName, final String name) throws ResourceNotFoundException {
        return getTemplate(this.resourceLoader.concat(parentName, name));
    }

    /**
     * get template by name.
     *
     * @param name template's name
     * @return Template
     * @throws ResourceNotFoundException
     */
    public Template getTemplate(final String name) throws ResourceNotFoundException {
        Template template;
        if ((template = this.templateCache.get(name)) != null) {
            return template;
        } else {
            return createTemplateIfAbsent(name);
        }
    }

    private Template createTemplateIfAbsent(final String name) throws ResourceNotFoundException {
        Template template;
        final String normalizedName;
        if ((normalizedName = this.resourceLoader.normalize(name)) != null) {
            if ((template = this.templateCache.get(normalizedName)) == null) {
                Template oldTemplate;
                if ((oldTemplate = this.templateCache.putIfAbsent(normalizedName,
                        template = new Template(this, normalizedName,
                                this.resourceLoader.get(normalizedName)))) != null) {
                    template = oldTemplate;
                }
                if (!name.equals(normalizedName)
                        && (oldTemplate = this.templateCache.putIfAbsent(name, template)) != null) {
                    template = oldTemplate;
                }
            }
            return template;
        }
        throw new ResourceNotFoundException("Illegal template path: ".concat(name));
    }

    public boolean checkNativeAccess(String path) {
        return this.nativeSecurityManager.access(path);
    }

    public void setNativeSecurityManagerClass(Class nativeSecurityManagerClass) {
        this.nativeSecurityManagerClass = nativeSecurityManagerClass;
    }

    public void setCoderFactoryClass(Class coderFactoryClass) {
        this.coderFactoryClass = coderFactoryClass;
    }

    public void setResolvers(Class[] resolvers) {
        this.resolvers = resolvers;
    }

    public void setResourceLoaderClass(Class resourceLoaderClass) {
        this.resourceLoaderClass = resourceLoaderClass;
    }

    public void setTextStatementFactoryClass(Class textStatementFactoryClass) {
        this.textStatementFactoryClass = textStatementFactoryClass;
    }

    /**
     *
     * @since 1.3.2
     * @return Loader
     */
    public Loader getResourceLoader() {
        return resourceLoader;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        if (encoding != null) {
            this.encoding = EncodingPool.intern(encoding);
        }
    }

    public NativeFactory getNativeFactory() {
        return nativeFactory;
    }

    public boolean isLooseVar() {
        return looseVar;
    }

    public void setLooseVar(boolean looseVar) {
        this.looseVar = looseVar;
    }

    public boolean isShareRootData() {
        return shareRootData;
    }

    public void setShareRootData(boolean shareRootData) {
        this.shareRootData = shareRootData;
    }

    public boolean isTrimCodeBlockBlankLine() {
        return trimCodeBlockBlankLine;
    }

    public void setTrimCodeBlockBlankLine(boolean trimCodeBlockBlankLine) {
        this.trimCodeBlockBlankLine = trimCodeBlockBlankLine;
    }

    public NativeSecurityManager getNativeSecurityManager() {
        return nativeSecurityManager;
    }

    public ResolverManager getResolverManager() {
        return resolverManager;
    }

    public TextStatementFactory getTextStatementFactory() {
        return textStatementFactory;
    }

    public CoderFactory getCoderFactory() {
        return coderFactory;
    }

    public void setFilterClass(Class filterClass) {
        this.filterClass = filterClass;
    }

    public void setGlobalManagerClass(Class globalManagerClass) {
        this.globalManagerClass = globalManagerClass;
    }

    public GlobalManager getGlobalManager() {
        return globalManager;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setLoggerClass(Class loggerClass) {
        this.loggerClass = loggerClass;
    }

    public Logger getLogger() {
        return logger;
    }

    public boolean isAppendLostSuffix() {
        return appendLostSuffix;
    }

    public void setAppendLostSuffix(boolean appendLostSuffix) {
        this.appendLostSuffix = appendLostSuffix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String[] getVars() {
        return vars;
    }

    public void setVars(String[] vars) {
        final int len;
        if (vars != null && (len = vars.length) != 0) {
            final ArrayList<String> list = new ArrayList<String>(len);
            String var;
            for (int i = 0; i < len; i++) {
                if ((var = vars[i].trim()).length() != 0) {
                    list.add(var);
                }
            }
            this.vars = list.size() != 0
                    ? list.toArray(new String[list.size()])
                    : null;
        } else {
            this.vars = null;
        }
    }

    public void setInitTemplates(String[] initTemplates) {
        this.initTemplates = initTemplates;
    }
}
