/*
 * Copyright 2013-present febit.org (support@febit.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.febit.wit.test.component;

import org.febit.wit.Wit;
import org.febit.wit.WitModule;
import org.febit.wit.test.component.lib.ConstMethods;
import org.febit.wit.test.component.lib.ConstMethods2;
import org.febit.wit.util.HeapNativeUtils;
import org.febit.wit.util.NativeMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestCasesModule implements WitModule {

    @Override
    public void apply(Wit wit) {

        var heaps = wit.globals();
        var functions = wit.nativeAccess().functions();

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
        HeapNativeUtils.collectConstFields(heaps.constants(), ConstMethods.class);
        HeapNativeUtils.collectStaticMethods(heaps.constants(), functions, ConstMethods.class);

        try {
            heaps.constants().set("new_ConstMethods2", functions.constructor(ConstMethods2.class.getConstructor()));
            heaps.constants().set("const2Member", functions.method(NativeMethods.find(ConstMethods2.class, "const2Member")
                    .toList()));
            heaps.constants().set("const2Size", functions.method(NativeMethods.find(ConstMethods2.class, "const2Size")
                    .toList()));
            heaps.constants().set("const2Foo", functions.method(NativeMethods.find(ConstMethods2.class, "const2Foo")
                    .toList()));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        // For optimize.wit
        heaps.constants().set("CONST_STRING_BUILDER", new StringBuilder());
        heaps.constants().set("CONST_ATOMIC_INT", new AtomicInteger());

    }

}
