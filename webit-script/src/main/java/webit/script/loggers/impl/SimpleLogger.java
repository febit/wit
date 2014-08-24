// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import java.io.PrintStream;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.loggers.Logger;
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

    //settings
    private String level = "info";
    private String name = DEFAULT_NAME;

    private String prefix;
    private boolean traceFlag = false;
    private boolean debugFlag = false;
    private boolean infoFlag = false;
    private boolean warnFlag = false;
    private boolean errorFlag = false;

    public void init(Engine engine) {
        prefix = StringUtil.concat("[", name, "] ");
        level = level.trim().toLowerCase();

        errorFlag = true;
        if ("error".equals(level)) {
            return;
        }
        warnFlag = true;
        if ("warn".equals(level)) {
            return;
        }
        infoFlag = true;
        if ("info".equals(level)) {
            return;
        }
        debugFlag = true;
        if ("debug".equals(level)) {
            return;
        }
        traceFlag = true;
    }

    public boolean isTraceEnabled() {
        return traceFlag;
    }

    public void trace(String msg) {
        if (traceFlag) {
            printStackTrace(msg, null, false);
        }
    }

    public void trace(String format, Object... arguments) {
        if (traceFlag) {
            printStackTrace(format, null, false, arguments);
        }
    }

    public void trace(String msg, Throwable throwable) {
        if (traceFlag) {
            printStackTrace(msg, throwable, false);
        }
    }

    public boolean isDebugEnabled() {
        return debugFlag;
    }

    public void debug(String msg) {
        if (debugFlag) {
            printStackTrace(msg, null, false);
        }
    }

    public void debug(String format, Object... arguments) {
        if (debugFlag) {
            printStackTrace(format, null, false, arguments);
        }
    }

    public void debug(String msg, Throwable throwable) {
        if (debugFlag) {
            printStackTrace(msg, throwable, false);
        }
    }

    public boolean isInfoEnabled() {
        return infoFlag;
    }

    public void info(String msg) {
        if (infoFlag) {
            printStackTrace(msg, null, false);
        }
    }

    public void info(String format, Object... arguments) {
        if (infoFlag) {
            printStackTrace(format, null, false, arguments);
        }
    }

    public void info(String msg, Throwable throwable) {
        if (infoFlag) {
            printStackTrace(msg, throwable, false);
        }
    }

    public boolean isWarnEnabled() {
        return warnFlag;
    }

    public void warn(String msg) {
        if (warnFlag) {
            printStackTrace(msg, null, true);
        }
    }

    public void warn(String format, Object... arguments) {
        if (warnFlag) {
            printStackTrace(format, null, true, arguments);
        }
    }

    public void warn(String msg, Throwable throwable) {
        if (warnFlag) {
            printStackTrace(msg, throwable, true);
        }
    }

    public boolean isErrorEnabled() {
        return errorFlag;
    }

    public void error(String msg) {
        if (errorFlag) {
            printStackTrace(msg, null, true);
        }
    }

    public void error(String format, Object... arguments) {
        if (errorFlag) {
            printStackTrace(format, null, true, arguments);
        }
    }

    public void error(String msg, Throwable throwable) {
        if (errorFlag) {
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
        return StringUtil.format(format, args);
    }

    private void printStackTrace(String format, Throwable throwable, boolean error, Object... arguments) {
        printStackTrace(format(format, arguments), throwable, error);
    }

    private void printStackTrace(String msg, Throwable throwable, boolean error) {
        PrintStream out = error ? System.err : System.out;
        out.print(getMessage(msg));
        if (throwable != null) {
            throwable.printStackTrace(out);
        }
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setName(String name) {
        this.name = name;
    }
}
