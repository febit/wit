// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script;

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
import webit.script.util.InternedEncoding;
import webit.script.util.KeyValuesUtil;
import webit.script.util.Petite;
import webit.script.util.Props;
import webit.script.util.PropsUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public final class Engine {

    private final ConcurrentMap<String, Template> templateCache = new ConcurrentHashMap<>();

    private boolean looseVar;
    private boolean shareRootData = true;
    private boolean trimCodeBlockBlankLine = true;
    private InternedEncoding encoding;
    private String inits;
    private String[] vars;

    private Petite petite;
    private Logger logger;
    private Loader loader;
    private GlobalManager globalManager;
    private TextStatementFactory textStatementFactory;
    private NativeSecurityManager nativeSecurityManager;
    private CoderFactory coderFactory;
    private NativeFactory nativeFactory;
    private ResolverManager resolverManager;

    @Init
    public void init() {
    }

    private void executeInits() {
        if (this.inits != null) {
            final Out out = new DiscardOut();
            final GlobalManager myGlobalManager = this.globalManager;
            final Bag globalBag = myGlobalManager.getGlobalBag();
            final Bag constBag = myGlobalManager.getConstBag();
            final KeyValues params = KeyValuesUtil.wrap(
                    new String[]{"GLOBAL", "CONST"},
                    new Object[]{globalBag, constBag}
            );
            for (String templateName : StringUtil.toArray(this.inits)) {
                this.logger.info("Merge init template: {}", templateName);
                try {
                    this.getTemplate(templateName).merge(params, out);
                } catch (ResourceNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
                //Commit Global
                myGlobalManager.commit();
            }
        }
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
        return getTemplate(this.loader.concat(parentName, name));
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
        final Loader myLoader;
        if ((normalizedName = (myLoader = this.loader).normalize(resourceName)) != null) {
            return myLoader.get(normalizedName).exists();
        }
        return false;
    }

    private Template createTemplateIfAbsent(final String name) throws ResourceNotFoundException {
        Template template;
        final Loader myLoader = this.loader;
        final String normalizedName = myLoader.normalize(name);
        if (normalizedName == null) {
            //if normalized-name is null means not found resource.
            throw new ResourceNotFoundException("Illegal template path: ".concat(name));
        }
        template = this.templateCache.get(normalizedName);
        if (template == null) {
            //then newInstance Template
            template = new Template(this, normalizedName,
                    myLoader.get(normalizedName));
            if (myLoader.isEnableCache(normalizedName)) {
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

    /**
     * Get component or bean by type.
     *
     * @param <T>
     * @param type
     * @return Component
     * @since 2.0
     */
    public <T> T get(final Class<T> type) {
        return this.petite.get(type);
    }

    /**
     * Get bean by name.
     *
     * @param name
     * @return bean
     * @since 2.0
     */
    public Object get(final String name) {
        return this.petite.get(name);
    }

    public Object getConfig(String key) {
        return this.petite.getConfig(key);
    }

    public void addComponent(Object bean) {
        this.petite.addComponent(bean);
    }

    public boolean checkNativeAccess(String path) {
        return this.nativeSecurityManager.access(path);
    }

    public void inject(String key, final Object bean) {
        this.petite.inject(key, bean);
    }

    public CoderFactory getCoderFactory() {
        return coderFactory;
    }

    public boolean isLooseVar() {
        return looseVar;
    }

    public boolean isTrimCodeBlockBlankLine() {
        return trimCodeBlockBlankLine;
    }

    public Logger getLogger() {
        return logger;
    }

    public GlobalManager getGlobalManager() {
        return globalManager;
    }

    public TextStatementFactory getTextStatementFactory() {
        return textStatementFactory;
    }

    public NativeFactory getNativeFactory() {
        return nativeFactory;
    }

    public boolean isShareRootData() {
        return shareRootData;
    }

    public ResolverManager getResolverManager() {
        return resolverManager;
    }

    public InternedEncoding getEncoding() {
        return encoding;
    }

    public String[] getVars() {
        return vars;
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
        petite.config(props, parameters);
        petite.initComponents();

        final Engine engine = petite.get(Engine.class);
        engine.getLogger().info("Loaded props: {}", props.getModulesString());
        engine.executeInits();
        return engine;
    }

}
