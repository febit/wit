// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Engine;
import org.febit.wit.EnginePlugin;
import org.febit.wit.test.component.lib.ConstMethods;
import org.febit.wit.test.component.lib.ConstMethods2;
import org.febit.wit.util.JavaNativeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCasesModule implements EnginePlugin {

    @Override
    public void apply(Engine engine) {

        var heap = engine.globalHeap();
        var nativeFactory = engine.nativeFactory();

        //Globals
        heap.setGlobal("MY_GLOBAL", "MY_GLOBAL");
        heap.setGlobal("MY_GLOBAL_2", "MY_GLOBAL_2");

        //Const
        heap.setConst("MY_CONST", "MY_CONST");
        heap.setConst("MY_CONST_2", "MY_CONST_2");

        //Native
        heap.setConst("new_list", nativeFactory.getNativeConstructorDeclare(ArrayList.class, null));
        heap.setConst("list_size", nativeFactory.getNativeMethodDeclare(List.class, "size", null));
        heap.setConst("list_add", nativeFactory.getNativeMethodDeclare(List.class, "add", new Class[]{Object.class}));
        heap.setConst("substring", nativeFactory.getNativeMethodDeclare(String.class, "substring", new Class[]{int.class, int.class}));

        JavaNativeUtils.addConstFields(heap, ConstMethods.class);
        JavaNativeUtils.addStaticMethods(heap, nativeFactory, ConstMethods.class);

        heap.setConst("new_ConstMethods2", nativeFactory.getNativeConstructorDeclare(ConstMethods2.class, null));
        heap.setConst("const2Member", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Member"));
        heap.setConst("const2Size", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Size"));
        heap.setConst("const2Foo", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Foo"));

        // For optimize.wit
        heap.setConst("CONST_STRING_BUILDER", new StringBuilder());
        heap.setConst("CONST_ATOMIC_INT", new AtomicInteger());

    }

}
