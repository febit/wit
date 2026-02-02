// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.extern.lib.tld;

import java.util.List;

@lombok.Builder(
        builderClassName = "Builder"
)
public record TldFunction(
        String name,
        String declaredClass,
        String returnType,
        String methodName,
        List<String> parameterTypes
) {
}
