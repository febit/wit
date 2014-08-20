// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.type;

import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.lang.Bag;

/**
 *
 * @author zqq90
 */
public class TypeGlobalRegister implements GlobalRegister {

    public void regist(GlobalManager manager) {
        final Bag constBag = manager.getConstBag();

        constBag.set("is_array", TypeMethods.is_array);
        constBag.set("is_bool", TypeMethods.is_bool);
        constBag.set("is_function", TypeMethods.is_function);
        constBag.set("is_callable", TypeMethods.is_function);
        constBag.set("is_null", TypeMethods.is_null);
        constBag.set("is_number", TypeMethods.is_number);
    }
}
