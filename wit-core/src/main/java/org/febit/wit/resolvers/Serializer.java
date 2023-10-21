// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.resolvers;

import org.febit.wit.io.Out;

public interface Serializer<T> extends Resolver<T> {

    void render(Out out, T bean);
}
