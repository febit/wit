// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.loggers.AbstractLogger;
import webit.script.loggers.Logger;

/**
 *
 * @author zqq90
 */
public final class MultiLogger extends AbstractLogger {

    private Logger[] loggers;

    @Override
    public boolean isEnabled(int level) {
        for (Logger logger : this.loggers) {
            if (logger.isEnabled(level)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void log(int level, String msg) {
        for (Logger logger : this.loggers) {
            logger.log(level, msg);
        }
    }

    @Override
    public void log(int level, String msg, Throwable throwable) {
        for (Logger logger : this.loggers) {
            logger.log(level, msg, throwable);
        }
    }
}
