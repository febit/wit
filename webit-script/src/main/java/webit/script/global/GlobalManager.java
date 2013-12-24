// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.global;

import webit.script.util.SimpleBag;

/**
 *
 * @author zqq90 <zqq_90@163.com>
 */
public interface GlobalManager {

    int getGlobalIndex(String name);

    Object getGlobal(int index);

    void setGlobal(int index, Object value);

    void commit();

    boolean hasConst(String name);

    Object getConst(String name);

    public SimpleBag getConstBag();

    public SimpleBag getGlobalBag();
}
