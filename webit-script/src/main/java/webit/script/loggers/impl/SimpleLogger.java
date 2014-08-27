// Copyright (c) 2013-2014, Webit Team. All Rights Reserved.
package webit.script.loggers.impl;

import webit.script.loggers.AbstractLogger;
import webit.script.Engine;
import webit.script.Initable;
import webit.script.util.StringUtil;

/**
 *
 * @author zqq90
 */
public final class SimpleLogger extends AbstractLogger implements Initable {

    //settings
    private String level = "info";

    private String prefix;
    private int levelNum;

    public void init(Engine engine) {
        prefix = StringUtil.concat("[", name, "] ");

        String levelString = level.trim().toLowerCase();
        levelNum = "error".equals(levelString) ? LEVEL_ERROR
                : "warn".equals(levelString) ? LEVEL_WARN
                : "info".equals(levelString) ? LEVEL_INFO
                : "debug".equals(levelString) ? LEVEL_DEBUG
                : Integer.MAX_VALUE;
    }

    public boolean isEnabled(int level) {
        return level >= this.levelNum;
    }

    public void log(int level, String msg) {
        printLog(level, msg, null);
    }

    public void log(int level, String msg, Throwable throwable) {
        printLog(level, msg, throwable);
    }

    protected void printLog(int level, String msg, Throwable throwable) {
        if (isEnabled(level)) {
            System.out.println(prefix == null ? msg : prefix.concat(msg != null ? msg : "null"));
            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
