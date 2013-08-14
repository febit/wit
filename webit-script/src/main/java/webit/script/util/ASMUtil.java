package webit.script.util;

import jodd.util.StringUtil;
import webit.script.asm4.Type;

/**
 *
 * @author Zqq
 */
public class ASMUtil {

    public static final Type BOOLEAN_BOXED_TYPE = Type.getType(Boolean.class);
    public static final Type CHAR_BOXED_TYPE = Type.getType(Character.class);
    public static final Type BYTE_BOXED_TYPE = Type.getType(Byte.class);
    public static final Type SHORT_BOXED_TYPE = Type.getType(Short.class);
    public static final Type INT_BOXED_TYPE = Type.getType(Integer.class);
    public static final Type LONG_BOXED_TYPE = Type.getType(Long.class);
    public static final Type FLOAT_BOXED_TYPE = Type.getType(Float.class);
    public static final Type DOUBLE_BOXED_TYPE = Type.getType(Double.class);

    private static class AsmClassLoader extends ClassLoader {

        public final Class<?> loadClass(String name, byte[] b, int off, int len)
                throws ClassFormatError {
            return defineClass(name, b, off, len, null);
        }
    };
    private static AsmClassLoader classLoader = new AsmClassLoader();

    public static Class loadClass(String name, byte[] b, int off, int len) {
        return classLoader.loadClass(name, b, off, len);
    }

    /**
     * "java.util.Object" to "java/util/Object"
     *
     * @param className
     * @return
     */
    public static String toAsmClassName(String className) {
        return StringUtil.replaceChar(className, '.', '/');
    }
}
