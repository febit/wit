// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.tools;

import java.lang.reflect.Method;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import webit.script.util.bean.FieldInfo;
import webit.script.util.bean.FieldInfoResolver;

/**
 *
 * @author Zqq
 */
public class ResolverGenerator {

    @Test
    public void hash() {
        System.out.println("attributes".hashCode());
        System.out.println("parameters".hashCode());
    }

    //@Test
    public void generateGetCase() {

        Class cls = HttpServletRequest.class;
        FieldInfo[] all = FieldInfoResolver.resolver(cls);
        for (int i = 0; i < all.length; i++) {
            FieldInfo fieldInfo = all[i];
            Method getter = fieldInfo.getGetterMethod();
            if (getter != null) {
                //return book.getName();
                System.out.println("case " + fieldInfo.name.hashCode() + ":");
                System.out.println("if(\"" + fieldInfo.name + "\" == property || \"" + fieldInfo.name + "\".equals(property)){");
                System.out.println("return ((" + cls.getName() + ")bean)." + getter.getName() + "();}\nbreak;");
            } else if (fieldInfo.getField() != null) {
                //return book.name;
                System.out.println("case " + fieldInfo.name.hashCode() + ":");
                System.out.println("if(\"" + fieldInfo.name + "\" == property || \"" + fieldInfo.name + "\".equals(property)){");
                System.out.println("return ((" + cls.getName() + ")bean)." + fieldInfo.getField().getName() + ";}\nbreak;");
            }
        }
    }

    //@Test
    public void generateSetCase() {
        Class cls = HttpServletRequest.class;
        FieldInfo[] all = FieldInfoResolver.resolver(cls);
        for (int i = 0; i < all.length; i++) {
            FieldInfo fieldInfo = all[i];
            Method setter = fieldInfo.getSetterMethod();
            if (setter != null) {
                //return book.getName();
                System.out.println("case " + fieldInfo.name.hashCode() + ":");
                System.out.println("if(\"" + fieldInfo.name + "\" == property || \"" + fieldInfo.name + "\".equals(property)){");
                System.out.println("((" + cls.getName() + ")bean)." + setter.getName() + "(value);return true;}\nbreak;");
            } else if (fieldInfo.getField() != null) {
                //return book.name;
                System.out.println("case " + fieldInfo.name.hashCode() + ":");
                System.out.println("if(\"" + fieldInfo.name + "\" == property || \"" + fieldInfo.name + "\".equals(property)){");
                System.out.println("((" + cls.getName() + ")bean)." + fieldInfo.getField().getName() + " = value;return true;}\nbreak;");
            }
        }
    }
}
