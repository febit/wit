// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.core.text.TextStatementFactory;
import org.febit.wit.exceptions.IllegalConfigException;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.io.Out;
import org.febit.wit.io.charset.CoderFactory;
import org.febit.wit.io.impl.DiscardOut;
import org.febit.wit.lang.Bag;
import org.febit.wit.lang.KeyValues;
import org.febit.wit.loaders.Loader;
import org.febit.wit.loggers.Logger;
import org.febit.wit.resolvers.ResolverManager;
import org.febit.wit.security.NativeSecurityManager;
import org.febit.wit.util.InternedEncoding;
import org.febit.wit.util.KeyValuesUtil;
import org.febit.wit.util.Petite;
import org.febit.wit.util.Props;
import org.febit.wit.util.PropsUtil;
import org.febit.wit.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class Engine {

    protected final ConcurrentMap<String, Template> cachedTemplates = new ConcurrentHashMap<>();

    protected boolean looseVar;
    protected boolean shareRootData = true;
    protected boolean trimCodeBlockBlankLine = true;
    protected InternedEncoding encoding;
    protected String inits;
    protected String[] vars;

    protected Petite petite;
    protected Logger logger;
    protected Loader loader;
    protected GlobalManager globalManager;
    protected TextStatementFactory textStatementFactory;
    protected NativeSecurityManager nativeSecurityManager;
    protected CoderFactory coderFactory;
    protected NativeFactory nativeFactory;
    protected ResolverManager resolverManager;

    @Init
    public void init() {
        // Do nothing
    }

    protected void executeInits() throws ResourceNotFoundException {
        if (this.inits == null) {
            return;
        }
        final Out out = new DiscardOut();
        final Bag globalBag = this.globalManager.getGlobalBag();
        final Bag constBag = this.globalManager.getConstBag();
        final KeyValues params = KeyValuesUtil.wrap(
                new String[]{"GLOBAL", "CONST"},
                new Object[]{globalBag, constBag}
        );
        for (String templateName : StringUtil.toArray(this.inits)) {
            this.logger.info("Merge init template: {}", templateName);
            this.getTemplate(templateName).merge(params, out);
            // Commit after each init-template
            this.globalManager.commit();
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
        final Template template = this.cachedTemplates.get(name);
        if (template != null) {
            return template;
        }
        return createTemplateIfAbsent(name);
    }

    /**
     * if exists this resource.
     *
     * @param resourceName
     * @return boolean
     * @since 1.4.1
     */
    public boolean exists(final String resourceName) {
        final Loader myLoader = this.loader;
        final String normalizedName = myLoader.normalize(resourceName);
        if (normalizedName == null) {
            return false;
        }
        return myLoader.get(normalizedName).exists();
    }

    protected Template createTemplateIfAbsent(final String name) throws ResourceNotFoundException {
        Template template;
        final Loader myLoader = this.loader;
        final String normalizedName = myLoader.normalize(name);
        if (normalizedName == null) {
            //if normalized-name is null means not found resource.
            throw new ResourceNotFoundException("Illegal template path: ".concat(name));
        }
        template = this.cachedTemplates.get(normalizedName);
        if (template != null) {
            return template;
        }
        //then newInstance Template
        template = new Template(this, normalizedName, myLoader.get(normalizedName));
        Template oldTemplate;
        if (myLoader.isEnableCache(normalizedName)) {
            oldTemplate = this.cachedTemplates.putIfAbsent(normalizedName, template);
            //if old Template exist, use the old one
            if (oldTemplate != null) {
                template = oldTemplate;
            }
            if (!name.equals(normalizedName)) {
                // cache Template with un-normalized name
                oldTemplate = this.cachedTemplates.putIfAbsent(name, template);
                if (oldTemplate != null) {
                    template = oldTemplate;
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
        try {
            engine.executeInits();
        } catch (ResourceNotFoundException ex) {
            throw new IllegalConfigException("engine.inits", ex);
        }
        return engine;
    }

}
