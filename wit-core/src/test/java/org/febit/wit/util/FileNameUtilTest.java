// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author zqq90
 */
public class FileNameUtilTest {

    @Test
    public void test() {
        assertEquals("/parent/tmpl.wit", FileNameUtil.concat("/parent/", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", FileNameUtil.concat("/parent/", "./tmpl.wit"));
        assertEquals("/tmpl.wit", FileNameUtil.concat("/parent/", "../tmpl.wit"));

        assertEquals("/parent/tmpl.wit", FileNameUtil.concat("/parent", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", FileNameUtil.concat("/parent", "./tmpl.wit"));
        assertEquals("/tmpl.wit", FileNameUtil.concat("/parent", "../tmpl.wit"));

        assertEquals("/tmpl.wit", FileNameUtil.normalize("/parent/../tmpl.wit"));

        assertNull(FileNameUtil.normalize("/../tmpl.wit"));
        assertNull(FileNameUtil.normalize("../tmpl.wit"));
        assertNull(FileNameUtil.normalize("/parent/../../tmpl.wit"));

        assertEquals("/parent/", FileNameUtil.getPath("/parent/tmpl.wit"));
        assertEquals("/", FileNameUtil.getPath("/tmpl.wit"));
    }
}
