// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.Engine;
import webit.script.Initable;
import webit.script.loggers.AbstractLogger;
import webit.script.loggers.Logger;
import webit.script.util.ClassEntry;

/**
 *
 * @author zqq90
 */
public final class MultiLogger extends AbstractLogger implements Initable {

    private Logger[] _loggers;

    //Settings
    private ClassEntry[] loggers;

    public void init(Engine engine) {
        ClassEntry[] entrys = this.loggers;
        if (entrys != null) {
            int len = entrys.length;
            _loggers = new Logger[len];
            for (int i = 0; i < len; i++) {
                _loggers[i] = (Logger) engine.getComponent(entrys[i]);
            }
        } else {
            _loggers = new Logger[0];
        }
    }

    public boolean isEnabled(int level) {
        for (Logger logger : this._loggers) {
            if (logger.isEnabled(level)) {
                return true;
            }
        }
        return false;
    }

    public void log(int level, String msg) {
        for (Logger logger : this._loggers) {
            logger.log(level, msg);
        }
    }

    public void log(int level, String msg, Throwable throwable) {
        for (Logger logger : this._loggers) {
            logger.log(level, msg, throwable);
        }
    }

    public void setLoggers(ClassEntry[] loggers) {
        this.loggers = loggers;
    }
}
