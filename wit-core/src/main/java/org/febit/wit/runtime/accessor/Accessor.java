// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor;

/**
 * Object accessor.
 *
 * @param <T> the target type
 * @see Getter
 * @see Setter
 * @see Render
 */
@SuppressWarnings("unused")
public sealed interface Accessor<T>
        permits Getter, Setter, Render {

}
