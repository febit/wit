// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import webit.script.exceptions.ParseException;
import webit.script.util.ClassNameBand;
import webit.script.util.ClassUtil;
import webit.script.util.StringUtil;

/**
 *
 * @author Zqq
 */
public class NativeImportManager {

    public Map<String, String> classes;
    public Set<String> pkgs;

    public NativeImportManager() {
        classes = new HashMap<String, String>();
        pkgs = new HashSet<String>();
        registPackage("java.lang");
    }

    public final void registPackage(String pkgName) throws ParseException {
        if (pkgs.add(pkgName) == false) {
            throw new ParseException("Duplicate package register: ".concat(pkgName));
        }
    }

    public boolean registClass(ClassNameBand classNameBand) throws ParseException {
        final String className = classNameBand.getClassSimpleName();
        if (ClassUtil.getCachedClass(className) != null) {
            throw new ParseException("Duplicate class simple name:".concat(classNameBand.getClassPureName()));
        }
        if (classes.containsKey(className)) {
            throw new ParseException("Duplicate class register:".concat(classNameBand.getClassPureName()));
        }
        String classFullName = classNameBand.getClassPureName();

        classes.put(className, classFullName);
        return true;

    }

    protected String findFullClassName(String simpleName) {
        String classPureName;

        if ((classPureName = classes.get(simpleName)) != null) {
            return classPureName;
        }

        //TODO:冲突检查
        for (String pkg : pkgs) {
            try {
                classPureName = StringUtil.concat(pkg, ".", simpleName);
                ClassUtil.getClass(classPureName);
                classes.put(simpleName, classPureName); //put to cache
                return classPureName;
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }
        return simpleName;
    }

    //XXX:need rewrite
    public Class<?> toClass(ClassNameBand classNameBand) {
        String classPureName;
        if (classNameBand.isSimpleName()) {

            String simpleName = classNameBand.getClassSimpleName();
            if (classNameBand.isArray() == false) {
                Class cls = ClassUtil.getCachedClass(simpleName);
                if (cls != null) {
                    return cls;
                }
            }
            classPureName = findFullClassName(simpleName);

        } else {
            classPureName = classNameBand.getClassPureName();
        }
        try {
            return ClassUtil.getClass(classPureName, classNameBand.getArrayDepth());
        } catch (ClassNotFoundException ex) {
            throw new ParseException(ex);
        }
    }
}
