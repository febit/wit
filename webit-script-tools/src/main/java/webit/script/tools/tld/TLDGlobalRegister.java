// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.tools.tld;

import java.io.IOException;
import java.io.InputStream;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.core.NativeFactory;
import webit.script.global.GlobalManager;
import webit.script.global.GlobalRegister;
import webit.script.lang.MethodDeclare;
import webit.script.loggers.Logger;
import webit.script.util.ClassUtil;
import webit.script.util.FileNameUtil;

/**
 *
 * @author zqq90
 */
public class TLDGlobalRegister implements GlobalRegister, Initable {

    protected String prefix = "";
    protected String tld;
    protected boolean checkAccess;

    protected Engine engine;
    protected NativeFactory nativeFactory;

    public void regist(GlobalManager manager) {
        final InputStream input = ClassUtil.getDefaultClassLoader()
                .getResourceAsStream(FileNameUtil.concat("META-INF/", tld));
        if (input == null) {
            throw new RuntimeException("TLD file not found: " + tld);
        }
        Logger logger = this.engine.getLogger();
        if (logger.isInfoEnabled()) {
            logger.info("Load TLD file: "+ tld);
        }

        try {
            TLDFunction[] functions = TLDDocumentParser.parse(input);
            for (int i = 0, len = functions.length; i < len; i++) {
                TLDFunction func = functions[i];
                manager.setConst(this.prefix + func.name, createMethodDeclare(func));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally{
            try {
                input.close();
            } catch (IOException ignore) {
            }
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
