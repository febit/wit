package org.febit.wit.runtime.accessor;

public interface AccessorFactory {

    <T> Getter<T> getter(Class<T> type);

    <T> Setter<T> setter(Class<T> type);

    <T> Render<T> render(Class<T> type);
}
