// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit;

import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.function.Executable;

/**
 * @author zqq90
 */
public class EngineManager {

    private static final Engine engine;

    static {
        try {
            engine = Engine.create("/febit-wit-test.wim", null);
        } catch (Exception e) {
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

    public static Executable tmplChecker(String tmpl) {
        return () -> getTemplate(tmpl)
                .reload();
    }

    public static Executable codeChecker(String code) {
        return tmplChecker("code: \n" + code);
    }
}
