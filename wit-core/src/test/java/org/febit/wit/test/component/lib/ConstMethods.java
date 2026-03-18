/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
