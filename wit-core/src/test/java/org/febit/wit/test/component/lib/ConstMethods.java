// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component.lib;

import org.febit.wit.runtime.Undefined;
import org.febit.wit.runtime.WitFunction;

import java.util.Map;

@SuppressWarnings("unused")
public class ConstMethods {

    public static final String CONST_FIELD = "CONST_FIELD";

    /**
     * A empty function, do nothing.
     */
    public static final WitFunction.Constable noop = args -> Undefined.UNDEFINED;

    public static final WitFunction.Constable CONST_METHOD = args -> "CONST_METHOD";

    public static String constEmpty() {
        return "constEmpty";
    }

    public static void constVoid() {
        // do nothing
    }

    public static WitFunction constMethod() {
        return CONST_METHOD;
    }

    public static int constSize(String obj) {
        return obj.length();
    }

    public static int constSize(Map<?, ?> obj) {
        return obj.size();
    }

    public static int constSize(Object[] arr) {
        return arr.length;
    }

}
