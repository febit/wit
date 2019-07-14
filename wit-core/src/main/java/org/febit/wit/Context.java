// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import org.febit.wit.exceptions.NotFunctionException;
import org.febit.wit.exceptions.ScriptRuntimeException;
import org.febit.wit.lang.InternalVoid;

import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Runtime context.
 *
 * @author zqq90
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
    Object get(String name) throws ScriptRuntimeException;

    /**
     * Get a variable by name.
     *
     * @param name  variable name
     * @param force whether or not throw a ScriptRuntimeException if not found variable, or return a null
     * @return variable value
     * @throws ScriptRuntimeException In force mode, if not found variable by given name
     */
    Object get(String name, boolean force) throws ScriptRuntimeException;

    /**
     * Set a variable by name.
     *
     * @param name  variable name
     * @param value variable value
     */
    void set(String name, Object value);

    /**
     * Get a local variable by name.
     *
     * @param name variable name
     * @return variable value
     */
    Object getLocal(Object name);

    /**
     * Set a local variable by name.
     *
     * @param name  variable name
     * @param value value
     */
    void setLocal(Object name, Object value);

    /**
     * Export a function by name.
     *
     * @param name function name
     * @return function
     * @throws NotFunctionException if not found function by given name
     * @since 1.5.0
     */
    Function exportFunction(String name) throws NotFunctionException;

    /**
     * Export variables to a given map.
     *
     * @param map target map
     */
    void exportTo(final Map<? super String, Object> map);

    /**
     * @param consumer consumer
     * @since 2.6.0
     */
    void forEachVar(BiConsumer<? super String, Object> consumer);
}
