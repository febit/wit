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
package org.febit.wit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReservedNames {

    /**
     * Used for setup scripts, to register global variables.
     */
    public static final String GLOBAL = "GLOBAL";
    /**
     * Used for setup scripts, to register global constants.
     */
    public static final String CONST = "CONST";

    /**
     * Used in functions, to access all arguments as an array.
     */
    public static final String ARGUMENTS = "arguments";

    /**
     * Used int loops, to access the current iteration.
     *
     * @see org.febit.wit.runtime.iter.Iter
     * @see org.febit.wit.runtime.iter.KeyIter
     */
    public static final String FOR_ITER = "for.iter";

    /**
     * Used in native expressions, to get a class reference.
     * example: `native java.util.List.class`
     */
    public static final String CLASS = "class";
    /**
     * Used in native expressions, to get constructor function.
     * example: `native new java.util.ArrayList()`
     */
    public static final String NEW = "new";
}
