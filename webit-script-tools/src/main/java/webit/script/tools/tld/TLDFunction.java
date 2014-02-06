// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.tld;

/**
 *
 * @author zqq90
 */
public class TLDFunction {

    protected final String name;
    protected final String declaredClass;
    protected final String methodName;
    protected final String[] argumentTypes;

    public TLDFunction(String name, String declaredClass, String methodName, String[] argumentTypes) {
        this.name = name;
        this.declaredClass = declaredClass;
        this.methodName = methodName;
        this.argumentTypes = argumentTypes;
    }

    public String getName() {
        return name;
    }

    public String getDeclaredClass() {
        return declaredClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getArgumentTypes() {
        return argumentTypes;
    }
}
