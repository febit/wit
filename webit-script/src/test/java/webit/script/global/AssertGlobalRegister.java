// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global;

import webit.script.util.SimpleBag;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class AssertGlobalRegister implements GlobalRegister {

    public static AssertGlobalRegister instance;

    public AssertGlobalRegister() {
        instance = this;
    }

    private GlobalManager manager;
    private final static String ASSERT_COUNT = "assertCount";
    private int accessCountIndex;

    public void regist(GlobalManager manager) {
        this.manager = manager;
        
        //Globals
        SimpleBag globalBag = manager.getGlobalBag();
        globalBag.set(ASSERT_COUNT, 0);
        
        manager.commit();
        accessCountIndex = manager.getGlobalIndex(ASSERT_COUNT);
    }

    public void resetAssertCount() {
        this.manager.setGlobal(accessCountIndex, 0);
    }

    public int getAssertCount() {
        return (Integer) this.manager.getGlobal(accessCountIndex);
    }
}
