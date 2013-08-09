package webit.script.loaders.impl.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import webit.script.loaders.Resource;

/**
 *
 * @author Zqq
 */
public class FileResource implements Resource {

    private final String path;
    private final String encoding;
    private long lastLoadTime;

    public FileResource(String path, String encoding) {
        this.path = path;
        this.encoding = encoding;
    }

    public boolean isModified() {
        return lastLoadTime > new File(this.path).lastModified();
    }

    public Reader openReader() throws IOException {
        lastLoadTime = System.currentTimeMillis();
        InputStream inputStream = new FileInputStream(this.path);

        return encoding == null
                ? new InputStreamReader(inputStream)
                : new InputStreamReader(inputStream, encoding);
    }
}
