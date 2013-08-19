// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import webit.script.core.ast.StatmentPart;
import webit.script.exceptions.NativeSecurityException;
import webit.script.exceptions.ParserException;
import webit.script.security.NativeSecurityManager;
import webit.script.util.NativeSecurityManagerUtil;

/**
 *
 * @author Zqq
 */
public class NativeConstructorDeclareExpressionPart extends StatmentPart {

    private Class clazz;
    private List<Class> paramTypeList;

    public NativeConstructorDeclareExpressionPart(int line, int column) {
        super(line, column);
        this.paramTypeList = new ArrayList<Class>(5);
    }

    public NativeConstructorDeclareExpressionPart setClassName(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public NativeConstructorDeclareExpressionPart append(Class paramType) {
        paramTypeList.add(paramType);
        return this;
    }

    public NativeConstructorDeclareExpression pop(NativeSecurityManager securityManager) {
        try {
            Class[] paramTypes = new Class[paramTypeList.size()];
            paramTypeList.toArray(paramTypes);

            NativeSecurityManagerUtil.checkAccess(securityManager, clazz.getName() + ".<init>");

            Constructor constructor = clazz.getConstructor(paramTypes);
            return new NativeConstructorDeclareExpression(constructor, line, column);
        } catch (NoSuchMethodException ex) {
            throw new ParserException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParserException(ex.getMessage(), line, column);
        } catch (NativeSecurityException ex) {
            throw new ParserException(ex.getMessage(), line, column);
        }
    }
}
