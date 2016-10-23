// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.tools.tld;

import java.io.IOException;
import java.io.InputStream;
import org.febit.wit.Engine;
import org.febit.wit.core.NativeFactory;
import org.febit.wit.global.GlobalManager;
import org.febit.wit.global.GlobalRegister;
import org.febit.wit.lang.MethodDeclare;
import org.febit.wit.loggers.Logger;
import org.febit.wit.util.ClassUtil;
import org.febit.wit.util.FileNameUtil;

/**
 *
 * @author zqq90
 */
public class TLDGlobalRegister implements GlobalRegister{

    protected String prefix = "";
    protected String tld;
    protected boolean checkAccess;

    protected Engine engine;
    protected NativeFactory nativeFactory;

    @Override
    public void regist(GlobalManager manager) {
        final InputStream input = ClassUtil.getDefaultClassLoader()
                .getResourceAsStream(FileNameUtil.concat("META-INF/", tld));
        if (input == null) {
            throw new RuntimeException("TLD file not found: " + tld);
        }
        Logger logger = this.engine.getLogger();
        if (logger.isInfoEnabled()) {
            logger.info("Load TLD file: " + tld);
        }

        try {
            TLDFunction[] functions = TLDDocumentParser.parse(input);
            for (int i = 0, len = functions.length; i < len; i++) {
                TLDFunction func = functions[i];
                manager.setConst(this.prefix + func.name, createMethodDeclare(func));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
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
        return this.nativeFactory.getNativeMethodDeclare(
                ClassUtil.getClass(func.declaredClass),
                func.methodName,
                parameterTypes,
                checkAccess);
    }
}
