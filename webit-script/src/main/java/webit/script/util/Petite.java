// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import java.util.HashMap;
import java.util.Map;
import webit.script.loggers.Logger;
import webit.script.util.bean.BeanUtil;
import webit.script.util.props.Props;

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
                        logger.warn("Failed to set declared property: ".concat(parameterName), ex);
                    }
                }
            }
        }
    }

    public void defineParameters(Map<String, Object> parameters) {
        parameters.putAll(parameters);
    }

    public void defineParameters(Props props, Map<String, Object> parameters) {
        if (props == null) {
            props = new Props();
        }
        final Map<String, Object> extraDirectParameters;
        if (parameters != null) {
            extraDirectParameters = new HashMap<String, Object>();
            String name;
            Object value;
            int len;
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                if ((name = entry.getKey()) == null) {
                    continue;
                }
                name = name.trim();
                value = entry.getValue();
                if (value instanceof String) {
                    if ((len = name.length()) > 0) {
                        if (name.charAt(len - 1) == '+') {
                            props.append(name.substring(0, len - 1).trim(), (String) value);
                        } else {
                            props.load(name, (String) value);
                        }
                    }
                } else {
                    extraDirectParameters.put(name, value);
                }
            }
        } else {
            extraDirectParameters = null;
        }
        //
        props.extractProps(this.parameters);
        if (extraDirectParameters != null && extraDirectParameters.size() > 0) {
            this.parameters.putAll(extraDirectParameters);
        }
    }

    public void defineParameter(String name, Object value) {
        parameters.put(name, value);
    }

    public Object getParameter(String name) {
        return parameters.get(name);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
