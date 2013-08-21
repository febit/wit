// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script;

import webit.script.exceptions.ResourceNotFoundException;

/**
 *
 * @author Zqq
 */
public class EngineManager {

    private static Engine engine;

    static {
        engine = Engine.createEngine("/webitl-test.props", null);
    }

    public static Engine getEngine() {
        return engine;
    }

    public static Template getTemplate(String name) throws ResourceNotFoundException {
        return engine.getTemplate(name);
    }
}
