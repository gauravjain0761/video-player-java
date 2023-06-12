package com.app.mvpdemo.util.log;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Log工具类:<br>
 * <pre>
 * // 参数会按顺序自动组装(支持v,d,i,w,e,f六种级别)
 * LogUtil.d(TAG, "obj1=", obj1, ", obj2=", obj2);
 * // 打印堆栈信息
 * LogUtil.d(TAG, e, "obj1=", obj1, ", obj2=", obj2);
 *
 * // 保存log到文件
 * LogUtil.f(TAG, msg1, msg2);
 *
 * </pre>
 * 编译版本时:<ul>
 * <li>需要填写APP_VERSION,以便在log中反映出来应用版本号
 * <li>在project.properties中配置assumenosideeffects,使不需要的log失效</ul>
 * 写文件log:<ul>
 * <li>不同进程会写到不同的文件夹中,文件夹用进程名命名
 * <li>同一进程写文件log的操作在同一个线程中执行
 * <li>log文件名为log.2, log.1, log.0等等,最新的log文件数字标号最小</ul>
 */
public final class LogUtil {
    public static final String APP_VERSION = "";

    private static final String LOGPRE_SPLIT = "|";
    private static final String LOGPRE_APPVER = APP_VERSION + LOGPRE_SPLIT;
    private static final String LOGPRE_TIME_FORMAT = "yyyyMMdd-HH:mm:ss:SSS" + LOGPRE_SPLIT;

    private static final int LOGCAT_MESSAGE_MAX_LENTH = 4000;

    private static final int MAX_LOGFILE_LENGTH = 3 * 1024 * 1024;//3M
    private static final int MAX_LOGFILE_NUMBER = 6;
    private static String LOGFILE_PATH = null;
    private static final String FILECONTENT_ENCODE = "UTF-8";

    private static final String TAG = "LogUtil";
    private static final String LOG_PATH = "/benefm/";

    private static final long LOGFILE_BUFFER_MAX = 128 * 1024; // 128k
    private static final long LOGFILE_DELAY = 6 * 1000; // 6sec
    private static final long LOGFILE_THREAD_EXIT_DELAY = 5 * 60 * 1000;// 5min

    private static final int MSG_SAVE_LOG_FILE = 1;
    private static final int MSG_LOG_FILE_THREAD_EXIT = 2;

    private static StringBuilder logFileBuffer;
    private static Handler logFileHandler;

    private LogUtil() {
    }

