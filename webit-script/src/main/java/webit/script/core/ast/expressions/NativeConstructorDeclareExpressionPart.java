// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;
import webit.script.Engine;
import webit.script.asm.AsmMethodCaller;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.core.ast.Position;
import webit.script.core.ast.method.AsmNativeMethodDeclare;
import webit.script.core.ast.method.NativeConstructorDeclare;
import webit.script.exceptions.ParseException;

/**
 *
 * @author Zqq
 */
public class NativeConstructorDeclareExpressionPart extends Position {

    private Class clazz;
    private List<Class> paramTypeList;

    public NativeConstructorDeclareExpressionPart(int line, int column) {
        super(line, column);
        this.paramTypeList = new LinkedList<Class>();
    }

    public NativeConstructorDeclareExpressionPart setClassName(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public NativeConstructorDeclareExpressionPart append(Class paramType) {
        paramTypeList.add(paramType);
        return this;
    }

    @SuppressWarnings("unchecked")
    public CommonMethodDeclareExpression pop(Engine _engine) {

        final String path;
        if (_engine.checkNativeAccess(path = (clazz.getName() + ".<init>")) == false) {
            throw new ParseException("Not accessable of native path: " + path, line, column);
        }

        try {
            final Constructor constructor = clazz.getConstructor(paramTypeList.toArray(new Class[paramTypeList.size()]));
            AsmMethodCaller caller;
            if (_engine.isEnableAsmNative()) {
                try {
                    if ((caller = AsmMethodCallerManager.getCaller(constructor)) == null) {
                        _engine.getLogger().error("AsmMethodCaller for '" + constructor.toString() + "' is null, and instead by NativeConstructorDeclare");
                    }
                } catch (Throwable ex) {
                    caller = null;
                    _engine.getLogger().error("Generate AsmMethodCaller for '" + constructor.toString() + "' failed, and instead by NativeConstructorDeclare", ex);
                }
            } else {
                caller = null;
            }

            return new CommonMethodDeclareExpression(caller != null
                    ? new AsmNativeMethodDeclare(caller)
                    : new NativeConstructorDeclare(constructor),
                    line, column);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }
}
