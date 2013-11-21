// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.web;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import webit.script.Template;
import webit.script.util.FastByteArrayOutputStream;
import webit.script.util.StreamUtil;

/**
 *
 * @author Zqq
 */
public class HttpServletTemplateRender {

    protected int bufferSize = 1024;

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @SuppressWarnings("unchecked")
    public void render(Template template, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("request", request);
        parameters.put("response", response);

        final Enumeration<String> enumeration = request.getAttributeNames();
        String key;
        while (enumeration.hasMoreElements()) {
            parameters.put(key = enumeration.nextElement(), request.getAttribute(key));
        }

        OutputStream out = null;
        try {
            final FastByteArrayOutputStream buffer;
            template.merge(parameters,
                    buffer = getBuffer());
            response.setContentLength(buffer.size());
            buffer.writeTo(out = response.getOutputStream());
            buffer.reset();
        } finally {
            StreamUtil.flushAndClose(out);
        }
    }

    protected final ThreadLocal<WeakReference<FastByteArrayOutputStream>> CACHE
            = new ThreadLocal<WeakReference<FastByteArrayOutputStream>>();

    protected FastByteArrayOutputStream getBuffer() {
        WeakReference<FastByteArrayOutputStream> ref;
        FastByteArrayOutputStream buffer;
        if ((ref = this.CACHE.get()) == null || (buffer = ref.get()) == null) {
            if (ref != null) {
                ref.clear();
            }
            this.CACHE.set(new WeakReference<FastByteArrayOutputStream>(buffer = new FastByteArrayOutputStream(this.bufferSize)));
        }
        buffer.reset();
        return buffer;
    }
}
