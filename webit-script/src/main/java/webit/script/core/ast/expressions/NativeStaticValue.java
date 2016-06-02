// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.lang.reflect.Field;
import webit.script.InternalContext;
import webit.script.core.ast.ResetableValueExpression;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author zqq90
 */
public class NativeStaticValue extends ResetableValueExpression {

    private final Field field;

    public NativeStaticValue(Field field, int line, int column) {
        super(line, column);
        this.field = field;
    }

    @Override
    public Object execute(InternalContext context) {
        try {
            return field.get(null);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            return new ScriptRuntimeException("Failed to get static field value: ".concat(field.toString()), ex, this);
        }
    }

    @Override
    public Object setValue(InternalContext context, Object value) {
        try {
            field.set(null, value);
            return value;
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            return new ScriptRuntimeException("Failed to set static field value: ".concat(field.toString()), ex, this);
        }
    }
}
