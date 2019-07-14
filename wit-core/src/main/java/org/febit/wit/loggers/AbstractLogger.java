// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loggers;

import org.febit.wit.util.StringUtil;

/**
 * @author zqq90
 */
@SuppressWarnings({
        "WeakerAccess"
})
public abstract class AbstractLogger implements Logger {

    protected static final int LEVEL_DEBUG = 1;
    protected static final int LEVEL_INFO = 2;
    protected static final int LEVEL_WARN = 3;
    protected static final int LEVEL_ERROR = 4;
    protected static final int LEVEL_OFF = 5;

    protected String name = "wit";

    protected abstract boolean isEnabled(int level);

    protected abstract void log(int level, String msg);

    protected abstract void log(int level, String msg, Throwable t);

    protected void log(int level, String format, Object... args) {
        if (isEnabled(level)) {
            log(level, StringUtil.format(format, args));
        }
    }

    @Override
    public boolean isDebugEnabled() {
        return isEnabled(LEVEL_DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return isEnabled(LEVEL_INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return isEnabled(LEVEL_WARN);
    }

    @Override
    public boolean isErrorEnabled() {
        return isEnabled(LEVEL_ERROR);
    }

    @Override
    public void debug(String msg) {
        log(LEVEL_DEBUG, msg);
    }

    @Override
    public void debug(String format, Object... args) {
        log(LEVEL_DEBUG, format, args);
    }

    @Override
    public void debug(String msg, Throwable t) {
        log(LEVEL_DEBUG, msg, t);
    }

    @Override
    public void info(String msg) {
        log(LEVEL_INFO, msg);
    }

    @Override
    public void info(String format, Object... args) {
        log(LEVEL_INFO, format, args);
    }

    @Override
    public void info(String msg, Throwable t) {
        log(LEVEL_INFO, msg, t);
    }

    @Override
    public void warn(String msg) {
        log(LEVEL_WARN, msg);
    }

    @Override
    public void warn(String format, Object... args) {
        log(LEVEL_WARN, format, args);
    }

    @Override
    public void warn(String msg, Throwable t) {
        log(LEVEL_WARN, msg, t);
    }

    @Override
    public void error(String msg) {
        log(LEVEL_ERROR, msg);
    }

    @Override
    public void error(String format, Object... args) {
        log(LEVEL_ERROR, format, args);
    }

    @Override
    public void error(String msg, Throwable t) {
        log(LEVEL_ERROR, msg, t);
    }
}
