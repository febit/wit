// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

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
import webit.script.resolvers.ResolverManager;
import webit.script.security.NativeSecurityManager;
import webit.script.util.ClassEntry;
import webit.script.util.ClassUtil;
import webit.script.util.EncodingPool;
import webit.script.util.Petite;
import webit.script.util.PropsUtil;
import webit.script.util.SimpleBag;
import webit.script.util.StringUtil;
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
    private ClassEntry _resourceLoader;
    private ClassEntry _filter;
    private ClassEntry _textStatementFactory;
    private ClassEntry _nativeSecurityManager;
    private ClassEntry _coderFactory;
    private ClassEntry _globalManager;
    private ClassEntry _logger;
    private String encoding = EncodingPool.UTF_8;
    private boolean looseVar = false;
    private boolean shareRootData = true;
    private boolean trimCodeBlockBlankLine = true;
    private boolean appendLostSuffix = false;
    private String suffix = DEFAULT_SUFFIX;
    private String[] vars = null;
    private String[] assistantSuffixs = null;
    private String inits = null;
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

        if (this._logger == null) {
            this._logger = ClassEntry.wrap(webit.script.loggers.impl.NOPLogger.class);
        }
        this.logger = (Logger) ClassUtil.newInstance(this._logger);

        if (this._coderFactory == null) {
            this._coderFactory = ClassEntry.wrap(webit.script.io.charset.impl.DefaultCoderFactory.class);
        }
        this.coderFactory = (CoderFactory) ClassUtil.newInstance(this._coderFactory);

        if (this._nativeSecurityManager == null) {
            this._nativeSecurityManager = ClassEntry.wrap(webit.script.security.impl.NoneNativeSecurityManager.class);
        }
        this.nativeSecurityManager = (NativeSecurityManager) ClassUtil.newInstance(this._nativeSecurityManager);

        if (this._textStatementFactory == null) {
            this._textStatementFactory = ClassEntry.wrap(webit.script.core.text.impl.SimpleTextStatementFactory.class);
        }
        this.textStatementFactory = (TextStatementFactory) ClassUtil.newInstance(this._textStatementFactory);

        if (this._resourceLoader == null) {
            this._resourceLoader = ClassEntry.wrap(webit.script.loaders.impl.ClasspathLoader.class);
        }
        this.resourceLoader = (Loader) ClassUtil.newInstance(this._resourceLoader);

        if (this._globalManager == null) {
            this._globalManager = ClassEntry.wrap(webit.script.global.DefaultGlobalManager.class);
        }
        this.globalManager = (GlobalManager) ClassUtil.newInstance(this._globalManager);

        if (this._filter != null) {
            this.filter = (Filter) ClassUtil.newInstance(this._filter);
        }

        resolveComponent(this.logger, this._logger);
        this.petite.setLogger(this.logger);

        resolveComponent(this.nativeFactory);
        resolveComponent(this.resolverManager);
        resolveComponent(this.coderFactory, this._coderFactory);
        resolveComponent(this.nativeSecurityManager, this._nativeSecurityManager);
        resolveComponent(this.textStatementFactory, this._textStatementFactory);
        resolveComponent(this.resourceLoader, this._resourceLoader);
        if (this.filter != null) {
            resolveComponent(this.filter, this._filter);
        }

        resolveComponent(this.globalManager, this._globalManager);
        this.globalManager.commit();
    }

    private void executeInitTemplates() throws ResourceNotFoundException {
        if (this.inits != null) {
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
            for (String templateName : StringUtil.splitc(this.inits)) {
                if ((templateName = templateName.trim()).length() != 0) {
                    if (this.logger.isInfoEnabled()) {
                        this.logger.info("Merge init template: " + templateName);
                    }
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

    public void setNativeSecurityManager(ClassEntry _nativeSecurityManager) {
        this._nativeSecurityManager = _nativeSecurityManager;
    }

    public void setCoderFactory(ClassEntry _coderFactory) {
        this._coderFactory = _coderFactory;
    }

    public void setResourceLoader(ClassEntry _resourceLoader) {
        this._resourceLoader = _resourceLoader;
    }

    public void setTextStatementFactory(ClassEntry _textStatementFactory) {
        this._textStatementFactory = _textStatementFactory;
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

    public void setFilter(ClassEntry _filter) {
        this._filter = _filter;
    }

    public void setGlobalManager(ClassEntry _globalManager) {
        this._globalManager = _globalManager;
    }

    public GlobalManager getGlobalManager() {
        return globalManager;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setLogger(ClassEntry _logger) {
        this._logger = _logger;
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
        this.vars = StringUtil.splitAndRemoveBlank(vars);
    }

    public String[] getAssistantSuffixs() {
        return assistantSuffixs;
    }

    public void setAssistantSuffixs(String assistantSuffixs) {
        this.assistantSuffixs = StringUtil.splitAndRemoveBlank(assistantSuffixs);
    }

    public void setInits(String inits) {
        this.inits = inits;
    }
}
