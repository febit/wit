// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class TestGlobalRegister implements GlobalRegister{

    public void regist(DefaultGlobalManager manager) {
        manager.setConst("MY_CONST", "MY_CONST");
        manager.setConst("MY_CONST_2", "MY_CONST_2");
        manager.setGlobal("MY_GLOBAL", "MY_GLOBAL");
        manager.setGlobal("MY_GLOBAL_2", "MY_GLOBAL_2");
    }
    
}
