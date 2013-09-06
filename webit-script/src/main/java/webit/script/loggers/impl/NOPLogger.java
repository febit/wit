// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.loggers.Logger;

/**
 *
 * @author zqq90
 */
public class NOPLogger implements Logger {

    public boolean isTraceEnabled() {
        return false;
    }

    public void trace(String msg) {
    }

    public void trace(String format, Object... arguments) {
    }

    public void trace(String msg, Throwable t) {
    }

    public boolean isDebugEnabled() {
        return false;
    }

    public void debug(String msg) {
    }

    public void debug(String format, Object... arguments) {
    }

    public void debug(String msg, Throwable t) {
    }

    public boolean isInfoEnabled() {
        return false;
    }

    public void info(String msg) {
    }

    public void info(String format, Object... arguments) {
    }

    public void info(String msg, Throwable t) {
    }

    public boolean isWarnEnabled() {
        return false;
    }

    public void warn(String msg) {
    }

    public void warn(String format, Object... arguments) {
    }

    public void warn(String msg, Throwable t) {
    }

    public boolean isErrorEnabled() {
        return false;
    }

    public void error(String msg) {
    }

    public void error(String format, Object... arguments) {
    }

    public void error(String msg, Throwable t) {
    }
}
