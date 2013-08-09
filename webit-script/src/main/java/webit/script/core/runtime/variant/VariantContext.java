package webit.script.core.runtime.variant;

import java.util.Map;

/**
 *
 * @author Zqq
 */
public class VariantContext {

    private static final Object[] EMPTY_ARRAY = new Object[0];
    private final Object[] values;
    private final VariantMap box;

    public VariantContext(VariantMap box) {
        this.box = box;
        int size = box.size();
        this.values = size != 0 ? new Object[size] : EMPTY_ARRAY;
    }

    public void exportTo(Map map) {
        for (int i = 0; i < values.length; i++) {
            map.put(box.getName(i), values[i]);
        }
    }

    public Object get(int index) {
        return values[index];
    }

    public boolean set(int index, Object value) {
        values[index] = value;
        return true;
    }

    public boolean set(String key, Object value) {
        int index = box.getIndex(key);
        if (index >= 0) {
            values[index] = value;
            return true;
        }
        return false;
    }

    public int getIndex(String key) {
        return box.getIndex(key);
    }

    public boolean hasKey(String key) {
        int index = box.getIndex(key);
        return index >= 0;
    }

    public int size() {
        return values.length;
    }
}
