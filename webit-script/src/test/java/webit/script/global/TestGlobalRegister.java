// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.global;

import java.util.ArrayList;
import java.util.List;
import webit.script.core.NativeFactory;

/**
 *
 * @author zqq90
 */
public class TestGlobalRegister implements GlobalRegister{

    protected NativeFactory nativeFactory;

    @Override
    public void regist(final GlobalManager manager) {

        //Globals
        manager.setGlobal("MY_GLOBAL", "MY_GLOBAL");
        manager.setGlobal("MY_GLOBAL_2", "MY_GLOBAL_2");

        //Consts
        manager.setConst("MY_CONST", "MY_CONST");
        manager.setConst("MY_CONST_2", "MY_CONST_2");

        //Native
        manager.setConst("new_list", this.nativeFactory.getNativeConstructorDeclare(ArrayList.class, null));
        manager.setConst("list_size", this.nativeFactory.getNativeMethodDeclare(List.class, "size", null));
        manager.setConst("list_add", this.nativeFactory.getNativeMethodDeclare(List.class, "add", new Class[]{Object.class}));
        manager.setConst("substring", this.nativeFactory.getNativeMethodDeclare(String.class, "substring", new Class[]{int.class, int.class}));
    }
}
