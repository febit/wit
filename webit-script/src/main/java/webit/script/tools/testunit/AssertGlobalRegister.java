// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.testunit;

import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;

/**
 *
 * @author zqq90
 */
public class AssertGlobalRegister implements GlobalRegister {

    public void regist(final GlobalManager manager) {
        manager.setConst("assertTrue", Assert.assertTrue);
        manager.setConst("assertFalse", Assert.assertFalse);
        manager.setConst("assertNull", Assert.assertNull);
        manager.setConst("assertNotNull", Assert.assertNotNull);
        manager.setConst("assertSame", Assert.assertSame);
        manager.setConst("assertNotSame", Assert.assertNotSame);
        manager.setConst("assertEquals", Assert.assertEquals);
        manager.setConst("assertArrayEquals", Assert.assertArrayEquals);
    }
}
