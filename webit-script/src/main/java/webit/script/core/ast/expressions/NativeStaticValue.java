// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.lang.reflect.Field;
import webit.script.Context;
import webit.script.core.ast.AbstractExpression;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.exceptions.ScriptRuntimeException;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public class NativeStaticValue extends AbstractExpression implements ResetableValueExpression {

    private final Field field;

    public NativeStaticValue(Field field, int line, int column) {
        super(line, column);
        this.field = field;
    }

    public Object execute(Context context) {
        try {
            return field.get(null);
        } catch (Exception ex) {
            throw new ScriptRuntimeException(StringUtil.concat("Failed to get static field value: ".concat(field.toString())), ex, this);
        }
    }

    public Object setValue(Context context, Object value) {
        try {
            field.set(null,value);
            return value;
        } catch (Exception ex) {
            throw new ScriptRuntimeException(StringUtil.concat("Failed to get static field value: ".concat(field.toString())), ex, this);
        }
    }

}
