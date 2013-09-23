// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.filters.impl;

import webit.script.Engine;
import webit.script.Initable;
import webit.script.filters.Filter;

/**
 *
 * @author Zqq
 */
public class MutiFilter implements Filter, Initable {

    //settings
    private Class[] filterClasses;
    //
    private Filter[] _Filters;

    public Object process(Object object) {
        final Filter[] filters = this._Filters;
        if (filters != null) {
            for (int i = 0; i < filters.length; i++) {
                object = filters[i].process(object);
            }
        }
        return object;
    }

    @SuppressWarnings("unchecked")
    public void init(Engine engine) {
        if (filterClasses != null) {
            _Filters = new Filter[filterClasses.length];
            try {
                for (int i = 0; i < filterClasses.length; i++) {
                    _Filters[i] = (Filter) engine.getBean(filterClasses[i]);
                }
            } catch (Throwable ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void setFilterClasses(Class[] filterClasses) {
        this.filterClasses = filterClasses;
    }
}
