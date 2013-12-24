// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global;

import webit.script.util.SimpleBag;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class TestGlobalRegister implements GlobalRegister {

    public void regist(GlobalManager manager) {

        //Globals
        SimpleBag globalBag = manager.getGlobalBag();
        globalBag.set("MY_GLOBAL", "MY_GLOBAL");
        globalBag.set("MY_GLOBAL_2", "MY_GLOBAL_2");
        
        //Consts
        SimpleBag constBag = manager.getConstBag();
        constBag.set("MY_CONST", "MY_CONST");
        constBag.set("MY_CONST_2", "MY_CONST_2");
    }

}
