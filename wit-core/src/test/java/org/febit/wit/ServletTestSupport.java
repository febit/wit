/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.ee11.servlet.ServletContextHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.PathResourceFactory;
import org.febit.wit.exception.NoSuchSourceException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServletTestSupport {

    private static final Path WEBAPP_ROOT = Path.of("./src/test/jetty-webapp").toAbsolutePath();

    private static class ContextLazyHolder {
        private static final ServletContextHandler HANDLER;

        static {
            HANDLER = new ServletContextHandler("/");
            HANDLER.setBaseResource(new PathResourceFactory().newResource(WEBAPP_ROOT));
            HANDLER.addServlet(new WitServlet(), "*.whtml");
            HANDLER.addServlet(new OutFileServlet(), "*.out");
        }
    }

    private static class ServerLazyHolder {
        private static final Server SERVER;

        static {
            SERVER = new Server(0);
            SERVER.setHandler(ContextLazyHolder.HANDLER);
            try {
                SERVER.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    private static class OutFileServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            var path = req.getServletPath();
            var full = WEBAPP_ROOT.resolve(StringUtils.removeStart(path, '/'));
            if (!Files.exists(full)) {
                log.warn("[JETTY] No file found for path: {}", path);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found");
                return;
            }
            try (var in = Files.newInputStream(full)) {
                IOUtils.copy(in, resp.getOutputStream());
            } catch (IOException e) {
                log.error("[JETTY] Failed to serve file: " + path, e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to serve file");
            }
        }
    }

    private static class WitServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                throws ServletException, IOException {
            var path = req.getServletPath();
            log.info("[JETTY] Render WIT --> {}", path);

            try {
                var script = WitTestSupport.WIT.script("servlet:" + path);
                var inputs = Vars.of(Map.of(
                        "request", req,
                        "response", resp
                ));
                script.evaluator()
                        .out(resp.getOutputStream())
                        .inputs(inputs)
                        .eval();
            } catch (NoSuchSourceException e) {
                log.warn("[JETTY] No script found for path: {}", path, e);
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Not found");
            } catch (Exception e) {
                log.error("[JETTY] Failed to process request: {}", path, e);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to process request");
            }
        }
    }

    public static URI uri() {
        return ServerLazyHolder.SERVER.getURI();
    }

    public static int port() {
        return ServerLazyHolder.SERVER.getURI().getPort();
    }

    public static ServletContext context() {
        return ContextLazyHolder.HANDLER.getServletContext();
    }

//    private static ServletContext createServletContext(Path baseDir) {
//        var ctx = mock(ServletContext.class);
//        when(ctx.getContextPath()).thenReturn("");
//        when(ctx.getVirtualServerName()).thenReturn("wit-test");
//        when(ctx.getServerInfo()).thenReturn("TestWit-Servlet");
//        when(ctx.getMajorVersion()).thenReturn(6);
//        when(ctx.getEffectiveMajorVersion()).thenReturn(6);
//        when(ctx.getMinorVersion()).thenReturn(1);
//        when(ctx.getEffectiveMinorVersion()).thenReturn(1);
//        when(ctx.getResourcePaths(anyString())).thenReturn(Collections.emptySet());
//
//        when(ctx.getRealPath(anyString())).thenAnswer(inv ->
//                resolveServletPath(baseDir, inv.getArgument(0)).toString());
//        try {
//            when(ctx.getResource(anyString())).thenAnswer(inv ->
//                    toUrl(resolveServletPath(baseDir, inv.getArgument(0))));
//        } catch (MalformedURLException e) {
//            throw new IllegalStateException("Unexpected URL resolution failure", e);
//        }
//        when(ctx.getResourceAsStream(anyString())).thenAnswer(inv ->
//                openStream(resolveServletPath(baseDir, inv.getArgument(0)))
//        );
//
//        var attributes = new ConcurrentHashMap<String, Object>();
//        when(ctx.getAttributeNames()).thenAnswer(inv -> Collections.enumeration(attributes.keySet()));
//        when(ctx.getAttribute(anyString())).thenAnswer(inv -> attributes.get(inv.getArgument(0)));
//
//        doAnswer(inv -> {
//            var key = inv.getArgument(0, String.class);
//            if (key != null) {
//                attributes.put(key, inv.getArgument(1));
//            }
//            return null;
//        }).when(ctx).setAttribute(anyString(), any());
//
//        doAnswer(inv -> {
//            var key = inv.getArgument(0, String.class);
//            if (key != null) {
//                attributes.remove(key);
//            }
//            return null;
//        }).when(ctx).removeAttribute(anyString());
//
//        return ctx;
//    }
//
//    private static Path resolveServletPath(Path baseDir, @Nullable String raw) {
//        var relative = trimLeadingSlash(raw == null ? "/" : raw);
//        return relative.isEmpty()
//                ? baseDir
//                : baseDir.resolve(relative);
//    }
//
//    private static String trimLeadingSlash(String path) {
//        int index = 0;
//        while (index < path.length() && path.charAt(index) == '/') {
//            index++;
//        }
//        return index == 0 ? path : path.substring(index);
//    }
//
//    @Nullable
//    private static URL toUrl(Path path) {
//        if (!Files.exists(path)) {
//            return null;
//        }
//        try {
//            return path.toUri().toURL();
//        } catch (MalformedURLException e) {
//            throw new IllegalStateException("Failed to resolve servlet resource: " + path, e);
//        }
//    }
//
//    @Nullable
//    private static InputStream openStream(Path path) {
//        if (!Files.exists(path)) {
//            return null;
//        }
//        try {
//            return Files.newInputStream(path);
//        } catch (IOException e) {
//            throw new UncheckedIOException("Failed to open servlet resource: " + path, e);
//        }
//    }

}
