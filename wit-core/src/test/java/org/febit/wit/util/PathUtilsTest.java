// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PathUtilsTest {

    @Test
    void test() {
        assertEquals("/parent/tmpl.wit", PathUtils.concat("/parent/", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", PathUtils.concat("/parent/", "./tmpl.wit"));
        assertEquals("/tmpl.wit", PathUtils.concat("/parent/", "../tmpl.wit"));

        assertEquals("/parent/tmpl.wit", PathUtils.concat("/parent", "tmpl.wit"));
        assertEquals("/parent/tmpl.wit", PathUtils.concat("/parent", "./tmpl.wit"));
        assertEquals("/tmpl.wit", PathUtils.concat("/parent", "../tmpl.wit"));

        assertEquals("/tmpl.wit", PathUtils.normalize("/parent/../tmpl.wit"));

        assertNull(PathUtils.normalize("/../tmpl.wit"));
        assertNull(PathUtils.normalize("../tmpl.wit"));
        assertNull(PathUtils.normalize("/parent/../../tmpl.wit"));

        assertEquals("/parent/", PathUtils.parent("/parent/tmpl.wit"));
        assertEquals("/", PathUtils.parent("/tmpl.wit"));
    }
}
