// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.Configable;
import webit.script.Engine;
import webit.script.loggers.Logger;

/**
 *
 * @author zqq90
 */
public final class MultiLogger implements Logger, Configable {

    //Settings
    private Class[] loggers;
    //
    private Logger[] _loggers;

    @SuppressWarnings("unchecked")
    public void init(Engine engine) {
        if (loggers != null && loggers.length > 0) {
            int len = loggers.length;
            _loggers = new Logger[loggers.length];
            for (int i = 0; i < len; i++) {
                try {
                    _loggers[i] = (Logger) engine.getBean(loggers[i]);
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    public boolean isTraceEnabled() {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                if (_loggers[i].isTraceEnabled()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void trace(String msg) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].trace(msg);
            }
        }
    }

    public void trace(String format, Object... arguments) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].trace(format, arguments);
            }
        }
    }

    public void trace(String msg, Throwable t) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].trace(msg, t);
            }
        }
    }

    public boolean isDebugEnabled() {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                if (_loggers[i].isDebugEnabled()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void debug(String msg) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].debug(msg);
            }
        }
    }

    public void debug(String format, Object... arguments) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].debug(format, arguments);
            }
        }
    }

    public void debug(String msg, Throwable t) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].debug(msg, t);
            }
        }
    }

    public boolean isInfoEnabled() {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                if (_loggers[i].isInfoEnabled()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void info(String msg) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].info(msg);
            }
        }
    }

    public void info(String format, Object... arguments) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].info(format, arguments);
            }
        }
    }

    public void info(String msg, Throwable t) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].info(msg, t);
            }
        }
    }

    public boolean isWarnEnabled() {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                if (_loggers[i].isWarnEnabled()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void warn(String msg) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].warn(msg);
            }
        }
    }

    public void warn(String format, Object... arguments) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].warn(format, arguments);
            }
        }
    }

    public void warn(String msg, Throwable t) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].warn(msg, t);
            }
        }
    }

    public boolean isErrorEnabled() {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                if (_loggers[i].isErrorEnabled()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void error(String msg) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].error(msg);
            }
        }
    }

    public void error(String format, Object... arguments) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].error(format, arguments);
            }
        }
    }

    public void error(String msg, Throwable t) {
        if (_loggers != null) {
            for (int i = 0, len = _loggers.length; i < len; i++) {
                _loggers[i].error(msg, t);
            }
        }
    }

    public void setLoggers(Class[] loggers) {
        this.loggers = loggers;
    }
}
