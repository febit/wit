// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.asm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import webit.script.core.NativeFactory;
import webit.script.lang.MethodDeclare;
import webit.script.util.ClassUtil;

/**
 *
 * @author zqq
 */
public class AsmNativeFactory extends NativeFactory {

    @Override
    public MethodDeclare createNativeConstructorDeclare(Class clazz, Constructor constructor, int line, int column) {
        if (ClassUtil.isPublic(clazz)) {
            if (ClassUtil.isPublic(constructor)) {
                try {
                    AsmMethodAccessor accessor = AsmMethodAccessorManager.getAccessor(constructor);
                    if (accessor == null) {
                        logger.error("AsmMethodAccessor for '{}' is null, and instead by NativeConstructorDeclare", constructor);
                    }
                    return new AsmNativeMethodDeclare(accessor);
                } catch (Exception ex) {
                    logger.error("Generate AsmMethodAccessor for '{}' failed, and instead by NativeConstructorDeclare: {}", constructor, ex);
                }
            } else {
                logger.warn("'{}' will not use asm, since this method is not public, and instead by NativeConstructorDeclare", constructor);
            }
        } else {
            logger.warn("'{}' will not use asm, since class is not public, and instead by NativeConstructorDeclare", constructor);
        }
        return super.createNativeConstructorDeclare(clazz, constructor, line, column);
    }

    @Override
    public MethodDeclare createNativeMethodDeclare(Class type, Method method, int line, int column) {
        if (ClassUtil.isPublic(type)) {
            if (ClassUtil.isPublic(method)) {
                try {
                    AsmMethodAccessor accessor = AsmMethodAccessorManager.getAccessor(method);
                    if (accessor == null) {
                        logger.error("AsmMethodAccessor for '{}' is null, and instead by NativeMethodDeclare", method);
                    }
                    return new AsmNativeMethodDeclare(accessor);
                } catch (Exception ex) {
                    logger.error("Generate AsmMethodAccessor for '{}' failed, and instead by NativeMethodDeclare:{}", method, ex);
                }
            } else {
                logger.warn("'{}' will not use asm, since this method is not public, and instead by NativeMethodDeclare", method);
            }
        } else {
            logger.warn("'{}' will not use asm, since class is not public, and instead by NativeMethodDeclare", method);
        }
        return super.createNativeMethodDeclare(type, method, line, column);
    }
}
