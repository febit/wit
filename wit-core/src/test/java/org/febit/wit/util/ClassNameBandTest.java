// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author zqq90
 */
public class ClassNameBandTest {
    
    @Test
    public void test(){
        ClassNameBand band;
        
        
        band = new ClassNameBand("abc");
        assertEquals("abc",band.getClassSimpleName());
        assertEquals("abc",band.getClassPureName());
        assertEquals("abc",band.toString());
        
        band = new ClassNameBand("abc").plusArrayDepth();
        assertEquals("abc",band.getClassSimpleName());
        assertEquals("abc",band.getClassPureName());
        assertEquals("abc[]",band.toString());
        
        
        band = new ClassNameBand("abc").append("def");
        assertEquals("def",band.getClassSimpleName());
        assertEquals("abc.def",band.getClassPureName());
        assertEquals("abc.def",band.toString());
        
        
        band = new ClassNameBand("abc").append("def").plusArrayDepth();
        assertEquals("def",band.getClassSimpleName());
        assertEquals("abc.def",band.getClassPureName());
        assertEquals("abc.def[]",band.toString());
        
        
        band = new ClassNameBand("abc").append("def").plusArrayDepth().plusArrayDepth();
        assertEquals("def",band.getClassSimpleName());
        assertEquals("abc.def",band.getClassPureName());
        assertEquals("abc.def[][]",band.toString());
    }
}
