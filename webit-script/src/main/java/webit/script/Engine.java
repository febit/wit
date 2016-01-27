// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
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
import webit.script.util.ArrayUtil;
import webit.script.util.ClassUtil;
import webit.script.util.KeyValuesUtil;
import webit.script.util.Props;
import webit.script.util.PropsUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public final class Engine {

    public static final String UTF_8 = internEncoding("UTF-8");

    private final ConcurrentMap<String, Template> templateCache = new ConcurrentHashMap<String, Template>();
    private final Map<Class, Object> components = new HashMap<Class, Object>();
    private final Map<String, Object> beans = new HashMap<String, Object>();
    private final Map<String, Object> datas = new HashMap<String, Object>();
    private final Map<String, Entry> configEntrys = new HashMap<String, Entry>();

    private boolean looseVar;
    private boolean shareRootData = true;
    private boolean trimCodeBlockBlankLine = true;
    private String encoding = UTF_8;
    private String inits;
    private String[] vars;

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
        this.encoding = internEncoding(encoding);
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
                if (this.logger.isInfoEnabled()) {
                    this.logger.info("Merge init template: {}", templateName);
                }
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
     * Get component or bean by type
     *
     * @param <T>
     * @param type
     * @return Component
     * @since 2.0
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final Class<T> type) {
        T bean = (T) components.get(type);
        if (bean != null) {
            return bean;
        }
        return (T) get(type.getName());
    }

    /**
     * Get bean by name
     *
     * @param name
     * @return bean
     * @since 2.0
     */
    public Object get(final String name) {
        Object bean = this.beans.get(name);
        if (bean != null) {
            return bean;
        }
        return createBeanIfAbsent(name);
    }

    public void config(Props props, Map<String, Object> parameters) {
        if (props == null) {
            props = new Props();
        }
        final Map<String, Object> extras;
        if (parameters != null) {
            extras = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                String key = entry.getKey();
                if (key == null) {
                    continue;
                }
                key = key.trim();
                Object value = entry.getValue();
                if (value instanceof String) {
                    int len = key.length();
                    if (len > 0) {
                        if (key.charAt(len - 1) == '+') {
                            props.append(key.substring(0, len - 1).trim(), (String) value);
                        } else {
                            props.set(key, (String) value);
                        }
                    }
                } else {
                    extras.put(key, value);
                }
            }
        } else {
            extras = null;
        }

        for (String key : props.keySet()) {
            putProp(key, props.get(key));
        }

        if (extras != null) {
            for (Map.Entry<String, Object> entrySet : extras.entrySet()) {
                putProp(entrySet.getKey(), entrySet.getValue());
            }
        }
    }

    public Object getConfig(String key) {
        return this.datas.get(key);
    }

    private void putProp(String key, Object value) {
        this.datas.put(key, value);
        int index = key.lastIndexOf('.');
        int index2;
        if (index > 0
                && (index2 = index + 1) < key.length()
                && key.charAt(index2) != '@') {
            String beanName = key.substring(0, index);
            this.configEntrys.put(beanName,
                    new Entry(key.substring(index2), value, this.configEntrys.get(beanName)));
        }
    }

    public void addComponent(Object bean) {
        //regist all impls
        for (Class cls : ClassUtil.impls(bean.getClass())) {
            this.components.put(cls, bean);
        }
    }

    public void initComponents() {
        addComponent(this);
        Object globalRaw = datas.get("@global");
        if (globalRaw != null) {
            final String[] beanNames = StringUtil.toArray(globalRaw.toString());
            final int size = beanNames.length;
            if (size != 0) {
                final Object[] globalBeans = new Object[size];
                for (int i = 0; i < size; i++) {
                    String name = beanNames[i];
                    Object bean = newInstance(name);
                    globalBeans[i] = bean;
                    this.beans.put(name, bean);
                    addComponent(bean);
                }
                inject("engine", this);
                for (int i = 0; i < size; i++) {
                    inject(beanNames[i], globalBeans[i]);
                }
            }
        }
        this.logger = get(Logger.class);
    }

    private Object newInstance(String key) {
        String type;
        do {
            type = key;
            key = (String) this.datas.get(key + ".@class");
        } while (key != null);
        return ClassUtil.newInstance(type);
    }

    private synchronized Object createBeanIfAbsent(String key) {
        Object bean = this.beans.get(key);
        if (bean != null) {
            return bean;
        }
        bean = newInstance(key);
        inject(key, bean);
        this.beans.put(key, bean);
        return bean;
    }

    public void inject(String key, final Object bean) {
        LinkedList<String> keyList = new LinkedList<String>();
        do {
            keyList.addFirst(key);
            key = (String) this.datas.get(key + ".@class");
        } while (key != null);

        String[] keys = keyList.toArray(new String[keyList.size()]);

        Map<String, Field> fields = ClassUtil.getSetableMemberFields(bean.getClass());
        //global
        for (Field field : fields.values()) {
            Object comp = this.components.get(field.getType());
            if (comp != null) {
                try {
                    field.set(bean, comp);
                } catch (Exception ex) {
                    //shouldn't be
                    throw new RuntimeException(ex);
                }
            }
        }

        Set<String> injected = new HashSet<String>();
        for (String profile : keys) {
            inject(profile, bean, injected, fields);
        }

        //Init
        for (Method method : bean.getClass().getMethods()) {
            if (method.getAnnotation(Init.class) != null) {
                final Class[] argTypes = method.getParameterTypes();
                final Object[] args;
                if (argTypes.length == 0) {
                    args = ArrayUtil.EMPTY_OBJECTS;
                } else {
                    args = new Object[argTypes.length];
                    for (int i = 0; i < argTypes.length; i++) {
                        args[i] = this.components.get(argTypes[i]);
                    }
                }
                try {
                    method.invoke(bean, args);
                } catch (Exception ex) {
                    //shouldn't be
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private void inject(final String beanName, final Object bean, final Set<String> injected, final Map<String, Field> fields) {

        if (injected.contains(beanName)) {
            return;
        }
        injected.add(beanName);
        //inject @extends first
        Object extendProfiles = datas.get(beanName.concat(".@extends"));
        if (extendProfiles != null) {
            for (String profile : StringUtil.toArray(String.valueOf(extendProfiles))) {
                inject(profile, bean, injected, fields);
            }
        }

        Entry entry = configEntrys.get(beanName);
        while (entry != null) {
            try {
                Field field = fields.get(entry.name);
                if (field == null) {
                    if (logger != null) {
                        logger.warn("Not found field {}#{} ", bean.getClass(), entry.name);
                    }
                } else {
                    Object value = entry.value;
                    if (value instanceof String) {
                        value = convert((String) value, field.getType());
                    }
                    field.set(bean, value);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            entry = entry.next;
        }
    }

    private Object convert(String string, Class cls) {
        if (cls == String.class) {
            return string;
        }
        if (cls == int.class) {
            if (string == null || string.length() == 0) {
                return 0;
            }
            return Integer.valueOf(string);
        }
        if (cls == boolean.class) {
            return StringUtil.toBoolean(string);
        }
        if (string == null) {
            return null;
        }
        if (cls.isArray()) {
            final String[] strings = StringUtil.toArray(string);
            final int len = strings.length;
            if (cls == String[].class) {
                return strings;
            }
            if (cls == Class[].class) {
                final Class[] entrys = new Class[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = ClassUtil.getClass(strings[i]);
                }
                return entrys;
            }
            if (cls == int[].class) {
                final int[] entrys = new int[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = Integer.valueOf(strings[i]);
                }
                return entrys;
            }
            if (cls == Integer[].class) {
                final Integer[] entrys = new Integer[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = Integer.valueOf(strings[i]);
                }
                return entrys;
            }
            if (cls == boolean[].class) {
                final boolean[] entrys = new boolean[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = StringUtil.toBoolean(strings[i]);
                }
                return entrys;
            }
            if (cls == Boolean[].class) {
                final Boolean[] entrys = new Boolean[len];
                for (int i = 0; i < len; i++) {
                    entrys[i] = StringUtil.toBoolean(strings[i]);
                }
                return entrys;
            }
            Object[] array = (Object[]) Array.newInstance(cls.getComponentType(), len);
            for (int i = 0; i < len; i++) {
                array[i] = get(strings[i]);
            }
            return array;
        } else {
            if (cls == Boolean.class) {
                return StringUtil.toBoolean(string);
            }
            if (string.length() == 0) {
                return null;
            }
            if (cls == Class.class) {
                return ClassUtil.getClass(string);
            }
            if (cls == Integer.class) {
                return Integer.valueOf(string);
            }
            return get(string);
        }
    }

    public boolean checkNativeAccess(String path) {
        return this.nativeSecurityManager.access(path);
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

    public String getEncoding() {
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

        final Engine engine = new Engine();
        engine.config(props, parameters);
        engine.initComponents();

        if (engine.getLogger().isInfoEnabled()) {
            engine.getLogger().info("Loaded props: {}", props.get(CFG.WIM_FILE_LIST));
        }
        engine.executeInits();
        return engine;
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

    private static final class Entry {

        final String name;
        final Object value;
        final Entry next;

        Entry(String name, Object value, Entry next) {
            this.name = name;
            this.value = value;
            this.next = next;
        }
    }
}
