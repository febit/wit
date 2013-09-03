// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.filters.impl;

import webit.script.Configurable;
import webit.script.Engine;
import webit.script.filters.Filter;

/**
 *
 * @author Zqq
 */
public class MutiFilter implements Filter, Configurable {

    //settings
    private Class[] filtersClass;
    //
    private Filter[] filters;

    public Object process(Object object) {
        Filter[] filters = this.filters;
        if (filters != null) {
            for (int i = 0; i < filters.length; i++) {
                Filter filter = filters[i];
                object = filter.process(object);
            }
        }
        return object;
    }

    public void init(Engine engine) {
        if (filtersClass != null) {
            filters = new Filter[filtersClass.length];
            try {
                for (int i = 0; i < filtersClass.length; i++) {
                    filters[i] = (Filter) engine.getBean(filtersClass[i]);
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    public void setFiltersClass(Class[] filtersClass) {
        this.filtersClass = filtersClass;
    }
}
