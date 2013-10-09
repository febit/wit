// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class UnixStyleFileNameUtilTest {

    //@Test
    public void test() {
        assertEquals("/parent/tmpl.wtl", UnixStyleFileNameUtil.concat("/parent/", "tmpl.wtl"));
        assertEquals("/parent/tmpl.wtl", UnixStyleFileNameUtil.concat("/parent/", "./tmpl.wtl"));
        assertEquals("/tmpl.wtl", UnixStyleFileNameUtil.concat("/parent/", "../tmpl.wtl"));
        
        
        assertEquals("/tmpl.wtl", UnixStyleFileNameUtil.normalize("/parent/../tmpl.wtl"));
        
        assertNull(UnixStyleFileNameUtil.normalize("/../tmpl.wtl"));
        assertNull(UnixStyleFileNameUtil.normalize("../tmpl.wtl"));
        assertNull(UnixStyleFileNameUtil.normalize("/parent/../../tmpl.wtl"));        
        
        assertEquals("/parent/", UnixStyleFileNameUtil.getPath("/parent/tmpl.wtl"));
        assertEquals("/", UnixStyleFileNameUtil.getPath("/tmpl.wtl"));
    }
}
