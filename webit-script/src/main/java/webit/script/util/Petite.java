// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.HashMap;
import java.util.Map;
import jodd.props.Props;
import webit.script.loggers.Logger;

/**
 *
 * @author zqq90
 */
public final class Petite {

    private final Map<String, Object> parameters;
    private Logger logger;

    public Petite() {
        this.parameters = new HashMap<String, Object>();
    }

    public String resolveBeanName(Class<?> type) {
        return type.getName();
    }

    public void wireBean(Object bean) {
        wireBean(resolveBeanName(bean.getClass()), bean);
    }

    public void wireBean(String beanName, final Object bean) {
        final String prefix;
        final int prefix_len;
        String parameterName;
        prefix_len = (prefix = beanName + '.').length();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            parameterName = entry.getKey();
            if (parameterName.startsWith(prefix)) {
                try {
                    //BeanUtil.setDeclaredPropertyForced(bean, parameterName.substring(prefix_len), entry.getValue());
                    BeanUtil.set(bean, parameterName.substring(prefix_len), entry.getValue(), true);
                } catch (Exception ex) {
                    //Log
                    if (logger != null) {
                        logger.warn("Failed to set declared property: " + parameterName, ex);
                    }
                }
            }
        }
    }

    public void defineParameters(Map<String, Object> parameters) {
        parameters.putAll(parameters);
    }

    public void defineParameters(Props props) {
        //XXX: profiles support
        //extractProps(final Map target, final String... profiles)
        props.extractProps(this.parameters);
    }

    public void defineParameter(String name, Object value) {
        parameters.put(name, value);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
