// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.util;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Zqq
 */
public class UnixStyleFileNameUtilTest {

    @Test
    public void test() {
        assertEquals("/parent/tmpl.wit", UnixStyleFileNameUtil.concat("/parent/", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", UnixStyleFileNameUtil.concat("/parent/", "./tmpl.wit"));
        assertEquals("/tmpl.wit", UnixStyleFileNameUtil.concat("/parent/", "../tmpl.wit"));
        
        
        assertEquals("/tmpl.wit", UnixStyleFileNameUtil.normalize("/parent/../tmpl.wit"));
        
        assertNull(UnixStyleFileNameUtil.normalize("/../tmpl.wit"));
        assertNull(UnixStyleFileNameUtil.normalize("../tmpl.wit"));
        assertNull(UnixStyleFileNameUtil.normalize("/parent/../../tmpl.wit"));        
        
        assertEquals("/parent/", UnixStyleFileNameUtil.getPath("/parent/tmpl.wit"));
        assertEquals("/", UnixStyleFileNameUtil.getPath("/tmpl.wit"));
    }
}
