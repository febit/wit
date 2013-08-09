package webit.script.loaders.impl.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import webit.script.loaders.Resource;

/**
 *
 * @author Zqq
 */
public class ClasspathResource implements Resource {

    private final String path;
    private final String encoding;
    //private long lastLoadTime;

    public ClasspathResource(String path, String encoding) {
        this.path = path;
        this.encoding = encoding;
    }

    public boolean isModified() {
        return false;
    }

    public Reader openReader() throws IOException {
        //lastLoadTime = System.currentTimeMillis();
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

        return encoding == null
                ? new InputStreamReader(inputStream)
                : new InputStreamReader(inputStream, encoding);
    }
}
