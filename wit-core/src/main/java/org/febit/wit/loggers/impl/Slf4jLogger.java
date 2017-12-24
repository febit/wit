// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loggers.impl;

import org.febit.wit.Init;
import org.febit.wit.loggers.AbstractLogger;

/**
 *
 * @author zqq90
 */
public class Slf4jLogger extends AbstractLogger {

    private org.slf4j.Logger log;

    @Init
    public void init() {
        log = org.slf4j.LoggerFactory.getLogger(name);
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
                return;
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
                return;
        }
    }
}
