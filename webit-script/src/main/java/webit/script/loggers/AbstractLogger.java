// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loggers;

import webit.script.util.StringUtil;

/**
 *
 * @author zqq
 */
public abstract class AbstractLogger implements Logger {

    public static final String DEFAULT_NAME = "WebitScript";

    protected String name = DEFAULT_NAME;

    public void setName(String name) {
        this.name = name;
    }

    public void log(int level, String format, Object... args) {
        if (isEnabled(level)) {
            log(level, StringUtil.format(format, args));
        }
    }

    public boolean isDebugEnabled() {
        return isEnabled(LEVEL_DEBUG);
    }

    public boolean isInfoEnabled() {
        return isEnabled(LEVEL_INFO);
    }

    public boolean isWarnEnabled() {
        return isEnabled(LEVEL_WARN);
    }

    public boolean isErrorEnabled() {
        return isEnabled(LEVEL_ERROR);
    }

    public void debug(String msg) {
        log(LEVEL_DEBUG, msg);
    }

    public void debug(String format, Object... args) {
        log(LEVEL_DEBUG, format, args);
    }

    public void debug(String msg, Throwable t) {
        log(LEVEL_DEBUG, msg, t);
    }

    public void info(String msg) {
        log(LEVEL_INFO, msg);
    }

    public void info(String format, Object... args) {
        log(LEVEL_INFO, format, args);
    }

    public void info(String msg, Throwable t) {
        log(LEVEL_INFO, msg, t);
    }

    public void warn(String msg) {
        log(LEVEL_WARN, msg);
    }

    public void warn(String format, Object... args) {
        log(LEVEL_WARN, format, args);
    }

    public void warn(String msg, Throwable t) {
        log(LEVEL_WARN, msg, t);
    }

    public void error(String msg) {
        log(LEVEL_ERROR, msg);
    }

    public void error(String format, Object... args) {
        log(LEVEL_ERROR, format, args);
    }

    public void error(String msg, Throwable t) {
        log(LEVEL_ERROR, msg, t);
    }
}
