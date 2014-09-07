// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.type;

import webit.script.Context;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.lang.MethodDeclare;
import webit.script.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public class TypeGlobalRegister implements GlobalRegister {

    public void regist(GlobalManager manager) {
        manager.setConst("is_array", is_array);
        manager.setConst("is_bool", is_bool);
        manager.setConst("is_function", is_function);
        manager.setConst("is_callable", is_function);
        manager.setConst("is_null", is_null);
        manager.setConst("is_number", is_number);
    }

    public final static MethodDeclare is_function = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            return ArrayUtil.get(args, 0, null) instanceof MethodDeclare;
        }
    };

    public final static MethodDeclare is_number = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            return ArrayUtil.get(args, 0, null) instanceof Number;
        }
    };

    public final static MethodDeclare is_bool = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            return ArrayUtil.get(args, 0, null) instanceof Boolean;
        }
    };

    public final static MethodDeclare is_null = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            return args == null
                    || args.length == 0
                    || args[0] == null;
        }
    };

    public final static MethodDeclare is_array = new MethodDeclare() {

        public Object invoke(Context context, Object[] args) {
            final Object item;
            return (item = ArrayUtil.get(args, 0, null)) != null
                    && item.getClass().isArray();
        }
    };
}
