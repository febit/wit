package org.febit.wit.accessor;

import org.febit.wit.accessor.impl.BeanReflectAccessor;

public class ReflectBeanAccessorFactory implements AccessorFactory {

    private static final BeanReflectAccessor ACCESSOR = new BeanReflectAccessor();

    @Override
    @SuppressWarnings("unchecked")
    public <T> Getter<T> getter(Class<T> type) {
        return (Getter<T>) ACCESSOR;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Setter<T> setter(Class<T> type) {
        return (Setter<T>) ACCESSOR;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Render<T> render(Class<T> type) {
        return(Render<T>) ACCESSOR;
    }
}
