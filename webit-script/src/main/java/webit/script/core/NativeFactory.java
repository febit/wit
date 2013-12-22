// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.asm.AsmMethodCaller;
import webit.script.asm.AsmMethodCallerManager;
import webit.script.core.ast.ClassNameList;
import webit.script.core.ast.Expression;
import webit.script.core.ast.expressions.CommonMethodDeclareExpression;
import webit.script.core.ast.expressions.DirectValue;
import webit.script.core.ast.expressions.NativeStaticValue;
import webit.script.exceptions.ParseException;
import webit.script.loggers.Logger;
import webit.script.method.impl.AsmNativeMethodDeclare;
import webit.script.method.impl.NativeConstructorDeclare;
import webit.script.method.impl.NativeMethodDeclare;
import webit.script.method.impl.NativeNewArrayDeclare;
import webit.script.security.NativeSecurityManager;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class NativeFactory implements Initable {

    private boolean enableAsm = true;

    private Logger logger;
    private NativeSecurityManager nativeSecurityManager;

    public void init(Engine engine) {
        this.logger = engine.getLogger();
        this.nativeSecurityManager = engine.getNativeSecurityManager();
    }

    public void setEnableAsm(boolean enableAsm) {
        this.enableAsm = enableAsm;
    }

    public Expression createNativeStaticValue(Class clazz, String fieldName, int line, int column) {
        final String path;
        if (this.nativeSecurityManager.access(path = (StringUtil.concat(clazz.getName(), ".", fieldName))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }
        Field field;
        try {
            field = clazz.getField(fieldName);
        } catch (NoSuchFieldException ex) {
            throw new ParseException("No such field: ".concat(path), line, column);
        }
        if (ClassUtil.isStatic(field)) {
            ClassUtil.setAccessible(field);
            if (ClassUtil.isFinal(field)) {
                try {
                    return new DirectValue(field.get(null), line, column);
                } catch (Exception ex) {
                    throw new ParseException("Failed to static field value: ".concat(path), ex, line, column);
                }
            } else {
                return new NativeStaticValue(field, line, column);
            }
        } else {
            throw new ParseException("No a static field: ".concat(path), line, column);
        }
    }

    public CommonMethodDeclareExpression createNativeNewArrayDeclare(Class componentType, int line, int column) {
        Class classWaitCheck = componentType;
        while (classWaitCheck.isArray()) {
            classWaitCheck = classWaitCheck.getComponentType();
        }

        if (classWaitCheck == Void.class || classWaitCheck == Void.TYPE) {
            throw new ParseException("ComponentType must not Void.class", line, column);
        }

        final String path;
        if (this.nativeSecurityManager.access(path = (classWaitCheck.getName().concat(".[]"))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        return new CommonMethodDeclareExpression(new NativeNewArrayDeclare(componentType), line, column);
    }

    public CommonMethodDeclareExpression createNativeMethodDeclare(Class clazz, String methodName, ClassNameList list, int line, int column) {
        return createNativeMethodDeclare(clazz, methodName, list.toArray(), line, column);
    }

    public CommonMethodDeclareExpression createNativeMethodDeclare(Class clazz, String methodName, Class[] paramClasses, int line, int column) {

        final String path;
        if (this.nativeSecurityManager.access(path = (StringUtil.concat(clazz.getName(), ".", methodName))) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Method method = ClassUtil.searchMethod(clazz, methodName, paramClasses, false);
            AsmMethodCaller caller;
            if (this.enableAsm) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(method)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(method)) == null) {
                                logger.error(StringUtil.concat("AsmMethodCaller for '", method.toString(), "' is null, and instead by NativeMethodDeclare"));
                            }
                        } catch (Exception ex) {
                            caller = null;
                            logger.error(StringUtil.concat("Generate AsmMethodCaller for '", method.toString(), "' failed, and instead by NativeMethodDeclare"), ex);
                        }
                    } else {
                        logger.warn(StringUtil.concat("'", method.toString(), "' will not use asm, since this method is not public, and instead by NativeMethodDeclare"));
                        caller = null;
                    }
                } else {
                    logger.warn(StringUtil.concat("'", method.toString(), "' will not use asm, since class is not public, and instead by NativeMethodDeclare"));
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

    public CommonMethodDeclareExpression createNativeConstructorDeclare(Class clazz, ClassNameList list, int line, int column) {
        return createNativeConstructorDeclare(clazz, list.toArray(), line, column);
    }

    @SuppressWarnings("unchecked")
    public CommonMethodDeclareExpression createNativeConstructorDeclare(Class clazz, Class[] paramClasses, int line, int column) {

        final String path;
        if (this.nativeSecurityManager.access(path = (clazz.getName() + ".<init>")) == false) {
            throw new ParseException("Not accessable of native path: ".concat(path), line, column);
        }

        try {
            final Constructor constructor = clazz.getConstructor(paramClasses);
            AsmMethodCaller caller;
            if (this.enableAsm) {
                if (ClassUtil.isPublic(clazz)) {
                    if (ClassUtil.isPublic(constructor)) {
                        try {
                            if ((caller = AsmMethodCallerManager.getCaller(constructor)) == null) {
                                logger.error(StringUtil.concat("AsmMethodCaller for '", constructor.toString(), "' is null, and instead by NativeConstructorDeclare"));
                            }
                        } catch (Exception ex) {
                            caller = null;
                            logger.error(StringUtil.concat("Generate AsmMethodCaller for '", constructor.toString(), "' failed, and instead by NativeConstructorDeclare"), ex);
                        }
                    } else {
                        logger.warn(StringUtil.concat("'", constructor.toString(), "' will not use asm, since this method is not public, and instead by NativeConstructorDeclare"));
                        caller = null;
                    }
                } else {
                    logger.warn(StringUtil.concat("'" + constructor.toString() + "' will not use asm, since class is not public, and instead by NativeConstructorDeclare"));
                    caller = null;
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
