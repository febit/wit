// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.core.ast;

import java.util.LinkedList;
import webit.script.core.NativeImportManager;
import webit.script.exceptions.ParseException;
import webit.script.util.ClassNameBand;

/**
 *
 * @author Zqq
 */
public class ClassNameList {

    private final LinkedList<Class> classList;
    private final NativeImportManager nativeImportMgr;

    public ClassNameList(NativeImportManager nativeImportMgr) {
        this.classList = new LinkedList<Class>();
        this.nativeImportMgr = nativeImportMgr;
    }

    public ClassNameList add(ClassNameBand classNameBand, int line, int column) throws ParseException {
        classList.add(nativeImportMgr.toClass(classNameBand, line, column));
        return this;
    }

    public Class[] toArray() {
        return classList.toArray(new Class[classList.size()]);
    }
}
