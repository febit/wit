// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.runtime.accessor;

import org.febit.wit.io.Out;

public non-sealed interface Render<T> extends Accessor<T> {

    void render(Out out, T obj);
}
