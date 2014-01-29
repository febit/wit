// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.util.ArrayList;
import java.util.HashMap;
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
import webit.script.util.ClassEntry;
import webit.script.util.ClassUtil;
import webit.script.util.EncodingPool;
import webit.script.util.Petite;
import webit.script.util.PropsUtil;
import webit.script.util.SimpleBag;
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
        } catch (ResourceNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        return engine;
    }

    //settings
    private ClassEntry resourceLoaderClass;
    private ClassEntry filterClass;
    private ClassEntry textStatementFactoryClass;
    private ClassEntry nativeSecurityManagerClass;
    private ClassEntry coderFactoryClass;
    private ClassEntry globalManagerClass;
    private ClassEntry loggerClass;
    private ClassEntry[] resolvers;
    private String encoding = EncodingPool.UTF_8;
    private boolean looseVar = false;
    private boolean shareRootData = true;
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
    private final Map<String, Object> componentContainer;
    private final Petite petite;

    private Engine(final Petite petite) {
        this.petite = petite;
        this.templateCache = new ConcurrentHashMap<String, Template>();
        this.componentContainer = new HashMap<String, Object>();
        this.resolverManager = new ResolverManager();
        this.nativeFactory = new NativeFactory();
    }

    @SuppressWarnings("unchecked")
    private void init() {

        if (this.loggerClass == null) {
            this.loggerClass = ClassEntry.wrap(webit.script.loggers.impl.NOPLogger.class);
        }
        this.logger = (Logger) ClassUtil.newInstance(this.loggerClass);

        if (this.coderFactoryClass == null) {
            this.coderFactoryClass = ClassEntry.wrap(webit.script.io.charset.impl.DefaultCoderFactory.class);
        }
        this.coderFactory = (CoderFactory) ClassUtil.newInstance(this.coderFactoryClass);

        if (this.nativeSecurityManagerClass == null) {
            this.nativeSecurityManagerClass = ClassEntry.wrap(webit.script.security.impl.DefaultNativeSecurityManager.class);
        }
        this.nativeSecurityManager = (NativeSecurityManager) ClassUtil.newInstance(this.nativeSecurityManagerClass);

        if (this.textStatementFactoryClass == null) {
            this.textStatementFactoryClass = ClassEntry.wrap(webit.script.core.text.impl.SimpleTextStatementFactory.class);
        }
        this.textStatementFactory = (TextStatementFactory) ClassUtil.newInstance(this.textStatementFactoryClass);

        if (this.resourceLoaderClass == null) {
            this.resourceLoaderClass = ClassEntry.wrap(webit.script.loaders.impl.ClasspathLoader.class);
        }
        this.resourceLoader = (Loader) ClassUtil.newInstance(this.resourceLoaderClass);

        if (this.globalManagerClass == null) {
            this.globalManagerClass = ClassEntry.wrap(webit.script.global.DefaultGlobalManager.class);
        }
        this.globalManager = (GlobalManager) ClassUtil.newInstance(this.globalManagerClass);

        if (this.filterClass != null) {
            this.filter = (Filter) ClassUtil.newInstance(this.filterClass);
        }

        resolveComponent(this.logger, this.loggerClass);
        this.petite.setLogger(this.logger);

        resolveComponent(this.nativeFactory);
        resolveComponent(this.resolverManager);
        resolveComponent(this.coderFactory, this.coderFactoryClass);
        resolveComponent(this.nativeSecurityManager,this.nativeSecurityManagerClass);
        resolveComponent(this.textStatementFactory, this.textStatementFactoryClass);
        resolveComponent(this.resourceLoader, this.resourceLoaderClass);
        if (this.filter != null) {
            resolveComponent(this.filter, this.filterClass);
        }

        if (this.resolvers != null) {
            final int resolversLength;
            final Resolver[] resolverInstances = new Resolver[resolversLength = this.resolvers.length];
            for (int i = 0; i < resolversLength; i++) {
                resolverInstances[i] = (Resolver) getComponent(this.resolvers[i]);
            }
            this.resolverManager.init(resolverInstances);
        }

        resolveComponent(this.globalManager, this.globalManagerClass);
        this.globalManager.commit();
    }

    private void executeInitTemplates() throws ResourceNotFoundException {
        final int size;
        if (this.initTemplates != null && (size = this.initTemplates.length) > 0) {
            String templateName;
            final Out out = new DiscardOut();
            final GlobalManager myGlobalManager = this.globalManager;
            final SimpleBag globalBag = myGlobalManager.getGlobalBag();
            final SimpleBag constBag = myGlobalManager.getGlobalBag();
            final KeyValues params = KeyValuesUtil.wrap(new String[]{
                "GLOBAL", "GLOBAL_MAP",
                "CONST", "CONST_MAP"
            }, new Object[]{
                globalBag, globalBag,
                constBag, constBag
            });
            for (int i = 0; i < size; i++) {
                if ((templateName = this.initTemplates[i]) != null
                        && (templateName = templateName.trim()).length() != 0) {
                    this.getTemplate(templateName)
                            .merge(params, out);
                    //Commit Global
                    myGlobalManager.commit();
                }
            }
        }
    }

    /**
     * get config by key form engine
     *
     * @since 1.4.0
     * @param key
     * @return config value
     */
    public Object getConfig(String key) {
        return this.petite.getParameter(key);
    }

    /**
     *
     * @since 1.4.0
     */
    private void resolveComponent(final Object bean) {
        resolveComponent(bean, ClassEntry.wrap(bean.getClass()));
    }

    /**
     *
     * @since 1.4.0
     */
    private void resolveComponent(final Object bean, final ClassEntry type) {
        this.petite.wireBean(type.getProfile(), bean);
        if (bean instanceof Initable) {
            ((Initable) bean).init(this);
        }
        this.componentContainer.put(type.getProfile(), bean);
    }

    /**
     *
     * @param type
     * @return Component
     * @since 1.4.0
     */
    public Object getComponent(final Class type) {
        return getComponent(ClassEntry.wrap(type));
    }

    /**
     *
     * @param type
     * @return Component
     * @since 1.4.0
     */
    public synchronized Object getComponent(final ClassEntry type) {
        Object bean;
        if ((bean = this.componentContainer.get(type.getProfile())) == null) {
            resolveComponent(bean = ClassUtil.newInstance(type), type);
        }
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
        final Template template;
        if ((template = this.templateCache.get(name)) != null) {
            return template;
        } else {
            return createTemplateIfAbsent(name);
        }
    }

    private Template createTemplateIfAbsent(final String name) throws ResourceNotFoundException {
        Template template;
        final String normalizedName;
        final Loader loader;
        if ((normalizedName = (loader = this.resourceLoader).normalize(name)) != null) {
            if ((template = this.templateCache.get(normalizedName)) == null) {
                Template oldTemplate;
                template = new Template(this, normalizedName,
                        loader.get(normalizedName));
                if (loader.isEnableCache(normalizedName)) {
                    if ((oldTemplate = this.templateCache.putIfAbsent(normalizedName, template)) != null) {
                        template = oldTemplate;
                    }
                    if (!name.equals(normalizedName)
                            && (oldTemplate = this.templateCache.putIfAbsent(name, template)) != null) {
                        template = oldTemplate;
                    }
                }
            }
            return template;
        }
        throw new ResourceNotFoundException("Illegal template path: ".concat(name));
    }

    public boolean checkNativeAccess(String path) {
        return this.nativeSecurityManager.access(path);
    }

    public void setNativeSecurityManagerClass(ClassEntry nativeSecurityManagerClass) {
        this.nativeSecurityManagerClass = nativeSecurityManagerClass;
    }

    public void setCoderFactoryClass(ClassEntry coderFactoryClass) {
        this.coderFactoryClass = coderFactoryClass;
    }

    public void setResolvers(ClassEntry[] resolvers) {
        this.resolvers = resolvers;
    }

    public void setResourceLoaderClass(ClassEntry resourceLoaderClass) {
        this.resourceLoaderClass = resourceLoaderClass;
    }

    public void setTextStatementFactoryClass(ClassEntry textStatementFactoryClass) {
        this.textStatementFactoryClass = textStatementFactoryClass;
    }

    /**
     *
     * @since 1.4.0
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

    public void setFilterClass(ClassEntry filterClass) {
        this.filterClass = filterClass;
    }

    public void setGlobalManagerClass(ClassEntry globalManagerClass) {
        this.globalManagerClass = globalManagerClass;
    }

    public GlobalManager getGlobalManager() {
        return globalManager;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setLoggerClass(ClassEntry loggerClass) {
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
