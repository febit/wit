// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Engine;
import org.febit.wit.EngineModule;
import org.febit.wit.test.component.lib.ConstMethods;
import org.febit.wit.test.component.lib.ConstMethods2;
import org.febit.wit.util.JavaNativeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCasesModule implements EngineModule {

    @Override
    public void apply(Engine engine) {

        var heaps = engine.staticHeaps();
        var nativeFactory = engine.nativeFactory();

        // Static
        heaps.variant().set("MY_GLOBAL", "MY_GLOBAL");
        heaps.variant().set("MY_GLOBAL_2", "MY_GLOBAL_2");

        //Const
        heaps.constant().set("MY_CONST", "MY_CONST");
        heaps.constant().set("MY_CONST_2", "MY_CONST_2");

        //Native
        heaps.constant().set("new_list", nativeFactory.getNativeConstructorDeclare(ArrayList.class, null));
        heaps.constant().set("list_size", nativeFactory.getNativeMethodDeclare(List.class, "size", null));
        heaps.constant().set("list_add", nativeFactory.getNativeMethodDeclare(List.class, "add", new Class[]{Object.class}));
        heaps.constant().set("substring", nativeFactory.getNativeMethodDeclare(String.class, "substring", new Class[]{int.class, int.class}));

        JavaNativeUtils.addConstFields(heaps, ConstMethods.class);
        JavaNativeUtils.addStaticMethods(heaps, nativeFactory, ConstMethods.class);

        heaps.constant().set("new_ConstMethods2", nativeFactory.getNativeConstructorDeclare(ConstMethods2.class, null));
        heaps.constant().set("const2Member", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Member"));
        heaps.constant().set("const2Size", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Size"));
        heaps.constant().set("const2Foo", nativeFactory.getNativeMethodDeclare(ConstMethods2.class, "const2Foo"));

        // For optimize.wit
        heaps.constant().set("CONST_STRING_BUILDER", new StringBuilder());
        heaps.constant().set("CONST_ATOMIC_INT", new AtomicInteger());

    }

}
