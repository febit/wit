package org.febit.wit.runtime.accessor;

@FunctionalInterface
public
interface AccessorConsumer {
    <T> void accept(Class<T> type, Accessor<? extends T> accessor);
}
