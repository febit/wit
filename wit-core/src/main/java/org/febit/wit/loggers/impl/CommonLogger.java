// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loggers.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.febit.wit.Init;
import org.febit.wit.loggers.AbstractLogger;

/**
 *
 * @author zqq90
 */
public class CommonLogger extends AbstractLogger {

    private Log log;

    @Init
    public void init() {
        log = LogFactory.getLog(name);
    }

    @Override
    protected boolean isEnabled(int level) {
        switch (level) {
            case LEVEL_DEBUG:
                return log.isDebugEnabled();
            case LEVEL_INFO:
                return log.isInfoEnabled();
            case LEVEL_WARN:
                return log.isWarnEnabled();
            case LEVEL_ERROR:
                return log.isErrorEnabled();
            case LEVEL_OFF:
            default:
                return false;
        }
    }

    @Override
    protected void log(int level, String msg) {
        switch (level) {
            case LEVEL_DEBUG:
                log.debug(msg);
                return;
            case LEVEL_INFO:
                log.info(msg);
                return;
            case LEVEL_WARN:
                log.warn(msg);
                return;
            case LEVEL_ERROR:
                log.error(msg);
                return;
            case LEVEL_OFF:
            default:
        }
    }

    @Override
    protected void log(int level, String msg, Throwable throwable) {
        switch (level) {
            case LEVEL_DEBUG:
                log.debug(msg, throwable);
                return;
            case LEVEL_INFO:
                log.info(msg, throwable);
                return;
            case LEVEL_WARN:
                log.warn(msg, throwable);
                return;
            case LEVEL_ERROR:
                log.error(msg, throwable);
                return;
            case LEVEL_OFF:
            default:
        }
    }
}
