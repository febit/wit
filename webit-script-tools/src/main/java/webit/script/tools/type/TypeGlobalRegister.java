// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.type;

import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;

/**
 *
 * @author zqq90
 */
public class TypeGlobalRegister implements GlobalRegister {

    public void regist(GlobalManager manager) {
        manager.setConst("is_array", TypeMethods.is_array);
        manager.setConst("is_bool", TypeMethods.is_bool);
        manager.setConst("is_function", TypeMethods.is_function);
        manager.setConst("is_callable", TypeMethods.is_function);
        manager.setConst("is_null", TypeMethods.is_null);
        manager.setConst("is_number", TypeMethods.is_number);
    }
}
