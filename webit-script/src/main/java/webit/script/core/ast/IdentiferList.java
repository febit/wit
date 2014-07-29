// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author zqq
 */
public class IdentiferList implements Iterable<Identifer>{
    
    private final ArrayList<Identifer> identiferList;

    public IdentiferList() {
        this.identiferList = new ArrayList<Identifer>();
    }

    public IdentiferList add(Identifer identifer) {
        identiferList.add(identifer);
        return this;
    }
    
    public IdentiferList add(String name, int line, int column) {
        return add(new Identifer(name, line, column));
    }

    public Iterator<Identifer> iterator() {
       return identiferList.iterator();
    }
}
