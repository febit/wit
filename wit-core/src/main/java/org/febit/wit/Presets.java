package org.febit.wit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Presets {

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
