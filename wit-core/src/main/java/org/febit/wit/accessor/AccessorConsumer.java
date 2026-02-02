package org.febit.wit.accessor;

@FunctionalInterface
public
interface AccessorConsumer {
    <T> void accept(Class<T> type, Accessor<? extends T> accessor);
}
