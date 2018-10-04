// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.tools.cache;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.Serializable;
import java.util.Arrays;
import org.febit.wit.InternalContext;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;
import org.febit.wit.io.impl.OutputStreamOut;
import org.febit.wit.io.impl.WriterOut;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public class CacheGlobalRegister implements GlobalRegister {

    protected final static String DEFAULT_NAME = "cache";

    protected String name = DEFAULT_NAME;
    protected boolean registCacheClear = false;
    protected boolean registCacheRemove = true;

    protected CacheProvider cacheProvider;

    @Override
    public void regist(GlobalManager manager) {
        manager.setConst(name, new CacheMethodDeclare(cacheProvider));
        if (registCacheRemove) {
            manager.setConst(name + "_remove", new CacheRemoveMethodDeclare(cacheProvider));
        }
        if (registCacheClear) {
            manager.setConst(name + "_clear", new CacheClearMethodDeclare(cacheProvider));
        }
    }

    protected static class CacheClearMethodDeclare implements MethodDeclare {

        protected final CacheProvider cacheProvider;

        public CacheClearMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            this.cacheProvider.clear();
            return InternalContext.VOID;
        }
    }

    protected static class CacheRemoveMethodDeclare implements MethodDeclare {

        protected final CacheProvider cacheProvider;

        public CacheRemoveMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        @Override
        public Object invoke(InternalContext context, Object[] args) {
            this.cacheProvider.remove(args[0]);
            return InternalContext.VOID;
        }
    }

    protected static class CacheMethodDeclare implements MethodDeclare {

        protected final CacheProvider cacheProvider;

        public CacheMethodDeclare(CacheProvider cacheProvider) {
            this.cacheProvider = cacheProvider;
        }

        @Override
        public Object invoke(final InternalContext context, final Object[] args) {
            final int len;
            if (args == null || (len = args.length) == 0) {
                throw new ScriptRuntimeException("This method need 1 argument at least.");
            }
            final CachingEntry cachingEntry;
            final Object firstArgument = args[0];
            if (firstArgument instanceof MethodDeclare) {
                cachingEntry = buildIfAbsent(context, firstArgument, (MethodDeclare) firstArgument, args, 1);
            } else if (len > 1) {
                final Object secondArgument = args[1];
                if (secondArgument instanceof MethodDeclare) {
                    cachingEntry = buildIfAbsent(context, firstArgument, (MethodDeclare) secondArgument, args, 2);
                } else {
                    throw new ScriptRuntimeException("This method need a function argument at index 0 or 1.");
                }
            } else {
                throw new ScriptRuntimeException("This method need a function argument.");
            }
            context.write(cachingEntry.outed);
            return cachingEntry.returned;
        }

        protected CachingEntry buildIfAbsent(final InternalContext context,
                                             final Object key,
                                             final MethodDeclare methodDeclare,
                                             final Object[] args,
                                             final int argsStart) {

            CachingEntry result = (CachingEntry) this.cacheProvider.get(key);
            if (result != null) {
                return result;
            }
            final Object returned;
            final Object outted;

            final Object[] methodArgs = args.length > argsStart
                    ? Arrays.copyOfRange(args, argsStart, args.length)
                    : ArrayUtil.emptyObjects();

            if (context.isByteStream) {
                ByteArrayOutputStream out = new ByteArrayOutputStream(256);
                returned = context.temporaryOut(new OutputStreamOut(out, context.encoding, context.getEngine()),
                        c -> methodDeclare.invoke(c, methodArgs));
                outted = out.toByteArray();
            } else {
                CharArrayWriter writer = new CharArrayWriter(256);
                returned = context.temporaryOut(new WriterOut(writer, context.encoding, context.getEngine()),
                        c -> methodDeclare.invoke(c, methodArgs));
                outted = writer.toCharArray();
            }
            result = new CachingEntry(returned, outted);
            this.cacheProvider.put(key, result);
            return result;
        }
    }

    protected static final class CachingEntry implements Serializable {

        private static final long serialVersionUID = 1L;

        public final Object returned;
        public final Object outed;

        public CachingEntry(Object returned, Object outed) {
            this.returned = returned;
            this.outed = outed;
        }
    }
}
