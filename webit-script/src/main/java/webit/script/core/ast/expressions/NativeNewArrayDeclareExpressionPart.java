// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core.ast.expressions;

import webit.script.core.ast.StatmentPart;
import webit.script.exceptions.NativeSecurityException;
import webit.script.exceptions.ParserException;
import webit.script.security.NativeSecurityManager;
import webit.script.util.NativeSecurityManagerUtil;

/**
 *
 * @author Zqq
 */
public class NativeNewArrayDeclareExpressionPart extends StatmentPart {

    private Class componentType;

    public NativeNewArrayDeclareExpressionPart(Class componentType, int line, int column) {
        super(line, column);
        this.componentType = componentType;
    }

    public NativeNewArrayDeclareExpression pop(NativeSecurityManager securityManager) {
        if (componentType != Void.class) {
            try {
                Class classWaitCheck = componentType;
                while (classWaitCheck.isArray()) {
                    classWaitCheck = classWaitCheck.getComponentType();
                }
                NativeSecurityManagerUtil.checkAccess(securityManager, classWaitCheck.getName() + ".[]");
                return new NativeNewArrayDeclareExpression(componentType, line, column);
            } catch (NativeSecurityException ex) {
                throw new ParserException(ex.getMessage(), line, column);
            }
        } else {
            throw new ParserException("componentType must not Void.class", line, column);
        }
    }
}
