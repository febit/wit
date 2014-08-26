// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers;

/**
 *
 * @author zqq90
 */
public interface Logger {

    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARN = 3;
    public static final int LEVEL_ERROR = 4;
    
    public boolean isEnabled(int level);

    public void log(int level, String msg);

    public void log(int level, String format, Object... args);

    public void log(int level, String msg, Throwable t);
    
    public boolean isDebugEnabled();

    public void debug(String msg);

    public void debug(String format, Object... args);

    public void debug(String msg, Throwable t);

    public boolean isInfoEnabled();

    public void info(String msg);

    public void info(String format, Object... args);

    public void info(String msg, Throwable t);

    public boolean isWarnEnabled();

    public void warn(String msg);

    public void warn(String format, Object... args);

    public void warn(String msg, Throwable t);

    public boolean isErrorEnabled();

    public void error(String msg);

    public void error(String format, Object... args);

    public void error(String msg, Throwable t);
}
