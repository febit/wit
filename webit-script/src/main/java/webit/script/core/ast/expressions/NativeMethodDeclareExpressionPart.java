package webit.script.core.ast.expressions;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import webit.script.core.ast.StatmentPart;
import webit.script.exceptions.ParserException;
import webit.script.util.ReflectUtil;

/**
 *
 * @author Zqq
 */
public class NativeMethodDeclareExpressionPart extends StatmentPart {

    private boolean isStatic;
    private Class clazz;
    private String methodName;
    private List<Class> paramTypeList;

    public NativeMethodDeclareExpressionPart(int line, int column) {
        super(line, column);
        this.paramTypeList = new ArrayList<Class>(5);
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

    public NativeMethodDeclareExpressionPart setIsStatic(boolean isStatic) {
        this.isStatic = isStatic;
        return this;
    }

    @Override
    public NativeMethodDeclareExpression pop() {
        try {
            Class[] paramTypes = new Class[paramTypeList.size()];
            paramTypeList.toArray(paramTypes);

            Method method = ReflectUtil.searchMethod(clazz, methodName, paramTypes, false);
            return new NativeMethodDeclareExpression(method, paramTypes.length, isStatic, line, column);
        } catch (NoSuchMethodException ex) {
            throw new ParserException(ex.getMessage(), line, column);
        }
    }
}
