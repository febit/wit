// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.web;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import webit.script.CFG;
import webit.script.Engine;
import webit.script.util.PropsUtil;
import webit.script.util.props.Props;

/**
 *
 * @author Zqq
 */
public class ServletEngineUtil {

    private final static String DEFAULT_WEB_PROPERTIES = "/webit-script-default-web.props";
    private final static String WEB_ROOT_PREFIX = "%WEB_ROOT%/";

    public static Engine createEngine(final ServletContext servletContext, final String configFiles) {
        return createEngine(servletContext, configFiles, null);
    }

    @SuppressWarnings("unchecked")
    public static Engine createEngine(final ServletContext servletContext, final String configFiles, final Map<String, Object> extraSettings) {
        final Map<String, Object> settings;
        final Props props;
        props = loadFromServletContextPath(Engine.createConfigProps(DEFAULT_WEB_PROPERTIES), servletContext, configFiles);
        settings = new HashMap<String, Object>();
        settings.put(CFG.SERVLET_CONTEXT, servletContext);
        if (extraSettings != null) {
            settings.putAll(extraSettings);
        }
        return Engine.createEngine(props, settings);
    }

    public static Props loadFromServletContextPath(final Props props, final ServletContext servletContext, final String... pathSets) {

        return PropsUtil.load(props, new PropsUtil.InputStreamResolver() {

            public InputStream openInputStream(String path) {
                if (path.charAt(0) == '/') {
                    path = path.substring(1);
                }
                final InputStream in;
                if ((in = servletContext.getResourceAsStream(path)) != null) {
                    return in;
                }
                try {
                    //try read file by real path
                    return new FileInputStream(servletContext.getRealPath(path));
                } catch (FileNotFoundException ignore) {
                }
                return null;
            }

            public String getViewPath(String path) {
                return WEB_ROOT_PREFIX.concat(path);
            }
        }, pathSets);
    }
}
