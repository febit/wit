// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import org.febit.wit.exception.NotFunctionException;
import org.febit.wit.runtime.heap.Heap;
import org.febit.wit.runtime.heap.LocalHeap;

/**
 * Runtime context.
 *
 */
@SuppressWarnings({
        "squid:S1214", //Constants should not be defined in interfaces
        "squid:RedundantThrowsDeclarationCheck"
})
public interface Context {

    Heap heap();

    LocalHeap local();

    Function exportFunction(String name) throws NotFunctionException;

}
