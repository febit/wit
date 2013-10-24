// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.support.jodd.madvoc;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jodd.madvoc.ActionRequest;
import jodd.madvoc.ScopeType;
import jodd.madvoc.component.MadvocController;
import jodd.madvoc.meta.In;
import jodd.madvoc.result.ActionResult;
import webit.script.Engine;
import webit.script.util.FastByteArrayOutputStream;
import webit.script.web.ServletEngineUtil;

/**
 *
 * @author Zqq
 */
public class WebitScriptResult extends ActionResult {

    public static final String NAME = "wtl";
    protected int bufferSize = 1024;
    protected String configPath = "/WEB-INF/webit-script-web-page.props";
    //
    @In(scope = ScopeType.CONTEXT)
    protected MadvocController madvocController;
    private final ThreadLocal<SoftReference<FastByteArrayOutputStream>> CACHE;
    private Engine _engine;

    public WebitScriptResult() {
        super(NAME);
        this.CACHE = new ThreadLocal<SoftReference<FastByteArrayOutputStream>>();
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void resetEngine() {
        this._engine = null;
    }

    protected FastByteArrayOutputStream getBuffer() {
        SoftReference<FastByteArrayOutputStream> ref;
        FastByteArrayOutputStream buffer;
        if ((ref = this.CACHE.get()) == null || (buffer = ref.get()) == null) {
            this.CACHE.set(new SoftReference<FastByteArrayOutputStream>(buffer = new FastByteArrayOutputStream(this.bufferSize)));
        }
        buffer.reset();
        return buffer;
    }

    protected Engine getEngine() {
        Engine engine;
        if ((engine = this._engine) != null) {
            return engine;
        } else {
            Map<String, Object> settings = new HashMap<String, Object>();
            settings.put("webit.script.Engine.appendLostFileNameExtension", Boolean.TRUE);
            return this._engine = ServletEngineUtil.createEngine(this.madvocController.getApplicationContext(), this.configPath, settings);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void render(ActionRequest actionRequest, Object resultObject, String resultValue, String resultPath) throws Exception {
        final HttpServletResponse response = actionRequest.getHttpServletResponse();
        final HttpServletRequest request = actionRequest.getHttpServletRequest();

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
            getEngine()
                    .getTemplate(resultPath)
                    .merge(parameters,
                            buffer = getBuffer());
            response.setContentLength(buffer.size());
            buffer.writeTo(out = response.getOutputStream());
            buffer.reset();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                } catch (IOException ioex) {
                    // ignore
                }
                try {
                    out.close();
                } catch (IOException ioex) {
                    // ignore
                }
            }
        }
    }
}
