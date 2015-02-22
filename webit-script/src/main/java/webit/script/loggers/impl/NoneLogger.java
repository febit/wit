// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.loggers.Logger;

/**
 *
 * @author zqq90
 */
public final class NoneLogger implements Logger {

    public boolean isEnabled(int level) {
        return false;
    }

    public void log(int level, String msg) {
    }

    public void log(int level, String format, Object... args) {
    }

    public void log(int level, String msg, Throwable t) {
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void debug(String msg) {
    }

    public void debug(String format, Object... args) {
    }

    public void debug(String msg, Throwable t) {
    }

    public boolean isInfoEnabled() {
        return false;
    }

    public void info(String msg) {
    }

    public void info(String format, Object... args) {
    }

    public void info(String msg, Throwable t) {
    }

    public boolean isWarnEnabled() {
        return false;
    }

    public void warn(String msg) {
    }

    public void warn(String format, Object... args) {
    }

    public void warn(String msg, Throwable t) {
    }

    public boolean isErrorEnabled() {
        return false;
    }

    public void error(String msg) {
    }

    public void error(String format, Object... args) {
    }

    public void error(String msg, Throwable t) {
    }
}
