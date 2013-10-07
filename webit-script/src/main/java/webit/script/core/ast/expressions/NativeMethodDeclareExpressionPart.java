// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import webit.script.Engine;
import webit.script.asm.AsmMethodCaller;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.core.ast.Position;
import webit.script.core.ast.method.AsmNativeMethodDeclare;
import webit.script.core.ast.method.NativeMethodDeclare;
import webit.script.exceptions.ParseException;
import webit.script.util.ClassUtil;

/**
 *
 * @author Zqq
 */
public class NativeMethodDeclareExpressionPart extends Position {

    private Class clazz;
    private String methodName;
    private List<Class> paramTypeList;

    public NativeMethodDeclareExpressionPart(int line, int column) {
        super(line, column);
        this.paramTypeList = new LinkedList<Class>();
    }

    public NativeMethodDeclareExpressionPart setClassName(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public NativeMethodDeclareExpressionPart setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public NativeMethodDeclareExpressionPart append(Class paramType) {
        paramTypeList.add(paramType);
        return this;
    }

    public CommonMethodDeclareExpression pop(Engine _engine) {

        final String path;
        if (_engine.checkNativeAccess(path = (clazz.getName() + '.' + methodName)) == false) {
            throw new ParseException("Not accessable of native path: " + path, line, column);
        }

        try {
            final Method method = ClassUtil.searchMethod(clazz, methodName, paramTypeList.toArray(new Class[paramTypeList.size()]), false);
            AsmMethodCaller caller;
            if (_engine.isEnableAsmNative()) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(method)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(method)) == null) {
                                _engine.getLogger().error("AsmMethodCaller for '" + method.toString() + "' is null, and instead by NativeMethodDeclare");
                            }
                        } catch (Throwable ex) {
                            caller = null;
                            _engine.getLogger().error("Generate AsmMethodCaller for '" + method.toString() + "' failed, and instead by NativeMethodDeclare", ex);
                        }
                    } else {
                        _engine.getLogger().warn("'" + method.toString() + "' will not use asm, since this method is not public, and instead by NativeMethodDeclare");
                        caller = null;
                    }
                } else {
                    _engine.getLogger().warn("'" + method.toString() + "' will not use asm, since class is not public, and instead by NativeMethodDeclare");
                    caller = null;
                }
            } else {
                caller = null;
            }

            return new CommonMethodDeclareExpression(caller != null
                    ? new AsmNativeMethodDeclare(caller)
                    : new NativeMethodDeclare(method),
                    line, column);

        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }
}
