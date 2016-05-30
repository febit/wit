// Copyright (c) 2013-2015, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.loggers.Logger;

/**
 *
 * @author zqq90
 */
public final class NoneLogger implements Logger {

    @Override
    public boolean isEnabled(int level) {
        return false;
    }

    @Override
    public void log(int level, String msg) {
    }

    @Override
    public void log(int level, String format, Object... args) {
    }

    @Override
    public void log(int level, String msg, Throwable t) {
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String msg) {
    }

    @Override
    public void debug(String format, Object... args) {
    }

    @Override
    public void debug(String msg, Throwable t) {
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String msg) {
    }

    @Override
    public void info(String format, Object... args) {
    }

    @Override
    public void info(String msg, Throwable t) {
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String msg) {
    }

    @Override
    public void warn(String format, Object... args) {
    }

    @Override
    public void warn(String msg, Throwable t) {
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String msg) {
    }

    @Override
    public void error(String format, Object... args) {
    }

    @Override
    public void error(String msg, Throwable t) {
    }
}
