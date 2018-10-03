// Copyright (c) 2013-present, febit.org. All Rights Reserved.
package org.febit.wit.loggers;

/**
 *
 * @author zqq90
 */
public interface Logger {

    boolean isDebugEnabled();

    void debug(String msg);

    void debug(String format, Object... args);

    void debug(String msg, Throwable t);

    boolean isInfoEnabled();

    void info(String msg);

    void info(String format, Object... args);

    void info(String msg, Throwable t);

    boolean isWarnEnabled();

    void warn(String msg);

    void warn(String format, Object... args);

    void warn(String msg, Throwable t);

    boolean isErrorEnabled();

    void error(String msg);

    void error(String format, Object... args);

    void error(String msg, Throwable t);
}
