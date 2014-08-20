// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.testunit;

import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.lang.Bag;

/**
 *
 * @author zqq90
 */
public class AssertGlobalRegister implements GlobalRegister {

    public void regist(final GlobalManager manager) {
        final Bag constBag = manager.getConstBag();

        constBag.set("assertTrue", Assert.assertTrue);
        constBag.set("assertFalse", Assert.assertFalse);
        constBag.set("assertNull", Assert.assertNull);
        constBag.set("assertNotNull", Assert.assertNotNull);
        constBag.set("assertSame", Assert.assertSame);
        constBag.set("assertNotSame", Assert.assertNotSame);
        constBag.set("assertEquals", Assert.assertEquals);
        constBag.set("assertArrayEquals", Assert.assertArrayEquals);
    }
}
