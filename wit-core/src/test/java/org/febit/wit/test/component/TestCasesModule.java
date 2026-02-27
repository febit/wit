// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.test.component;

import org.febit.wit.Wit;
import org.febit.wit.WitModule;
import org.febit.wit.test.component.lib.ConstMethods;
import org.febit.wit.test.component.lib.ConstMethods2;
import org.febit.wit.util.ClassUtils;
import org.febit.wit.util.HeapNativeUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCasesModule implements WitModule {

    @Override
    public void apply(Wit wit) {

        var heaps = wit.staticHeaps();
        var functions = wit.nativeLayout().functions();

        // Static
        heaps.variables().set("MY_GLOBAL", "MY_GLOBAL");
        heaps.variables().set("MY_GLOBAL_2", "MY_GLOBAL_2");

        //Const
        heaps.constants().set("MY_CONST", "MY_CONST");
        heaps.constants().set("MY_CONST_2", "MY_CONST_2");

        //Native
        try {
            heaps.constants().set("new_list", functions.constructor(ArrayList.class.getConstructor()));
            heaps.constants().set("list_size", functions.method(List.class.getMethod("size")));
            heaps.constants().set("list_add", functions.method(List.class.getMethod("add", Object.class)));
            heaps.constants().set("substring", functions.method(String.class.getMethod("substring", int.class, int.class)));

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        HeapNativeUtils.addConstFields(heaps.constants(), ConstMethods.class);
        HeapNativeUtils.addStaticMethods(heaps.constants(), functions, ConstMethods.class);

        try {
            heaps.constants().set("new_ConstMethods2", functions.constructor(ConstMethods2.class.getConstructor()));
            heaps.constants().set("const2Member", functions.method(ClassUtils.methods(ConstMethods2.class, "const2Member")
                    .filter(ClassUtils::isPublic).toList()));
            heaps.constants().set("const2Size", functions.method(ClassUtils.methods(ConstMethods2.class, "const2Size")
                    .filter(ClassUtils::isPublic).toList()));
            heaps.constants().set("const2Foo", functions.method(ClassUtils.methods(ConstMethods2.class, "const2Foo")
                    .filter(ClassUtils::isPublic).toList()));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        // For optimize.wit
        heaps.constants().set("CONST_STRING_BUILDER", new StringBuilder());
        heaps.constants().set("CONST_ATOMIC_INT", new AtomicInteger());

    }

}
