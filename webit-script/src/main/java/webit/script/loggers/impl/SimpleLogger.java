// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import java.io.PrintStream;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.loggers.Logger;
import webit.script.util.ExceptionUtil;
import webit.script.util.MessageFormatter;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public final class SimpleLogger implements Logger, Initable {

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

    public void init(Engine engine) {
        prefix = StringUtil.concat("[", name, "] ");
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

    public boolean isTraceEnabled() {
        return _trace;
    }

    public void trace(String msg) {
        if (_trace) {
            printStackTrace(msg, null, false);
        }
    }

    public void trace(String format, Object... arguments) {
        if (_trace) {
            printStackTrace(format, null, false, arguments);
        }
    }

    public void trace(String msg, Throwable throwable) {
        if (_trace) {
            printStackTrace(msg, throwable, false);
        }
    }

    public boolean isDebugEnabled() {
        return _debug;
    }

    public void debug(String msg) {
        if (_debug) {
            printStackTrace(msg, null, false);
        }
    }

    public void debug(String format, Object... arguments) {
        if (_debug) {
            printStackTrace(format, null, false, arguments);
        }
    }

    public void debug(String msg, Throwable throwable) {
        if (_debug) {
            printStackTrace(msg, throwable, false);
        }
    }

    public boolean isInfoEnabled() {
        return _info;
    }

    public void info(String msg) {
        if (_info) {
            printStackTrace(msg, null, false);
        }
    }

    public void info(String format, Object... arguments) {
        if (_info) {
            printStackTrace(format, null, false, arguments);
        }
    }

    public void info(String msg, Throwable throwable) {
        if (_info) {
            printStackTrace(msg, throwable, false);
        }
    }

    public boolean isWarnEnabled() {
        return _warn;
    }

    public void warn(String msg) {
        if (_warn) {
            printStackTrace(msg, null, true);
        }
    }

    public void warn(String format, Object... arguments) {
        if (_warn) {
            printStackTrace(format, null, true, arguments);
        }
    }

    public void warn(String msg, Throwable throwable) {
        if (_warn) {
            printStackTrace(msg, throwable, true);
        }
    }

    public boolean isErrorEnabled() {
        return _error;
    }

    public void error(String msg) {
        if (_error) {
            printStackTrace(msg, null, true);
        }
    }

    public void error(String format, Object... arguments) {
        if (_error) {
            printStackTrace(format, null, true, arguments);
        }
    }

    public void error(String msg, Throwable throwable) {
        if (_error) {
            printStackTrace(msg, throwable, true);
        }
    }

    private String getMessage(String msg) {
        if (prefix == null) {
            return msg;
        }
        return prefix.concat(msg != null ? msg : "null");
    }

    private String format(String format, Object... args) {
        return MessageFormatter.format(format, args);
    }

    private void printStackTrace(String format, Throwable throwable, boolean error, Object... arguments) {
        printStackTrace(format(format, arguments), throwable, error);
    }

    private void printStackTrace(String msg, Throwable throwable, boolean error) {
        PrintStream out = error ? System.err : System.out;
        out.print(getMessage(msg));
        if (throwable != null) {
            ExceptionUtil.printStackTrace(throwable, out);
        }
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }
}
