// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.util;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class CharUtilTest {
    
    //@Test
    public void indexOfTest(){
    
        char [] src = "abcdefghi".toCharArray();
        
        Assert.assertEquals(1, CharUtil.indexOf(src, "bcd".toCharArray(), 1));
        Assert.assertEquals(2, CharUtil.indexOf(src, "c".toCharArray(), 1));
        Assert.assertEquals(2, CharUtil.indexOf(src, "cde".toCharArray(), 1));
    
    }
}
