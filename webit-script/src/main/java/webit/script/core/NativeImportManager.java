// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.HashMap;
import java.util.Map;
import webit.script.exceptions.ParseException;
import webit.script.util.ClassNameBand;
import webit.script.util.ClassUtil;

/**
 *
 * @author Zqq
 */
public class NativeImportManager {

    public Map<String, String> classes;

    public NativeImportManager() {
        classes = new HashMap<String, String>();
    }

    public boolean registClass(ClassNameBand classNameBand, int line, int column) throws ParseException {
        final String className = classNameBand.getClassSimpleName();
        if (ClassUtil.getCachedClass(className) != null) {
            throw new ParseException("Duplicate class simple name:".concat(classNameBand.getClassPureName()), line, column);
        }
        if (classes.containsKey(className)) {
            throw new ParseException("Duplicate class register:".concat(classNameBand.getClassPureName()), line, column);
        }
        String classFullName = classNameBand.getClassPureName();

        classes.put(className, classFullName);
        return true;
    }

    public Class<?> toClass(ClassNameBand classNameBand, int line, int column) throws ParseException {
        String classPureName;
        if (classNameBand.isSimpleName()) {
            //1. find from @imports
            //2. if not array, find from cached
            //3. find from java.lang.*
            //4. use simpleName
            String simpleName = classNameBand.getClassSimpleName();
            classPureName = classes.get(simpleName);
            if (classPureName == null) {
                if (!classNameBand.isArray()) {
                    Class cls = ClassUtil.getCachedClass(simpleName);
                    if (cls != null) {
                        return cls;
                    }
                }
                try {
                    classPureName = "java.lang.".concat(simpleName);
                    ClassUtil.getClass(classPureName);
                } catch (ClassNotFoundException e) {
                    classPureName = simpleName;
                }
            }
        } else {
            classPureName = classNameBand.getClassPureName();
        }
        try {
            return ClassUtil.getClass(classPureName, classNameBand.getArrayDepth());
        } catch (ClassNotFoundException ex) {
            throw new ParseException("Class not found:".concat(classPureName), line, column);
        }
    }
}
