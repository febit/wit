// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script;

import java.util.Map;
import webit.script.exceptions.NotFunctionException;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.lang.InternalVoid;
import webit.script.lang.KeyValueAccepter;

/**
 *
 * @author zqq90
 */
public interface Context extends KeyValueAccepter {

    InternalVoid VOID = InternalVoid.VOID;

    /**
     * Get a variable by name.
     *
     * @param name
     * @return
     * @throws ScriptRuntimeException
     */
    Object get(String name) throws ScriptRuntimeException;

    /**
     * Get a variable by name.
     *
     * @param name
     * @param force
     * @return
     * @throws ScriptRuntimeException
     */
    Object get(String name, boolean force) throws ScriptRuntimeException;

    /**
     * Set a variable by name.
     *
     * @param name
     * @param value
     */
    @Override
    void set(String name, Object value);

    /**
     * Get a local variable by name.
     *
     * @param name
     * @return
     */
    Object getLocal(Object name);

    /**
     * Set a local variable by name.
     *
     * @param name
     * @param value
     */
    void setLocal(Object name, Object value);

    /**
     * Export a function by name.
     *
     * @since 1.5.0
     * @param name
     * @return
     * @throws NotFunctionException
     */
    Function exportFunction(String name) throws NotFunctionException;

    /**
     * Export variables to a given map.
     *
     * @param map
     */
    void exportTo(final Map map);
}
