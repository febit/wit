// Copyright (c) 2013-2016, febit.org. All Rights Reserved.

package org.febit.wit.test.tmpls;

import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.Test;
import org.febit.wit.EngineManager;
import org.febit.wit.InternalContext;
import org.febit.wit.Template;
import org.febit.wit.core.ast.Statement;
import org.febit.wit.debug.BreakPointListener;
import org.febit.wit.exceptions.ResourceNotFoundException;
import org.febit.wit.io.impl.DiscardOut;
import org.febit.wit.util.KeyValuesUtil;

/**
 *
 * @author zqq90
 */
public class DebugTest {
    
    private final Set<Object> labelCache = new HashSet<>();
    private int pointCount = 0;
    
    @Test
    public void test() throws ResourceNotFoundException {
        Template template = EngineManager.getTemplate("/debug.wit");
        
        template.merge(new DiscardOut());
        
        labelCache.clear();
        pointCount = 0;
        template.debug(KeyValuesUtil.EMPTY_KEY_VALUES, new DiscardOut(), new BreakPointListener() {

            @Override
            public void onBreak(Object label, InternalContext context, Statement statement, Object result) {
                labelCache.add(label);
                pointCount ++;
            }
        });
        
        assertEquals(18, pointCount);
        assertTrue(labelCache.contains(null));
        assertTrue(labelCache.contains("p1"));
        assertTrue(labelCache.contains("p2"));
        assertTrue(labelCache.contains("p3"));
        assertTrue(labelCache.contains("p4"));
        
    }
    
}
