// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.tmpls;

import org.febit.wit.EngineManager;
import org.febit.wit.Template;
import org.febit.wit.Vars;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author zqq90
 */
class DebugTest {

    private final Set<Object> labelCache = new HashSet<>();
    private int pointCount = 0;

    @Test
    void test() throws ResourceNotFoundException {
        Template template = EngineManager.getTemplate("/debug.wit");

        template.merge();

        labelCache.clear();
        pointCount = 0;
        template.debug(Vars.EMPTY, (label, context, statement, result) -> {
            labelCache.add(label);
            pointCount++;
        });

        assertEquals(18, pointCount);
        assertTrue(labelCache.contains(null));
        assertTrue(labelCache.contains("p1"));
        assertTrue(labelCache.contains("p2"));
        assertTrue(labelCache.contains("p3"));
        assertTrue(labelCache.contains("p4"));

    }

}
