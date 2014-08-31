// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import java.util.HashMap;
import java.util.Map;
import webit.script.core.VariantIndexer;
import webit.script.core.ast.loop.LoopCtrl;
import webit.script.exceptions.NotFunctionException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.io.Out;
import webit.script.lang.KeyValueAccepter;
import webit.script.lang.KeyValues;
import webit.script.lang.MethodDeclare;
import webit.script.lang.Void;
import webit.script.resolvers.OutResolver;
import webit.script.resolvers.ResolverManager;
import webit.script.util.ClassMap;
import webit.script.util.KeyValuesUtil;

/**
 *
 * @author Zqq
 */
public final class Context implements KeyValueAccepter {

    public static final Void VOID = new Void();

    private Map<Object, Object> localMap;
    public final Context topContext;
    public final KeyValues rootParams;
    public final String encoding;
    public final LoopCtrl loopCtrl;
    public final Template template;
    public final ResolverManager resolverManager;
    public final ClassMap<OutResolver> outterMap;
    public final boolean isByteStream;

    public Object[] vars;
    public VariantIndexer[] indexers;
    public int indexer;
    public Out out;

    public Context(final Template template, final Out out, final KeyValues rootParams) {
        this.template = template;
        this.out = out;
        this.topContext = this;
        this.rootParams = rootParams;
        this.encoding = out.getEncoding();
        this.isByteStream = out.isByteStream();
        ResolverManager resolverManager = this.resolverManager = template.engine.getResolverManager();
        this.outterMap = resolverManager.outterMap;
        this.loopCtrl = new LoopCtrl();
    }

    public Context(final Context parent, final Template template, final KeyValues params) {
        this.template = template;
        this.out = parent.out;
        this.topContext = parent.topContext;
        this.rootParams = template.engine.isShareRootData()
                ? KeyValuesUtil.wrap(parent.rootParams, params)
                : params;
        this.encoding = parent.encoding;
        this.isByteStream = parent.isByteStream;
        this.resolverManager = parent.resolverManager;
        this.outterMap = parent.outterMap;
        this.loopCtrl = new LoopCtrl();
    }

    public void pushRootParams() {
        rootParams.exportTo(this);
    }

    public void set(String key, Object value) {
        int index = indexers[this.indexer].getIndex(key);
        if (index >= 0) {
            this.vars[index] = value;
        }
    }

    public void outNotNull(final byte[] bytes) {
        this.out.write(bytes);
    }

    public void outNotNull(final char[] chars) {
        this.out.write(chars);
    }

    public void out(final Object object) {
        if (object != null) {
            final Class type;
            if ((type = object.getClass()) == String.class) {
                this.out.write((String) object);
                return;
            }
            final OutResolver resolver;
            if ((resolver = this.outterMap.unsafeGet(type)) != null) {
                resolver.render(this.out, object);
                return;
            }
            this.resolverManager.resolveOutResolver(type).render(this.out, object);
        }
    }

    public Object getLocalVar(final Object key) {
        final Map<Object, Object> map;
        return (map = this.localMap) != null ? map.get(key) : null;
    }

    public void setLocalVar(final Object key, final Object value) {
        final Map<Object, Object> map;
        if ((map = this.localMap) != null) {
            map.put(key, value);
            return;
        }
        (this.localMap = new HashMap<Object, Object>()).put(key, value);
    }

    public void setArgumentsForFunction(final int argsCount, final Object[] args, final int start) {
        final int len;
        final Object[] values;
        if (((values = vars)[start] = args) != null
                && argsCount != 0
                && (len = args.length) != 0) {
            System.arraycopy(args, 0, values, start + 1, argsCount > len ? len : argsCount);
        }
    }

    public Object[] get(final String[] keys) {
        int i;
        final Object[] results = new Object[i = keys.length];
        while (i != 0) {
            --i;
            results[i] = get(keys[i], true);
        }
        return results;
    }

    public Object get(String key) {
        return get(key, true);
    }

    public Object get(String key, boolean force) {
        int index = indexers[this.indexer].getIndex(key);
        if (index >= 0) {
            return this.vars[index];
        }
        if (force) {
            throw new ScriptRuntimeException("Not found variant named:".concat(key));
        }
        return null;
    }

    /**
     * Export a named function.
     *
     * @since 1.5.0
     * @param name
     * @return
     * @throws NotFunctionException
     */
    public Function exportFunction(String name) throws NotFunctionException {
        Object func = get(name, false);
        if (func instanceof MethodDeclare) {
            return new Function(this.template, (MethodDeclare) func, this.encoding, this.isByteStream);
        }
        throw new NotFunctionException(func);
    }

    /**
     * Export vars to a given map.
     *
     * @param map
     */
    @SuppressWarnings("unchecked")
    public void exportTo(final Map map) {
        VariantIndexer indexer = indexers[this.indexer];
        String[] names = indexer.names;
        int[] indexs = indexer.indexs;
        Object[] vars = this.vars;
        for (int i = 0, len = names.length; i < len; i++) {
            map.put(names[i], vars[indexs[i]]);
        }
    }
}
