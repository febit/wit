// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import java.util.logging.Level;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.loggers.Logger;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public final class JDK14Logger implements Logger, Initable {

    private java.util.logging.Logger logger;

    //settings
    private String name = DEFAULT_NAME;

    public void init(Engine engine) {
        logger = java.util.logging.Logger.getLogger(name);
    }

    public boolean isTraceEnabled() {
        return logger.isLoggable(Level.FINER);
    }

    public void trace(String msg) {
        logger.log(Level.FINER, msg);
    }

    public void trace(String format, Object... arguments) {
        if (logger.isLoggable(Level.FINER)) {
            logger.log(Level.FINER, getMessage(format, arguments));
        }
    }

    public void trace(String msg, Throwable t) {
        logger.log(Level.FINER, msg, t);
    }

    public boolean isDebugEnabled() {
        return logger.isLoggable(Level.FINE);
    }

    public void debug(String msg) {
        logger.log(Level.FINE, msg);
    }

    public void debug(String format, Object... arguments) {
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, getMessage(format, arguments));
        }
    }

    public void debug(String msg, Throwable t) {
        logger.log(Level.FINE, msg, t);
    }

    public boolean isInfoEnabled() {
        return logger.isLoggable(Level.INFO);
    }

    public void info(String msg) {
        logger.log(Level.INFO, msg);
    }

    public void info(String format, Object... arguments) {
        if (logger.isLoggable(Level.INFO)) {
            logger.log(Level.INFO, getMessage(format, arguments));
        }
    }

    public void info(String msg, Throwable t) {
        logger.log(Level.INFO, msg, t);
    }

    public boolean isWarnEnabled() {
        return logger.isLoggable(Level.WARNING);
    }

    public void warn(String msg) {
        logger.log(Level.WARNING, msg);
    }

    public void warn(String format, Object... arguments) {
        if (logger.isLoggable(Level.WARNING)) {
            logger.log(Level.WARNING, getMessage(format, arguments));
        }
    }

    public void warn(String msg, Throwable t) {
        logger.log(Level.WARNING, msg, t);
    }

    public boolean isErrorEnabled() {
        return logger.isLoggable(Level.SEVERE);
    }

    public void error(String msg) {
        logger.log(Level.SEVERE, msg);
    }

    public void error(String format, Object... arguments) {
        if (logger.isLoggable(Level.SEVERE)) {
            logger.log(Level.SEVERE, getMessage(format, arguments));
        }
    }

    public void error(String msg, Throwable t) {
        logger.log(Level.SEVERE, msg, t);
    }

    private String getMessage(String msg, Object... args) {
        return StringUtil.format(msg, args);
    }

    public void setName(String name) {
        this.name = name;
    }
}
