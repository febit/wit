// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global;

import java.util.HashMap;
import java.util.Map;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.exceptions.ScriptRuntimeException;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public class DefaultGlobalManager implements GlobalManager, Initable {

    private final Map<String, Object> constMap;
    private Map<String, Object> globalMap;
    private Object[] globalContext;
    private Map<String, Integer> globalIndexerMap;
    private boolean committed = false;

    private Class[] registers;

    public DefaultGlobalManager() {
        this.constMap = new HashMap<String, Object>();
        this.globalMap = new HashMap<String, Object>();
    }

    public void init(Engine engine) {
        if (registers != null) {
            for (int i = 0, len = registers.length; i < len; i++) {
                try {
                    ((GlobalRegister) engine.getBean(registers[i]))
                            .regist(this);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public void commit() {
        if (!this.committed) {
            this.committed = true;
            final int size;
            this.globalContext = new Object[size = this.globalMap.size()];
            this.globalIndexerMap = new HashMap<String, Integer>((size * 4) / 3 + 1, 0.75f);
            int i = 0;
            for (Map.Entry<String, Object> entry : this.globalMap.entrySet()) {
                this.globalContext[i] = entry.getValue();
                this.globalIndexerMap.put(entry.getKey(), i);
                i++;
            }
            this.globalMap = null;
        }
    }

    public void setConst(String key, Object value) {
        if (!this.committed) {
            this.constMap.put(key, value);
        }
    }

    public void setGlobal(String key, Object value) {
        if (!this.committed) {
            this.globalMap.put(key, value);
        } else {
            int index;
            if ((index = this.getGlobalIndex(key)) >= 0) {
                this.setGlobal(index, value);
            } else {
                throw new ScriptRuntimeException("Not found global variant named: ".concat(key));
            }
        }
    }

    public int getGlobalIndex(String name) {
        Integer index;
        return (index = globalIndexerMap.get(name)) != null ? index : -1;
    }

    public Object getGlobal(String key) {
        int index;
        if ((index = this.getGlobalIndex(key)) >= 0) {
            return this.getGlobal(index);
        } else {
            throw new ScriptRuntimeException("Not found global variant named: ".concat(key));
        }
    }

    public Object getGlobal(int index) {
        return globalContext[index];
    }

    public void setGlobal(int index, Object value) {
        this.globalContext[index] = value;
    }

    public boolean hasConst(String name) {
        return this.constMap.containsKey(name);
    }

    public Object getConst(String name) {
        return this.constMap.get(name);
    }

    public void setRegisters(Class[] registers) {
        this.registers = registers;
    }

}
