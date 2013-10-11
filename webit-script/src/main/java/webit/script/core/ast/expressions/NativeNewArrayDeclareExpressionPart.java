// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.Engine;
import webit.script.core.ast.Position;
import webit.script.core.ast.method.NativeNewArrayDeclare;
import webit.script.exceptions.ParseException;

/**
 *
 * @author Zqq
 */
public class NativeNewArrayDeclareExpressionPart extends Position {

    private Class componentType;

    public NativeNewArrayDeclareExpressionPart(Class componentType, int line, int column) {
        super(line, column);
        this.componentType = componentType;
    }

    public CommonMethodDeclareExpression pop(Engine _engine) {
        Class classWaitCheck = componentType;
        while (classWaitCheck.isArray()) {
            classWaitCheck = classWaitCheck.getComponentType();
        }

        if (classWaitCheck == Void.class || classWaitCheck == Void.TYPE) {
            throw new ParseException("ComponentType must not Void.class", line, column);
        }

        final String path;
        if (_engine.checkNativeAccess(path = (classWaitCheck.getName().concat(".[]"))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        return new CommonMethodDeclareExpression(new NativeNewArrayDeclare(componentType), line, column);
    }
}
