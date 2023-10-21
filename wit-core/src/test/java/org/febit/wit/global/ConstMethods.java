// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global;

import org.febit.wit.Context;
import org.febit.wit.lang.FunctionDeclare;

import java.util.Map;

/**
 * @author zqq90
 */
public class ConstMethods {

    public static final String CONST_FIELD = "CONST_FIELD";

    /**
     * A empty function, do nothing.
     */
    public static final FunctionDeclare noop = (context, args) -> Context.VOID;

    public static final FunctionDeclare CONST_METHOD = (context, args) -> "CONST_METHOD";

    public static String constEmpty() {
        return "constEmpty";
    }

    public static void constVoid() {
    }

    public static FunctionDeclare constMethod() {
        return CONST_METHOD;
    }

    public static int constSize(String obj) {
        return obj.length();
    }

    public static int constSize(Map obj) {
        return obj.size();
    }

    public static int constSize(Object[] arr) {
        return arr.length;
    }

}
