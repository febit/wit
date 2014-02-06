// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.tld;

import java.io.InputStream;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.core.NativeFactory;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.ClassUtil;
import webit.script.util.SimpleBag;
import webit.script.util.UnixStyleFileNameUtil;

/**
 *
 * @author zqq90
 */
public class TLDGlobalRegister implements GlobalRegister, Initable {

    protected String prefix = "";
    protected String tld;
    protected boolean checkAccess = false;

    //
    protected NativeFactory nativeFactory;

    public void regist(GlobalManager manager) {
        final InputStream inputStream = ClassLoaderUtil.getDefaultClassLoader()
                .getResourceAsStream(UnixStyleFileNameUtil.concat("META-INF/", tld));
        if (inputStream == null) {
            throw new RuntimeException("TLD file not found: " + tld);
        }

        final SimpleBag constBag = manager.getConstBag();

        try {
            TLDFunction[] functions = TLDDocumentParser.parse(inputStream);

            for (int i = 0, len = functions.length; i < len; i++) {
                TLDFunction tLDFunction = functions[i];

                Class[] argumentTypes;
                String[] argumentTypeStrings = tLDFunction.argumentTypes;
                if (argumentTypeStrings == null) {
                    argumentTypes = null;
                } else {
                    int argsLen = argumentTypeStrings.length;
                    argumentTypes = new Class[argsLen];
                    for (int j = 0; j < argsLen; j++) {
                        argumentTypes[j] = ClassUtil.getClass(argumentTypeStrings[j]);
                    }
                }

                constBag.set(this.prefix + tLDFunction.name,
                        this.nativeFactory.createNativeMethodDeclare(
                                ClassUtil.getClass(tLDFunction.declaredClass),
                                tLDFunction.methodName, argumentTypes, checkAccess));

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void init(Engine engine) {
        this.nativeFactory = engine.getNativeFactory();
        if (tld == null) {
            throw new RuntimeException("TLDGlobalRegister.tld need be setted.");
        }
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setTld(String tld) {
        this.tld = tld;
    }

    public void setCheckAccess(boolean checkAccess) {
        this.checkAccess = checkAccess;
    }
}
