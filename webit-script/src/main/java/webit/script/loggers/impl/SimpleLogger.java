// Copyright (c) 2013, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import jodd.petite.meta.PetiteInitMethod;
import webit.script.loggers.Logger;
import static webit.script.loggers.Logger.DEFAULT_NAME;
import webit.script.util.MessageFormatter;

/**
 *
 * @author zqq90
 */
public class SimpleLogger implements Logger {

    public static final int LEVEL_TRACE = 1;
    public static final int LEVEL_DEBUG = 2;
    public static final int LEVEL_INFO = 3;
    public static final int LEVEL_WARN = 4;
    public static final int LEVEL_ERROR = 5;
    //
    private String prefix;
    //settings
    private String level = "info";
    private String name = DEFAULT_NAME;
    //
    private boolean _trace = false;
    private boolean _debug = false;
    private boolean _info = false;
    private boolean _warn = false;
    private boolean _error = false;

    @PetiteInitMethod
    public void init() {
        prefix = "[" + name + "] ";
        level = level.trim().toLowerCase();
        
        _error = true;
        if ("error".equals(level)) {
            return;
        }
        _warn = true;
        if ("warn".equals(level)) {
            return;
        }
        _info = true;
        if ("info".equals(level)) {
            return;
        }
        _debug = true;
        if ("debug".equals(level)) {
            return;
        }
        _trace = true;
    }

    private String getMessage(String msg) {
        if (prefix == null) {
            return msg;
        }
        return prefix + msg;
    }

    private String getMessage(String msg, Object... args) {
        return getMessage(MessageFormatter.format(msg, args));
    }

    public final boolean isTraceEnabled() {
        return _trace;
    }

    public void trace(String msg) {
        if (_trace) {
            System.out.println(getMessage(msg));
        }
    }

    public void trace(String format, Object... arguments) {
        if (_trace) {
            System.out.println(getMessage(format, arguments));
        }
    }

    public void trace(String msg, Throwable t) {
        if (_trace) {
            System.out.println(getMessage(msg));
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }

    public boolean isDebugEnabled() {
        return _debug;
    }

    public void debug(String msg) {
        if (_debug) {
            System.out.println(getMessage(msg));
        }
    }

    public void debug(String format, Object... arguments) {
        if (_debug) {
            System.out.println(getMessage(format, arguments));
        }
    }

    public void debug(String msg, Throwable t) {
        if (_debug) {
            System.out.println(getMessage(msg));
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }

    public boolean isInfoEnabled() {
        return _info;
    }

    public void info(String msg) {
        if (_info) {
            System.out.println(getMessage(msg));
        }
    }

    public void info(String format, Object... arguments) {
        if (_info) {
            System.out.println(getMessage(format, arguments));
        }
    }

    public void info(String msg, Throwable t) {
        if (_info) {
            System.out.println(getMessage(msg));
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }

    public boolean isWarnEnabled() {
        return _warn;
    }

    public void warn(String msg) {
        if (_warn) {
            System.err.println(getMessage(msg));
        }
    }

    public void warn(String format, Object... arguments) {
        if (_warn) {
            System.err.println(getMessage(format, arguments));
        }
    }

    public void warn(String msg, Throwable t) {
        if (_warn) {
            System.err.println(getMessage(msg));
            if (t != null) {
                t.printStackTrace(System.err);
            }
        }
    }

    public boolean isErrorEnabled() {
        return _error;
    }

    public void error(String msg) {
        if (_error) {
            System.err.println(getMessage(msg));
        }
    }

    public void error(String format, Object... arguments) {
        if (_error) {
            System.err.println(getMessage(format, arguments));
        }
    }

    public void error(String msg, Throwable t) {
        if (_error) {
            System.err.println(getMessage(msg));
            if (t != null) {
                t.printStackTrace(System.err);
            }
        }
    }

    //
    public void setLevel(String level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }
}
