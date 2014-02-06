// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.cache;

import java.io.Serializable;
import webit.script.Context;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.io.Out;
import webit.script.io.impl.OutputStreamOut;
import webit.script.io.impl.WriterOut;
import webit.script.method.MethodDeclare;
import webit.script.tools.cache.impl.SimpleCacheProvider;
import webit.script.util.ArrayUtil;
import webit.script.util.ClassEntry;
import webit.script.util.FastByteArrayOutputStream;
import webit.script.util.FastCharArrayWriter;
import webit.script.util.SimpleBag;

/**
 *
 * @author zqq90
 */
public class CacheGlobalRegister implements GlobalRegister, Initable {

    protected final static String DEFAULT_NAME = "cache";
    protected String name = DEFAULT_NAME;
    protected ClassEntry cacheProvider;
    protected boolean registCacheClear = false;
    protected boolean registCacheRemove = true;
    //
    protected CacheProvider _cacheProvider;

    public void regist(GlobalManager manager) {

        final SimpleBag constBag = manager.getConstBag();

        constBag.set(name, new CacheMethodDeclare(_cacheProvider));
        if (registCacheRemove) {
            constBag.set(name + "_remove", new CacheRemoveMethodDeclare(_cacheProvider));
        }
        if (registCacheClear) {
            constBag.set(name + "_clear", new CacheClearMethodDeclare(_cacheProvider));
        }
    }

    public void init(Engine engine) {
        this._cacheProvider
                = (CacheProvider) engine.getComponent(this.cacheProvider != null ? this.cacheProvider : ClassEntry.wrap(SimpleCacheProvider.class));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegistCacheClear(boolean registCacheClear) {
        this.registCacheClear = registCacheClear;
    }

    public void setCacheProvider(ClassEntry cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    protected static class CacheClearMethodDeclare implements MethodDeclare {

        protected final CacheProvider cacheProvider;

        public CacheClearMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        public Object invoke(Context context, Object[] args) {
            this.cacheProvider.clear();
            return Context.VOID;
        }
    }

    protected static class CacheRemoveMethodDeclare implements MethodDeclare {

        protected final CacheProvider cacheProvider;

        public CacheRemoveMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        public Object invoke(Context context, Object[] args) {
            this.cacheProvider.remove(ArrayUtil.get(args, 0, null));
            return Context.VOID;
        }
    }

    protected static class CacheMethodDeclare implements MethodDeclare {

        protected final static Object[] EMPTY_ARRAY = new Object[0];
        protected final CacheProvider cacheProvider;

        public CacheMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        public Object invoke(final Context context, final Object[] args) {
            final int len;
            if (args == null || (len = args.length) == 0) {
                throw new ScriptRuntimeException("This method need 1 argument at least.");
            }
            final CachingEntry cachingEntry;
            final Object firstArgument = args[0];
            if (firstArgument instanceof MethodDeclare) {
                cachingEntry = buildIfAbent(context, firstArgument, (MethodDeclare) firstArgument, args, 1);
            } else if (len > 1) {
                final Object secondArgument;
                if ((secondArgument = args[1]) instanceof MethodDeclare) {
                    cachingEntry = buildIfAbent(context, firstArgument, (MethodDeclare) secondArgument, args, 2);
                } else {
                    throw new ScriptRuntimeException("This method need a function argument at index 0 or 1.");
                }
            } else {
                throw new ScriptRuntimeException("This method need a function argument.");
            }
            context.out(cachingEntry.outted);
            return cachingEntry.returned;
        }

        protected CachingEntry buildIfAbent(final Context context,
                final Object key,
                final MethodDeclare methodDeclare,
                final Object[] args,
                final int argsStart) {

            CachingEntry result;
            if ((result = (CachingEntry) this.cacheProvider.get(key)) == null) {
                final Object returned;
                final Object outted;
                final Out current;

                final Object[] methodArgs;

                final int len = args.length;
                if (len > argsStart) {
                    final int methodArgsLen = len - argsStart;
                    System.arraycopy(args, argsStart, methodArgs = new Object[methodArgsLen], 0, methodArgsLen);
                } else {
                    methodArgs = EMPTY_ARRAY;
                }

                if ((current = context.getOut()).isByteStream()) {
                    final FastByteArrayOutputStream out = new FastByteArrayOutputStream(256);

                    context.pushOut(new OutputStreamOut(out, (OutputStreamOut) current));

                    try {
                        returned = methodDeclare.invoke(context, methodArgs);
                    } finally {
                        context.popOut();
                    }
                    outted = out.toByteArray();
                } else {
                    final FastCharArrayWriter writer = new FastCharArrayWriter(256);

                    context.pushOut(current instanceof WriterOut
                            ? new WriterOut(writer, (WriterOut) current)
                            : new WriterOut(writer, context.encoding, context.template.engine.getCoderFactory()));

                    try {
                        returned = methodDeclare.invoke(context, methodArgs);
                        outted = writer.toCharArray();
                    } finally {
                        context.popOut();
                    }
                }
                this.cacheProvider.put(key, result = new CachingEntry(returned, outted));
            }
            return result;
        }

        protected static final class CachingEntry implements Serializable {

            private static final long serialVersionUID = 1L;

            protected final Object returned;
            protected final Object outted;

            public CachingEntry(Object returned, Object outted) {
                this.returned = returned;
                this.outted = outted;
            }
        }
    }
}
