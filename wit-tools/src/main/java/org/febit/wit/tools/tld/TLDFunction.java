// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.tools.tld;

/**
 *
 * @author zqq90
 */
public final class TLDFunction {

    final String name;
    final String declaredClass;
    final String returnType;
    final String methodName;
    final String[] parameterTypes;

    TLDFunction(String name, String declaredClass, String returnType, String methodName, String[] parameterTypes) {
        this.name = name;
        this.declaredClass = declaredClass;
        this.returnType = returnType;
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
    }

    public String getName() {
        return name;
    }

    public String getDeclaredClass() {
        return declaredClass;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }
}
