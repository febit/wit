// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import java.util.logging.Level;
import webit.script.Init;
import webit.script.loggers.AbstractLogger;

/**
 *
 * @author zqq90
 */
public final class Jdk14Logger extends AbstractLogger {

    private java.util.logging.Logger log;

    @Init
    public void init() {
        this.log = java.util.logging.Logger.getLogger(name);
    }

    @Override
    public boolean isEnabled(int level) {
        return log.isLoggable(getLevel(level));
    }

    @Override
    public void log(int level, String msg) {
        log.log(getLevel(level), msg);
    }

    @Override
    public void log(int level, String msg, Throwable throwable) {
        log.log(getLevel(level), msg, throwable);
    }

    protected static Level getLevel(int level) {
        switch (level) {
            case LEVEL_DEBUG:
                return Level.FINE;
            case LEVEL_INFO:
                return Level.INFO;
            case LEVEL_WARN:
                return Level.WARNING;
            case LEVEL_ERROR:
                return Level.SEVERE;
            default:
                return Level.FINEST;
        }
    }
}
