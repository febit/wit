// Copyright (c) 2013, Webit Team. All Rights Reserved.

package webit.script.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import webit.script.core.ast.ClassNameBand;
import webit.script.exceptions.ParserException;
import webit.script.util.ClassUtil;

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

    public final boolean registPackage(String pkgName) {

        boolean result = pkgs.add(pkgName);
        if (result == false) {
            throw new ParserException("Duplicate package register: " + pkgName);
        }
        return result;
    }

    public boolean registClass(ClassNameBand classNameBand) throws ParserException {
        String className = classNameBand.getClassSimpleName();
        if (CLASS_CACHE.containsKey(className)) {
            throw new ParserException("Duplicate class simple name:" + classNameBand.getClassPureName());
        }
        if (classes.containsKey(className)) {
            throw new ParserException("Duplicate class register:" + classNameBand.getClassPureName());
        }
        String classFullName = classNameBand.getClassPureName();

        classes.put(className, classFullName);
        return true;

    }

    protected String findFullClassName(String simpleName) {
        String classPureName;

        classPureName = classes.get(simpleName);
        if (classPureName != null) {
            return classPureName;
        }

        //TODO:冲突检查
        for (Iterator<String> it = pkgs.iterator(); it.hasNext();) {
            String pkg = it.next();

            try {
                classPureName = pkg + "." + simpleName;
                ClassUtil.getClassByName(classPureName);
                return classPureName;
            } catch (ClassNotFoundException e) {
                //ignore
            }
        }

        return simpleName;
    }

    public Class<?> toClass(ClassNameBand classNameBand) {
        String classPureName;
        if (classNameBand.isSimpleName()) {



            String simpleName = classNameBand.getClassSimpleName();
            if (classNameBand.isArray() == false) {
                Class cls = CLASS_CACHE.get(simpleName);
                if (cls != null) {
                    return cls;
                }
            }
            classPureName = findFullClassName(simpleName);

        } else {
            classPureName = classNameBand.getClassPureName();
        }
        try {
            return ClassUtil.getClassByName(classPureName, classNameBand.getArrayDepth());
        } catch (ClassNotFoundException ex) {
            throw new ParserException(ex);
        }
    }
    private static final Map<String, Class<?>> CLASS_CACHE;

    static {

        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        classes.put("boolean", boolean.class);
        classes.put("char", char.class);
        classes.put("byte", byte.class);
        classes.put("short", short.class);
        classes.put("int", int.class);
        classes.put("long", long.class);
        classes.put("float", float.class);
        classes.put("double", double.class);
        classes.put("void", void.class);
        classes.put("Boolean", Boolean.class);
        classes.put("Character", Character.class);
        classes.put("Byte", Byte.class);
        classes.put("Short", Short.class);
        classes.put("Integer", Integer.class);
        classes.put("Long", Long.class);
        classes.put("Float", Float.class);
        classes.put("Double", Double.class);
        classes.put("Number", Number.class);
        classes.put("String", String.class);
        classes.put("Object", Object.class);
        classes.put("Class", Class.class);
        classes.put("Void", Void.class);

        CLASS_CACHE = classes;
    }
}
