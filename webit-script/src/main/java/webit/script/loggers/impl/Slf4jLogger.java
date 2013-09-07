// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import jodd.petite.meta.PetiteInitMethod;
import webit.script.loggers.Logger;

/**
 *
 * @author zqq90
 */
public class Slf4jLogger implements Logger {

    //settings
    private String name = DEFAULT_NAME;
    //
    private org.slf4j.Logger logger;

    @PetiteInitMethod
    public void init() {
        logger = org.slf4j.LoggerFactory.getLogger(name);
    }

    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    public void trace(String msg) {
        logger.trace(msg);
    }

    public void trace(String format, Object... arguments) {
        logger.trace(format, arguments);
    }

    public void trace(String msg, Throwable t) {
        logger.trace(msg, t);
    }

    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    public void debug(String msg) {
        logger.debug(msg);
    }

    public void debug(String format, Object... arguments) {
        logger.debug(format, arguments);
    }

    public void debug(String msg, Throwable t) {
        logger.debug(msg, t);
    }

    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    public void info(String msg) {
        logger.info(msg);
    }

    public void info(String format, Object... arguments) {
        logger.info(format, arguments);
    }

    public void info(String msg, Throwable t) {
        logger.info(msg, t);
    }

    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String format, Object... arguments) {
        logger.warn(format, arguments);
    }

    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    public void error(String msg) {
        logger.error(msg);
    }

    public void error(String format, Object... arguments) {
        logger.error(format, arguments);
    }

    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    //
    public void setName(String name) {
        this.name = name;
    }
}
