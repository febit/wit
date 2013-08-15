package webit.script.asm;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Zqq
 */
public class AsmMethodCallerManager {

    private final static Map<Method, AsmMethodCallerBox> asmMethodCallerMap = new HashMap<Method, AsmMethodCallerBox>();
    private final static AsmMethodCallerGenerator asmMethodCallerGenerator = new AsmMethodCallerGenerator();

    public static AsmMethodCaller generateCaller(Method method) throws Exception {
        AsmMethodCallerBox box = asmMethodCallerMap.get(method);
        if (box == null) {
            synchronized (asmMethodCallerMap) {
                box = asmMethodCallerMap.get(method);
                if (box == null) {
                    box = new AsmMethodCallerBox(method);
                    asmMethodCallerMap.put(method, box);
                }
            }
        }
        //
        AsmMethodCaller caller = box.getCaller();
        if (caller == null) {
            synchronized (box) {
                caller = box.getCaller();
                if (caller == null) {
                    caller = (AsmMethodCaller) asmMethodCallerGenerator.generateCaller(method).newInstance();
                    box.setCaller(caller);
                }
            }
        }
        return caller;
    }

    private static class AsmMethodCallerBox {

        private final Method method;
        private AsmMethodCaller caller;

        public AsmMethodCallerBox(Method method) {
            this.method = method;
        }

        public AsmMethodCaller getCaller() {
            return caller;
        }

        public void setCaller(AsmMethodCaller caller) {
            this.caller = caller;
        }

        @Override
        public int hashCode() {
            return this.method != null ? this.method.hashCode() : 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AsmMethodCallerBox other = (AsmMethodCallerBox) obj;
            if (this.method != other.method && (this.method == null || !this.method.equals(other.method))) {
                return false;
            }
            return true;
        }
    }
}
