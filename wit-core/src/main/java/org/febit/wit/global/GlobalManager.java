// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.global;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.febit.wit.Init;
import org.febit.wit.exceptions.UncheckedException;
import org.febit.wit.lang.Bag;
import org.febit.wit.util.ArrayUtil;

/**
 *
 * @author zqq90
 */
public class GlobalManager {

    private final Map<String, Object> constMap;
    private final Map<String, Object> driftedGlobalMap;
    private final Map<String, Integer> globalIndexer;
    private Object[] globalContext = ArrayUtil.emptyObjects();

    //settings
    protected GlobalRegister[] registers;

    public GlobalManager() {
        this.constMap = new HashMap<>();
        this.driftedGlobalMap = new HashMap<>();
        this.globalIndexer = new HashMap<>();
    }

    @Init
    public void init() {
        if (registers != null) {
            try {
                for (GlobalRegister register : registers) {
                    register.regist(this);
                    this.commit();
                }
            } catch (Exception ex) {
                throw new UncheckedException(ex);
            }
        }
        commit();
    }

    public void clear() {
        this.driftedGlobalMap.clear();
        this.globalIndexer.clear();
        this.constMap.clear();
        Object[] myGlobalContext = this.globalContext;
        if (myGlobalContext != null) {
            Arrays.fill(myGlobalContext, null);
        }
        init();
    }

    public void commit() {
        if (this.driftedGlobalMap.isEmpty()) {
            return;
        }
        final int oldSize;
        final Object[] oldGlobalContext = this.globalContext;
        oldSize = oldGlobalContext != null ? oldGlobalContext.length : 0;

        final Object[] newGlobalContext = new Object[oldSize + this.driftedGlobalMap.size()];
        this.globalContext = newGlobalContext;
        if (oldSize > 0) {
            //Copy old data
            System.arraycopy(oldGlobalContext, 0, newGlobalContext, 0, oldSize);
        }

        int i = oldSize;
        for (Map.Entry<String, Object> entry : this.driftedGlobalMap.entrySet()) {
            newGlobalContext[i] = entry.getValue();
            this.globalIndexer.put(entry.getKey(), i);
            i++;
        }
        this.driftedGlobalMap.clear();
    }

    public void setConst(String key, Object value) {
        this.constMap.put(key, value);
    }

    public void setGlobal(String key, Object value) {
        int index = this.getGlobalIndex(key);
        if (index >= 0) {
            this.setGlobal(index, value);
        } else {
            this.driftedGlobalMap.put(key, value);
        }
    }

    public int getGlobalIndex(String name) {
        Integer index = globalIndexer.get(name);
        return index != null ? index : -1;
    }

    public Object getGlobal(String key) {
        int index = this.getGlobalIndex(key);
        if (index >= 0) {
            return this.getGlobal(index);
        } else {
            return this.driftedGlobalMap.get(key);
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

    public Bag getConstBag() {
        return new Bag() {

            @Override
            public Object get(Object key) {
                return GlobalManager.this.getConst(String.valueOf(key));
            }

            @Override
            public void set(Object key, Object value) {
                GlobalManager.this.setConst(String.valueOf(key), value);
            }
        };
    }

    public Bag getGlobalBag() {
        return new Bag() {

            @Override
            public Object get(Object key) {
                return GlobalManager.this.getGlobal(String.valueOf(key));
            }

            @Override
            public void set(Object key, Object value) {
                GlobalManager.this.setGlobal(String.valueOf(key), value);
            }
        };
    }
}
