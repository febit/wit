// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.tools;

import org.febit.wit.Engine;
import org.febit.wit.Template;
import org.febit.wit.exceptions.ResourceNotFoundException;

/**
 *
 * @author zqq90
 */
public class EngineManager {

    private static final Engine engine;

    static {
        engine = Engine.create("/wit-lib-test.wim", null);
    }

    public static Engine getEngine() {
        return engine;
    }

    public static Template getTemplate(String name) throws ResourceNotFoundException {
        return engine.getTemplate(name);
    }
}
