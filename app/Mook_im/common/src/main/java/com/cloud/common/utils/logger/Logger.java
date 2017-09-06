package com.cloud.common.utils.logger;


import android.util.Log;

import org.slf4j.LoggerFactory;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class Logger {
    private static final String DEFAULT_TAG = "wdcloud";
    //	private static int logLevel = Log.ERROR;
    private static int logLevel = Log.DEBUG;

    private static Logger inst;
    private Lock lock;

    private static Printer printer = null;

    private Logger() {
        lock = new ReentrantLock();
        init(DEFAULT_TAG);
    }

    public static synchronized Logger getLogger() {
        if (inst == null) {
            inst = new Logger();
        }
        return inst;
    }

    /**
     * 初始化日志系统
     * @param tag
     * @return
     */
    public static Settings init(String tag) {
        printer = new LoggerPrinter();
        return printer.init(tag);
    }

    /**
     * 清理日志系统
     */
    public static void clear() {
        printer.clear();
        printer = null;
    }

    private static Printer tLog(String tag) {
        return printer.t(tag, printer.getSettings().getMethodCount());
    }

    private static Printer tLog(int methodCount) {
        return printer.t(null, methodCount);
    }

    private static Printer tLog(String tag, int methodCount) {
        return printer.t(tag, methodCount);
    }

    private static void dLog(String message, StackTraceElement[] sts, Object... args) {
        printer.d(message, sts, args);
    }

    private static void eLog(String message, StackTraceElement[] sts, Object... args) {
        printer.e(null, message, sts, args);
    }

    private static void eLog(Throwable throwable, String message, StackTraceElement[] sts, Object... args) {
        printer.e(throwable, message, sts, args);
    }

    private static void iLog(String message, StackTraceElement[] sts, Object... args) {
        printer.i(message, sts, args);
    }

    private static void vLog(String message, StackTraceElement[] sts, Object... args) {
        printer.v(message, sts, args);
    }

    private static void wLog(String message, StackTraceElement[] sts, Object... args) {
        printer.w(message, sts, args);
    }

    private static void wtfLog(String message, StackTraceElement[] sts, Object... args) {
        printer.wtf(message, sts, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    private static void jsonLog(String json, StackTraceElement[] sts) {
        printer.json(json, sts);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    private static void xmlLog(String xml, StackTraceElement[] sts) {
        printer.xml(xml, sts);
    }



    private String getFunctionName() {
        StackTraceElement[] sts = Thread.currentThread().getStackTrace();

        if (sts == null) {
            return null;
        }

        for (StackTraceElement st : sts) {
            if (st.isNativeMethod()) {
                continue;
            }

            if (st.getClassName().equals(Thread.class.getName())) {
                continue;
            }

            if (st.getClassName().equals(this.getClass().getName())) {
                continue;
            }

            return "[" + st.getFileName() + ":" + st.getLineNumber() + "]";
        }

        return null;
    }

    private String createMessage(String msg) {
        return msg;
    }

    /**
     * log.i
     */
    public void i(String message) {
        if (logLevel <= Log.INFO) {
            lock.lock();
            try {
                LoggerFactory.getLogger(DEFAULT_TAG).info(message);
                iLog(message, Thread.currentThread().getStackTrace());
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * log.v
     */
    public void v(String message) {
        if (logLevel <= Log.VERBOSE) {
            lock.lock();
            try {
                vLog(message, Thread.currentThread().getStackTrace());
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * log.d    <br>
     * 样例：<br>
     * Logger.getLogger().d("log");
     */
    public void d(String message) {
        if (logLevel <= Log.DEBUG) {
            lock.lock();
            try {
                LoggerFactory.getLogger(DEFAULT_TAG).debug(message);
                dLog(message, Thread.currentThread().getStackTrace());
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * log.e    <br>
     * 样例：<br>
     * Logger.getLogger().e("log");
     */
    public void e(String message) {
        if (logLevel <= Log.ERROR) {
            lock.lock();
            try {
                LoggerFactory.getLogger(DEFAULT_TAG).error(message);
                eLog(message, Thread.currentThread().getStackTrace());
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    private String getInputString(String format, Object... args) {
        if (format == null) {
            return "null log format";
        }

        return String.format(format, args);
    }

    /**
     * log.error
     */
    private void error(Exception e) {
        if (logLevel <= Log.ERROR) {
            StringBuffer sb = new StringBuffer();
            lock.lock();
            try {
                String name = getFunctionName();
                StackTraceElement[] sts = e.getStackTrace();

                if (name != null) {
                    sb.append(name + " - " + e + "\r\n");
                } else {
                    sb.append(e + "\r\n");
                }
                if (sts != null && sts.length > 0) {
                    for (StackTraceElement st : sts) {
                        if (st != null) {
                            sb.append("[ " + st.getFileName() + ":"
                                    + st.getLineNumber() + " ]\r\n");
                        }
                    }
                }
                LoggerFactory.getLogger(DEFAULT_TAG).error(sb.toString());
                eLog(sb.toString(), Thread.currentThread().getStackTrace());
            } finally {
                lock.unlock();
            }
        }
    }
    /**
     * log.d
     */
    private void w(String message) {
        if (logLevel <= Log.WARN) {
            lock.lock();
            try {
                LoggerFactory.getLogger(DEFAULT_TAG).warn(message);
                wLog(message, Thread.currentThread().getStackTrace());
            } finally {
                lock.unlock();
            }
        }
    }

}
