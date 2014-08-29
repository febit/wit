// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.Engine;
import webit.script.Initable;
import webit.script.loggers.AbstractLogger;

/**
 *
 * @author zqq90
 */
public class Slf4jLogger extends AbstractLogger implements Initable {

    private org.slf4j.Logger log;

    public void init(Engine engine) {
        log = org.slf4j.LoggerFactory.getLogger(name);
    }

    public boolean isEnabled(int level) {
        switch (level) {
            case LEVEL_DEBUG:
                return log.isDebugEnabled();
            case LEVEL_INFO:
                return log.isInfoEnabled();
            case LEVEL_WARN:
                return log.isWarnEnabled();
            case LEVEL_ERROR:
                return log.isErrorEnabled();
        }
        return false;
    }

    public void log(int level, String msg) {
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
        }
    }

    public void log(int level, String msg, Throwable throwable) {
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
        }
    }
}
