// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.accessor;

import org.febit.wit.Out;

public non-sealed interface Render<T> extends Accessor<T> {

    void render(Out out, T obj);
}
