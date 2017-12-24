// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global;

import java.util.ArrayList;
import java.util.List;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.util.JavaNativeUtil;

/**
 *
 * @author zqq90
 */
public class TestGlobalRegister implements GlobalRegister {

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

        JavaNativeUtil.addConstFields(manager, nativeFactory, ConstMethods.class);
        JavaNativeUtil.addStaticMethods(manager, nativeFactory, ConstMethods.class);

        manager.setConst("new_ConstMethods2", this.nativeFactory.getNativeConstructorDeclare(ConstMethods2.class, null));
        manager.setConst("const2Member", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Member"));
        manager.setConst("const2Size", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Size"));
        manager.setConst("const2Foo", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Foo"));
    }
}
