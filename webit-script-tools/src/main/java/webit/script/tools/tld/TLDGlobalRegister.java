// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.tld;

import java.io.InputStream;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.core.NativeFactory;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.lang.Bag;
import webit.script.lang.MethodDeclare;
import webit.script.loggers.Logger;
import webit.script.util.ClassLoaderUtil;
import webit.script.util.ClassUtil;
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
    protected Engine engine;
    protected NativeFactory nativeFactory;

    public void regist(GlobalManager manager) {
        final InputStream inputStream = ClassLoaderUtil.getDefaultClassLoader()
                .getResourceAsStream(UnixStyleFileNameUtil.concat("META-INF/", tld));
        if (inputStream == null) {
            throw new RuntimeException("TLD file not found: " + tld);
        }
        Logger logger = this.engine.getLogger();
        if (logger.isInfoEnabled()) {
            logger.info("Load TLD file: "+ tld);
        }

        final Bag constBag = manager.getConstBag();

        try {
            TLDFunction[] functions = TLDDocumentParser.parse(inputStream);
            for (int i = 0, len = functions.length; i < len; i++) {
                TLDFunction func = functions[i];
                constBag.set(this.prefix + func.name, createMethodDeclare(func));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected MethodDeclare createMethodDeclare(TLDFunction func) throws ClassNotFoundException {

        final Class[] parameterTypes;
        final String[] parameterTypeNames = func.parameterTypes;
        if (parameterTypeNames == null) {
            parameterTypes = null;
        } else {
            int paramSize = parameterTypeNames.length;
            parameterTypes = new Class[paramSize];
            for (int j = 0; j < paramSize; j++) {
                parameterTypes[j] = ClassUtil.getClass(parameterTypeNames[j]);
            }
        }
        return this.nativeFactory.createNativeMethodDeclare(
                ClassUtil.getClass(func.declaredClass),
                func.methodName,
                parameterTypes,
                checkAccess);
    }

    public void init(Engine engine) {
        this.engine = engine;
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
