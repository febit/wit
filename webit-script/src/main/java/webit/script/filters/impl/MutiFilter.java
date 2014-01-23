// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.filters.impl;

import webit.script.Engine;
import webit.script.Initable;
import webit.script.filters.Filter;
import webit.script.util.ClassEntry;

/**
 *
 * @author Zqq
 */
public class MutiFilter implements Filter, Initable {

    //settings
    private ClassEntry[] filterClasses;
    //
    private Filter[] _filters;

    public Object process(Object object) {
        final Filter[] filters;
        if ((filters = this._filters) != null) {
            for (int i = 0, len = filters.length; i < len; i++) {
                object = filters[i].process(object);
            }
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    public void init(Engine engine) {
        ClassEntry[] classes;
        if ((classes = filterClasses) != null) {
            final int len;
            final Filter[] filters;
            _filters = filters = new Filter[len = classes.length];
            try {
                for (int i = 0; i < len; i++) {
                    filters[i] = (Filter) engine.getComponent(classes[i]);
                }
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void setFilterClasses(ClassEntry[] filterClasses) {
        this.filterClasses = filterClasses;
    }
}
