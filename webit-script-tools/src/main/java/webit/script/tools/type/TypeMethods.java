// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.type;

import webit.script.Context;
import webit.script.lang.MethodDeclare;
import webit.script.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public class TypeMethods {

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
