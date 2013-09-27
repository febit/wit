// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import java.util.LinkedList;
import java.util.List;
import webit.script.core.ast.Position;
import webit.script.exceptions.NativeSecurityException;
import webit.script.exceptions.ParseException;
import webit.script.security.NativeSecurityManager;
import webit.script.util.NativeSecurityManagerUtil;

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
    public NativeConstructorDeclareExpression pop(NativeSecurityManager securityManager) {
        try {

            NativeSecurityManagerUtil.checkAccess(securityManager, clazz.getName() + ".<init>");

            return new NativeConstructorDeclareExpression(
                    clazz.getConstructor(paramTypeList.toArray(new Class[paramTypeList.size()])),
                    line, column);
        } catch (NoSuchMethodException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (SecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        } catch (NativeSecurityException ex) {
            throw new ParseException(ex.getMessage(), line, column);
        }
    }
}
