package webit.script.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import webit.script.Engine;
import webit.script.util.FastCharBuffer;
import webit.script.util.StringPool;
import webit.script.util.StringUtil;
import webit.script.util.props.Props;

/**
 *
 * @author Zqq
 */
public class ServletEngineUtil {

    private final static int BUFFER_SIZE = 3072;
    private final static String DEFAULT_WEB_PROPERTIES = "/webit-script-default-web.props";
    private final static String SERVLET_LOADER_SERVLETCONTEXT = "webit.script.web.loaders.ServletLoader.servletContext";
    private final static String WEB_ROOT_PREFIX = "%WEB_ROOT%/";

    public static Engine createEngine(final ServletContext servletContext, final String configFiles) {
        return createEngine(servletContext, configFiles, null);
    }

    @SuppressWarnings("unchecked")
    public static Engine createEngine(final ServletContext servletContext, final String configFiles, final Map extraSettings) {
        final Map settings;
        final Props props;
        loadFromServletContextPath(props = Engine.createConfigProps(DEFAULT_WEB_PROPERTIES), configFiles, servletContext);
        settings = new HashMap();
        settings.put(SERVLET_LOADER_SERVLETCONTEXT, servletContext);
        if (extraSettings != null) {
            settings.putAll(extraSettings);
        }
        return Engine.createEngine(props, settings);
    }

    public static void  loadFromServletContextPath(final Props props, final String pathSet, final ServletContext servletContext) {
        String[] paths;
        String path;

        FastCharBuffer charsBuffer = new FastCharBuffer();
        Reader reader;
        InputStream in;
        char[] buffer = new char[BUFFER_SIZE];
        int read;

        if (pathSet != null && pathSet.length() > 0) {
            paths = StringUtil.splitc(pathSet, ',');
            StringUtil.trimAll(paths);
            for (int j = 0, len = paths.length; j < len; j++) {
                path = paths[j];
                if (path != null && path.length() > 0) {
                    //load from servletContext
                    if (path.charAt(0) == '/') {
                        path = path.substring(1);
                    }
                    if ((in = servletContext.getResourceAsStream(path)) != null) {
                        try {

                            reader = new InputStreamReader(in, StringUtil.endsWithIgnoreCase(path, ".properties")
                                    ? StringPool.ISO_8859_1
                                    : StringPool.UTF_8);

                            charsBuffer.clear();

                            while ((read = reader.read(buffer, 0, BUFFER_SIZE)) >= 0) {
                                charsBuffer.append(buffer, 0, read);
                            }

                            props.load(charsBuffer.toString());
                            props.append(Engine.PROPS_FILE_LIST, WEB_ROOT_PREFIX.concat(path));
                        } catch (IOException ignore) {
                            //Note:ignore props IOException
                        } finally {
                            try {
                                in.close();
                            } catch (IOException ignore) {
                            }
                            charsBuffer.clear();
                        }
                    }//Note: else ignore not found props
                }
            }
        }
    }
}
