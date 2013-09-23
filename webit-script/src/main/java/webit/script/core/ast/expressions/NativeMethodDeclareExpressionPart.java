// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.LinkedList;
import java.util.List;
import webit.script.core.ast.StatmentPart;
import webit.script.exceptions.NativeSecurityException;
import webit.script.exceptions.ParserException;
import webit.script.security.NativeSecurityManager;
import webit.script.util.ClassUtil;
import webit.script.util.NativeSecurityManagerUtil;

/**
 *
 * @author Zqq
 */
public class NativeMethodDeclareExpressionPart extends StatmentPart {

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

    public NativeMethodDeclareExpression pop(NativeSecurityManager securityManager) {
        try {
            NativeSecurityManagerUtil.checkAccess(securityManager, clazz.getName() + "." + methodName);

            return new NativeMethodDeclareExpression(
                    ClassUtil.searchMethod(clazz, methodName, paramTypeList.toArray(new Class[paramTypeList.size()]), false),
                    line, column);
        } catch (NoSuchMethodException ex) {
            throw new ParserException(ex.getMessage(), line, column);
        } catch (NativeSecurityException ex) {
            throw new ParserException(ex.getMessage(), line, column);
        }
    }
}