    /**
     * 打印verbose级别的log
     *
     * @param tag
     * @param msg
     */
    public static void v(String tag, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.v(tag, text);
            } else {
                do {
                    Log.v(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.v(tag, text);
                }
            }
        }
    }

    /**
     * 打印verbose级别的log，并打印堆栈
     *
     * @param tag
     * @param t
     * @param msg
     */
    public static void v(String tag, Throwable t, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.v(tag, text, t);
            } else {
                do {
                    Log.v(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.v(tag, text);
                }
                Log.v(tag, "", t);
            }
        }
    }

    /**
     * 打印debug级别的log
     *
     * @param tag
     * @param msg
     */
    public static void d(String tag, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.i(tag, text);
            } else {
                do {
                    Log.i(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.i(tag, text);
                }
            }
        }
    }

    /**
     * 打印debug级别的log，并打印堆栈
     *
     * @param tag
     * @param t
     * @param msg
     */
    public static void d(String tag, Throwable t, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.i(tag, text, t);
            } else {
                do {
                    Log.i(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.i(tag, text);
                }
                Log.i(tag, "", t);
            }
        }
    }

    /**
     * 打印info级别的log
     *
     * @param tag
     * @param msg
     */
    public static void i(String tag, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.i(tag, text);
            } else {
                do {
                    Log.i(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.i(tag, text);
                }
            }
        }
    }

    /**
     * 打印info级别的log，并打印堆栈
     *
     * @param tag
     * @param t
     * @param msg
     */
    public static void i(String tag, Throwable t, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.i(tag, text, t);
            } else {
                do {
                    Log.i(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.i(tag, text);
                }
                Log.i(tag, "", t);
            }
        }
    }

    /**
     * 打印warn级别的log
     *
     * @param tag
     * @param msg
     */
    public static void w(String tag, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.w(tag, text);
            } else {
                do {
                    Log.w(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.w(tag, text);
                }
            }
        }
    }

    /**
     * 打印warn级别的log，并打印堆栈
     *
     * @param tag
     * @param t
     * @param msg
     */
    public static void w(String tag, Throwable t, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.w(tag, text, t);
            } else {
                do {
                    Log.w(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.w(tag, text);
                }
                Log.w(tag, "", t);
            }
        }
    }

    /**
     * 打印error级别的log
     *
     * @param tag
     * @param msg
     */
    public static void e(String tag, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.e(tag, text);
            } else {
                do {
                    Log.e(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.e(tag, text);
                }
            }
        }
    }

    /**
     * 打印error级别的log，并打印堆栈
     *
     * @param tag
     * @param t
     * @param msg
     */
    public static void e(String tag, Throwable t, Object... msg) {
        String text = buildMessage(msg, LOGPRE_APPVER);
        if (text != null) {
            int len = text.length();
            if (len <= LOGCAT_MESSAGE_MAX_LENTH) {
                Log.e(tag, text, t);
            } else {
                do {
                    Log.e(tag, text.substring(0, LOGCAT_MESSAGE_MAX_LENTH));
                    text = text.substring(LOGCAT_MESSAGE_MAX_LENTH);
                    len = text.length();
                } while (len > LOGCAT_MESSAGE_MAX_LENTH);
                if (len > 0) {
                    Log.e(tag, text);
                }
                Log.e(tag, "", t);
            }
        }
    }

    /**
     * 保存log到文件
     *
     * @param tag
     * @param msg
     */
    public static void f(String tag, Object... msg) {
        String text = buildMessage(msg, getTimePrefix(), tag + LOGPRE_SPLIT, LOGPRE_APPVER);
        if (text != null) {
            putLogFileBuffer(text);
        }
    }

    /**
     * 保存log到文件, for debug version
     *
     * @param tag
     * @param msg
     */
    public static void f_d(String tag, Object... msg) {
        f(tag, msg);
    }

    /**
     * 保存log和堆栈信息到文件
     *
     * @param tag
     * @param t
     * @param msg
     */
    public static void f(String tag, Throwable t, Object... msg) {
        String text = buildMessage(msg, getTimePrefix(), tag + LOGPRE_SPLIT, LOGPRE_APPVER);
        if (text != null) {
            putLogFileBuffer(text);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            try {
                pw.close();
                sw.close();
            } catch (IOException e) {
                w(TAG, "print stace trace failed");
            }
            putLogFileBuffer(sw.toString());
        }
    }

    /**
     * 保存log和堆栈信息到文件, for debug version
     *
     * @param tag
     * @param t
     * @param msg
     */
    public static void f_d(String tag, Throwable t, Object... msg) {
        f(tag, t, msg);
    }

    private static String getTimePrefix() {
        SimpleDateFormat df = new SimpleDateFormat(LOGPRE_TIME_FORMAT, Locale.ENGLISH);
        return df.format(System.currentTimeMillis());
    }

    private static String buildMessage(Object[] msg, String... prefix) {
        try {
            if (msg == null || msg.length == 0) {
                return null;
            }

            StringBuilder sb = new StringBuilder();
            if (prefix != null) {
                for (String p : prefix) {
                    sb.append(p);
                }
            }
            for (Object m : msg) {
                sb.append(m);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error--->>" + e.getMessage());
            return null;
        }
    }

    /**
     * 向文件中写文本内容
     *
     * @param file
     * @param content
     * @param append
     */
    public static void writeFile(File file, String content, boolean append) {
        byte[] bytes = null;
        try {
            bytes = content.getBytes(FILECONTENT_ENCODE);
        } catch (UnsupportedEncodingException e) {
            w(TAG, "write file failed: ", e.getMessage());
        }
        if (bytes != null) {
            writeFile(file, bytes, append);
        }
    }

    /**
     * 向文件中写二进制内容
     *
     * @param file
     * @param content
     * @param append
     */
    public static void writeFile(File file, byte[] content, boolean append) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, append);
            out.write(content);
            out.flush();
        } catch (IOException e) {
            w(TAG, "write file failed: ", e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static File getLogFile() {
        if (LOGFILE_PATH == null) {
            String pname = getProcessName();
            if (pname == null) {
                pname = "health";
            } else {
                // replace characters not supported by Windows folder name
                pname = pname.replaceAll(":", "_");
            }
            String sdcard = null;
            try {
                sdcard = Environment.getExternalStorageDirectory().getCanonicalPath();
                LOGFILE_PATH = sdcard + LOG_PATH + pname + "/";
                File path = new File(LOGFILE_PATH);
                if (!path.exists()) {
                    if (!path.mkdirs()) {
                        w(TAG, "create log directory failed");
                        w(TAG, "path : ", path.getAbsolutePath());
                        return null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        File file = new File(LOGFILE_PATH + "log.0");
        if (file.exists() && file.length() > MAX_LOGFILE_LENGTH) {
            File tmp = new File(LOGFILE_PATH + "log." + (MAX_LOGFILE_NUMBER - 1));
            if (tmp.exists()) {
                if (!tmp.delete()) {
                    w(TAG, "delete log file failed");
                    return null;
                }
            }
            for (int i = MAX_LOGFILE_NUMBER - 2; i >= 0; i--) {
                tmp = new File(LOGFILE_PATH + "log." + i);
                if (tmp.exists()) {
                    if (!tmp.renameTo(new File(LOGFILE_PATH + "log." + (i + 1)))) {
                        w(TAG, "rename log file failed");
                        return null;
                    }
                }
            }
        }
        return file;
    }

    /**
     * @return 当前进程名
     */
    public static String getProcessName() {
        String pname = getProcessNameProc();
        if (pname == null) {
            // pname = getProcessNamePs();
        }
        return pname;
    }

    /**
     * <li>cat /proc/pid/cmdline
     * <li>ps pid | busybox tail -n 1 | busybox awk '{print $NF}'
     *
     * @return
     */
    private static String getProcessNameProc() {
        String cmd = "/proc/" + android.os.Process.myPid() + "/cmdline";
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(cmd),
                    "iso-8859-1"));
            int c;
            StringBuilder pname = new StringBuilder();
            while ((c = input.read()) > 0) {
                pname.append((char) c);
            }
            return pname.toString();
        } catch (IOException e) {
            d(TAG, "get process name (proc) failed: ", e.getMessage());
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static void putLogFileBuffer(String text) {
        synchronized (LogUtil.class) {
            if (logFileBuffer == null) {
                logFileBuffer = new StringBuilder(text);
            } else {
                logFileBuffer.append(text);
            }
            logFileBuffer.append("\n");

            boolean saveNow = logFileBuffer.length() >= LOGFILE_BUFFER_MAX;
            // schedule log file buffer saving
            if (logFileHandler == null) {
                HandlerThread thread = new HandlerThread("logfile_thread");
                thread.start();
                logFileHandler = new LogFileHandler(thread.getLooper());
                logFileHandler.sendEmptyMessageDelayed(MSG_SAVE_LOG_FILE, saveNow ? 0 :
                        LOGFILE_DELAY);
            } else {
                logFileHandler.removeMessages(MSG_LOG_FILE_THREAD_EXIT);
                if (saveNow) {
                    logFileHandler.removeMessages(MSG_SAVE_LOG_FILE);
                    logFileHandler.sendEmptyMessage(MSG_SAVE_LOG_FILE);
                } else if (!logFileHandler.hasMessages(MSG_SAVE_LOG_FILE)) {
                    logFileHandler.sendEmptyMessageDelayed(MSG_SAVE_LOG_FILE, LOGFILE_DELAY);
                }
            }
        }
    }

    private static void flushLogFileBufferLocked() {
        synchronized (LogUtil.class) {
            if (logFileBuffer == null) {
                return;
            }

            String text = logFileBuffer.toString();
            logFileBuffer = null;
            File file = getLogFile();
            if (file != null) {
                writeFile(file, text, true);
            } else {
                w(TAG, "get log file failed.");
            }
        }
    }

    private static class LogFileHandler extends Handler {

        public LogFileHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAVE_LOG_FILE:
                    synchronized (LogUtil.class) {
                        logFileHandler.removeMessages(MSG_SAVE_LOG_FILE);
                        flushLogFileBufferLocked();
                        logFileHandler.sendEmptyMessageDelayed(MSG_LOG_FILE_THREAD_EXIT,
                                LOGFILE_THREAD_EXIT_DELAY);
                    }
                    break;
                case MSG_LOG_FILE_THREAD_EXIT:
                    synchronized (LogUtil.class) {
                        if (!logFileHandler.hasMessages(MSG_SAVE_LOG_FILE)) {
                            logFileHandler.getLooper().quit();
                            logFileHandler = null;
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static String getHttpHandlerTag() {
        return "HttpHandler";
    }
}
