// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import webit.script.core.NativeFactory;
import webit.script.core.text.TextStatementFactory;
import webit.script.exceptions.ResourceNotFoundException;
import webit.script.global.GlobalManager;
import webit.script.io.Out;
import webit.script.io.charset.CoderFactory;
import webit.script.io.impl.DiscardOut;
import webit.script.lang.Bag;
import webit.script.lang.KeyValues;
import webit.script.loaders.Loader;
import webit.script.loggers.Logger;
import webit.script.resolvers.ResolverManager;
import webit.script.security.NativeSecurityManager;
import webit.script.util.ClassEntry;
import webit.script.util.ClassUtil;
import webit.script.util.KeyValuesUtil;
import webit.script.util.Petite;
import webit.script.util.Props;
import webit.script.util.PropsUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public final class Engine {

    public static final String DEFAULT_SUFFIX = ".wit";
    public static final String UTF_8 = "UTF-8";

    //settings
    private ClassEntry resourceLoaderType;
    private ClassEntry textStatementFactoryType;
    private ClassEntry nativeSecurityManagerType;
    private ClassEntry coderFactoryType;
    private ClassEntry globalManagerType;
    private ClassEntry loggerType;
    private ClassEntry resolverManagerType;
    private ClassEntry nativeFactoryType;
    private boolean looseVar;
    private boolean shareRootData = true;
    private boolean trimCodeBlockBlankLine = true;
    private boolean appendLostSuffix;
    private String suffix = DEFAULT_SUFFIX;
    private String encoding = UTF_8;
    private String inits;
    private String[] vars;
    private String[] assistantSuffixs;

    private Logger logger;
    private Loader resourceLoader;
    private GlobalManager globalManager;
    private TextStatementFactory textStatementFactory;
    private NativeSecurityManager nativeSecurityManager;
    private CoderFactory coderFactory;
    private NativeFactory nativeFactory;
    private ResolverManager resolverManager;

    private final ConcurrentMap<String, Template> templateCache;
    private final Map<String, Object> componentContainer;
    private final Petite petite;

    private Engine(final Petite petite) {
        this.petite = petite;
        this.templateCache = new ConcurrentHashMap<String, Template>();
        this.componentContainer = new HashMap<String, Object>();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        this.logger = (Logger) newComponentInstance(this.loggerType, webit.script.loggers.impl.NOPLogger.class);
        this.nativeFactory = (NativeFactory) newComponentInstance(this.nativeFactoryType, webit.script.asm.AsmNativeFactory.class);
        this.resolverManager = (ResolverManager) newComponentInstance(this.resolverManagerType, webit.script.asm.AsmResolverManager.class);
        this.coderFactory = (CoderFactory) newComponentInstance(this.coderFactoryType, webit.script.io.charset.impl.DefaultCoderFactory.class);
        this.nativeSecurityManager = (NativeSecurityManager) newComponentInstance(this.nativeSecurityManagerType, webit.script.security.impl.NoneNativeSecurityManager.class);
        this.textStatementFactory = (TextStatementFactory) newComponentInstance(this.textStatementFactoryType, webit.script.core.text.impl.SimpleTextStatementFactory.class);
        this.resourceLoader = (Loader) newComponentInstance(this.resourceLoaderType, webit.script.loaders.impl.ClasspathLoader.class);
        this.globalManager = (GlobalManager) newComponentInstance(this.globalManagerType, webit.script.global.GlobalManager.class);

        resolveComponent(this.logger, this.loggerType);
        this.petite.setLogger(this.logger);

        resolveComponent(this.nativeFactory, this.nativeFactoryType);
        resolveComponent(this.resolverManager, this.resolverManagerType);
        resolveComponent(this.coderFactory, this.coderFactoryType);
        resolveComponent(this.nativeSecurityManager, this.nativeSecurityManagerType);
        resolveComponent(this.textStatementFactory, this.textStatementFactoryType);
        resolveComponent(this.resourceLoader, this.resourceLoaderType);
        resolveComponent(this.globalManager, this.globalManagerType);

        this.globalManager.commit();
    }

    private void executeInitTemplates() throws ResourceNotFoundException {
        if (this.inits != null) {
            final Out out = new DiscardOut();
            final GlobalManager myGlobalManager = this.globalManager;
            final Bag globalBag = myGlobalManager.getGlobalBag();
            final Bag constBag = myGlobalManager.getGlobalBag();
            final KeyValues params = KeyValuesUtil.wrap(
                    new String[]{"GLOBAL", "CONST"},
                    new Object[]{globalBag, constBag}
            );
            for (String templateName : StringUtil.toArray(this.inits)) {
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Merge init template: {}", templateName);
                }
                this.getTemplate(templateName)
                        .merge(params, out);
                //Commit Global
                myGlobalManager.commit();
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
        return this.petite.get(key);
    }

    /**
     *
     * @since 1.4.0
     */
    private void resolveComponent(final Object bean, final ClassEntry type) {
        String profile = type != null
                ? type.profile
                : this.petite.resolveBeanName(bean.getClass());
        this.petite.wireBean(profile, bean);
        if (bean instanceof Initable) {
            ((Initable) bean).init(this);
        }
        this.componentContainer.put(profile, bean);
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
        Object bean = this.componentContainer.get(type.profile);
        if (bean == null) {
            bean = ClassUtil.newInstance(type.value);
            resolveComponent(bean, type);
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

    /**
     * if exists this resource.
     *
     * @param resourceName
     * @return boolean
     * @since 1.4.1
     */
    public boolean exists(final String resourceName) {
        final String normalizedName;
        final Loader loader;
        if ((normalizedName = (loader = this.resourceLoader).normalize(resourceName)) != null) {
            return loader.get(normalizedName).exists();
        }
        return false;
    }

    private Template createTemplateIfAbsent(final String name) throws ResourceNotFoundException {
        Template template;
        final Loader loader = this.resourceLoader;
        final String normalizedName = loader.normalize(name);
        if (normalizedName == null) {
            //if normalized-name is null means not found resource.
            throw new ResourceNotFoundException("Illegal template path: ".concat(name));
        }
        template = this.templateCache.get(normalizedName);
        if (template == null) {
            //then create Template
            template = new Template(this, normalizedName,
                    loader.get(normalizedName));
            if (loader.isEnableCache(normalizedName)) {
                Template oldTemplate;
                oldTemplate = this.templateCache.putIfAbsent(normalizedName, template);
                //if old Template exist, use the old one
                if (oldTemplate != null) {
                    template = oldTemplate;
                }
                if (!name.equals(normalizedName)) {
                    // cache Template with un-normalized name
                    oldTemplate = this.templateCache.putIfAbsent(name, template);
                    if (oldTemplate != null) {
                        template = oldTemplate;
                    }
                }
            }
        }
        return template;
    }

    public boolean checkNativeAccess(String path) {
        return this.nativeSecurityManager.access(path);
    }

    /**
     * @since 1.5.0
     */
    public void setNativeFactory(ClassEntry nativeFactory) {
        this.nativeFactoryType = nativeFactory;
    }

    /**
     * @since 1.5.0
     */
    public void setResolverManager(ClassEntry resolverManager) {
        this.resolverManagerType = resolverManager;
    }

    public void setNativeSecurityManager(ClassEntry nativeSecurityManager) {
        this.nativeSecurityManagerType = nativeSecurityManager;
    }

    public void setCoderFactory(ClassEntry coderFactory) {
        this.coderFactoryType = coderFactory;
    }

    public void setResourceLoader(ClassEntry resourceLoader) {
        this.resourceLoaderType = resourceLoader;
    }

    public void setTextStatementFactory(ClassEntry textStatementFactory) {
        this.textStatementFactoryType = textStatementFactory;
    }

    public void setGlobalManager(ClassEntry globalManager) {
        this.globalManagerType = globalManager;
    }

    public void setLogger(ClassEntry logger) {
        this.loggerType = logger;
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
            this.encoding = internEncoding(encoding);
        }
        //else ignore
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

    public GlobalManager getGlobalManager() {
        return globalManager;
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

    public void setVars(String vars) {
        this.vars = StringUtil.toArray(vars);
    }

    public String[] getAssistantSuffixs() {
        return assistantSuffixs;
    }

    public void setAssistantSuffixs(String assistantSuffixs) {
        this.assistantSuffixs = StringUtil.toArray(assistantSuffixs);
    }

    public void setInits(String inits) {
        this.inits = inits;
    }

    /**
     * @since 1.5.0
     */
    private static Object newComponentInstance(ClassEntry classEntry, Class defaultType) {
        if (classEntry == null) {
            return ClassUtil.newInstance(defaultType);
        }
        return ClassUtil.newInstance(classEntry.value);
    }

    public static Props createConfigProps(final String configPath) {
        return PropsUtil.loadFromClasspath(new Props(), CFG.DEFAULT_WIM, configPath);
    }

    /**
     * Create a Engine with default configPath.
     *
     * @return
     * @since 1.5.0
     */
    public static Engine create() {
        return create("", null);
    }

    /**
     * Create a Engine with given configPath.
     *
     * @param configPath
     * @return
     * @since 1.5.0
     */
    public static Engine create(final String configPath) {
        return create(configPath, null);
    }

    /**
     * Create a Engine with given configPath and extra-parameters.
     *
     * @param configPath
     * @param parameters
     * @return
     * @since 1.5.0
     */
    public static Engine create(final String configPath, final Map<String, Object> parameters) {
        return create(createConfigProps(configPath), parameters);
    }

    /**
     * Create a Engine with given baseProps and extra-parameters.
     *
     * @param props
     * @param parameters
     * @return
     * @since 1.5.0
     */
    public static Engine create(final Props props, final Map<String, Object> parameters) {

        final Petite petite = new Petite();
        petite.set(props, parameters);

        final Engine engine = new Engine(petite);
        petite.wireBean(engine);

        engine.init();
        if (engine.getLogger().isInfoEnabled()) {
            engine.getLogger().info("Loaded props: {}", petite.get(CFG.WIM_FILE_LIST));
        }
        try {
            engine.executeInitTemplates();
        } catch (ResourceNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        return engine;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Engine createEngine(final String configPath) {
        return create(configPath, null);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Engine createEngine(final String configPath, final Map<String, Object> parameters) {
        return create(configPath, parameters);
    }

    /**
     *
     * @deprecated
     */
    @Deprecated
    public static Engine createEngine(final Props props, final Map<String, Object> parameters) {
        return create(props, parameters);
    }

    /**
     * Intern encoding
     *
     * @param encoding
     * @return
     * @since 1.5.0
     */
    public static String internEncoding(final String encoding) {
        try {
            return Charset.forName(encoding).name();
        } catch (Exception e) {
            return encoding.intern();
        }
    }
}
