// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.loggers.AbstractLogger;
import webit.script.Engine;
import webit.script.Initable;
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
        if (loggers != null && loggers.length > 0) {
            int len = loggers.length;
            _loggers = new Logger[len];
            for (int i = 0; i < len; i++) {
                _loggers[i] = (Logger) engine.getComponent(loggers[i]);
            }
        }
    }

    public boolean isEnabled(int level) {
        Logger[] logs = this._loggers;
        if (logs != null) {
            for (int i = 0, len = logs.length; i < len; i++) {
                if (logs[i].isEnabled(level)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void log(int level, String msg) {
        Logger[] logs = this._loggers;
        if (logs != null) {
            for (int i = 0, len = logs.length; i < len; i++) {
                logs[i].log(level, msg);
            }
        }
    }

    public void log(int level, String msg, Throwable throwable) {
        Logger[] logs = this._loggers;
        if (logs != null) {
            for (int i = 0, len = logs.length; i < len; i++) {
                logs[i].log(level, msg, throwable);
            }
        }
    }

    public void setLoggers(ClassEntry[] loggers) {
        this.loggers = loggers;
    }
}
