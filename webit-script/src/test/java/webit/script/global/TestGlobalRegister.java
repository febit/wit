// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.global;

import java.util.ArrayList;
import java.util.List;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.core.NativeFactory;
import webit.script.util.SimpleBag;

/**
 *
 * @author zqq90
 */
public class TestGlobalRegister implements GlobalRegister, Initable {

    private NativeFactory nativeFactory;

    public void regist(final GlobalManager manager) {

        //Globals
        SimpleBag globalBag = manager.getGlobalBag();
        globalBag.set("MY_GLOBAL", "MY_GLOBAL");
        globalBag.set("MY_GLOBAL_2", "MY_GLOBAL_2");

        //Consts
        SimpleBag constBag = manager.getConstBag();
        constBag.set("MY_CONST", "MY_CONST");
        constBag.set("MY_CONST_2", "MY_CONST_2");

        //Native
        constBag.set("new_list", this.nativeFactory.createNativeConstructorDeclare(ArrayList.class, null));
        constBag.set("list_size", this.nativeFactory.createNativeMethodDeclare(List.class, "size", null));
        constBag.set("list_add", this.nativeFactory.createNativeMethodDeclare(List.class, "add", new Class[]{Object.class}));
        constBag.set("substring", this.nativeFactory.createNativeMethodDeclare(String.class, "substring", new Class[]{int.class, int.class}));
    }

    public void init(Engine engine) {
        this.nativeFactory = engine.getNativeFactory();
    }

}
