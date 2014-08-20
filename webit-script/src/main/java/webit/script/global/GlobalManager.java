// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.global;

import webit.script.lang.Bag;

/**
 *
 * @author zqq90
 */
public interface GlobalManager {

    int getGlobalIndex(String name);

    Object getGlobal(int index);

    void setGlobal(int index, Object value);

    void commit();

    boolean hasConst(String name);

    Object getConst(String name);

    public Bag getConstBag();

    public Bag getGlobalBag();
}
