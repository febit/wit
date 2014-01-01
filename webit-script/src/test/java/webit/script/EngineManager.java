// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script;

import webit.script.exceptions.ResourceNotFoundException;

/**
 *
 * @author Zqq
 */
public class EngineManager {

    private static final Engine engine;

    static {
        try {
            engine = Engine.createEngine("/webit-script-test.props", null);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static Engine getEngine() {
        return engine;
    }

    public static Template getTemplate(String name) throws ResourceNotFoundException {
        return engine.getTemplate(name);
    }
}
