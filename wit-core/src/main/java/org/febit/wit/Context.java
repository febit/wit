// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import org.febit.wit.exceptions.NotFunctionException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.InternalVoid;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Runtime context.
 *
 */
@SuppressWarnings({
        "squid:S1214", //Constants should not be defined in interfaces
        "squid:RedundantThrowsDeclarationCheck"
})
public interface Context {

    Object VOID = InternalVoid.VOID;

    /**
     * Get a variable by name.
     *
     * @param name variable name
     * @return variable value
     * @throws ScriptRuntimeException if not found variable by given name
     */
    @Nullable
    Object getVar(String name) throws ScriptRuntimeException;

    /**
     * Get a variable by name.
     *
     * @param name  variable name
     * @param force whether or not throw a ScriptRuntimeException if not found variable, or return a null
     * @return variable value
     * @throws ScriptRuntimeException In force mode, if not found variable by given name
     */
    @Nullable
    Object getVar(String name, boolean force) throws ScriptRuntimeException;

    /**
     * Set a variable by name.
     *
     * @param name  variable name
     * @param value variable value
     */
    void setVar(String name, @Nullable Object value);

    /**
     * Get a local variable by name.
     *
     * @param name variable name
     * @return variable value
     */
    @Nullable
    Object getLocalVar(Object name);

    /**
     * Set a local variable by name.
     *
     * @param name  variable name
     * @param value value
     */
    void setLocalVar(Object name, @Nullable Object value);

    /**
     * Export a function by name.
     *
     * @param name function name
     * @return function
     * @throws NotFunctionException if not a function by given name, or not found
     */
    Function exportFunction(String name) throws NotFunctionException;

    /**
     * Export variables to a given map.
     *
     * @param map target map
     */
    void exportVars(final Map<? super String, @Nullable Object> map);

    /**
     * @param consumer consumer
     */
    void forEachVar(BiConsumer<? super String, @Nullable Object> consumer);
}
