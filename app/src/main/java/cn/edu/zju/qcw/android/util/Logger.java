package cn.edu.zju.qcw.android.util;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import cn.edu.zju.qcw.android.PapicApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This is the class to do print log of all levels.
 *
 * Author: Shper
 * Version: V0.1 2017/3/8
 */
public class Logger {

    /** This is a log tag and it also contains the source of a log message. */
    private static String TAG = "ZJUPAPIC - %1$s.%2$s(L:%3$d)";
    private static List<String> ignoreClassList = new ArrayList<>();

    /**
     * Private constructor, avoid this class wall be instantiated.
     */
    private Logger() {
    }

    public static void initialize(String tag) {
        if (TextUtils.isEmpty(tag)) {
            return;
        }
        TAG = tag + " - %1$s.%2$s(L:%3$d)";
    }

    public static void addIgnoreClazz(@NonNull String clazzName) {
        if (TextUtils.isEmpty(clazzName)) {
            return;
        }

        ignoreClassList.add(clazzName);
    }

    /**
     * Send a verbose log message.
     *
     * @param messages These messages you would like logged.
     */
    public static void v(String... messages) {
        String message = concatMessage(messages);
        int maxLogChars = 4096 / 3 - 1;
        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.v(generateTag(), message.substring(i, end).trim());
        }
    }

    /**
     * Send a debug log message.
     *
     * @param messages These messages you would like logged.
     */
    public static void d(String... messages) {
        if (!PapicApp.isDebug() || isIgnore()) {
            return;
        }

        String message = concatMessage(messages);
        int maxLogChars = 4096 / 3 - 1;
        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.d(generateTag(), message.substring(i, end).trim());
        }
    }

    /**
     * Send a info log message.
     *
     * @param messages These messages you would like logged.
     */
    public static void i(String... messages) {
        String message = concatMessage(messages);
        int maxLogChars = 4096 / 3 - 1;
        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.i(generateTag(), message.substring(i, end).trim());
        }
    }

    /**
     * Send a warning log message.
     *
     * @param messages These messages you would like logged.
     */
    public static void w(String... messages) {
        String message = concatMessage(messages);
        int maxLogChars = 4096 / 3 - 1;
        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.w(generateTag(), message.substring(i, end).trim());
        }
    }

    /**
     * Send a error log message.
     *
     * @param messages These messages you would like logged.
     */
    public static void e(String... messages) {
        String message = concatMessage(messages);
        int maxLogChars = 4096 / 3 - 1;
        for (int i = 0; i < message.length(); i += maxLogChars) {
            int end = i + maxLogChars;
            if (end > message.length()) {
                end = message.length();
            }

            Log.e(generateTag(), message.substring(i, end).trim());
        }
    }

    /**
     * Returns the log tag,
     * the tag contains the caller class name, method name, and source line number.
     */
    private static String generateTag() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        return String.format(Locale.getDefault(),
                TAG, callerClazzName, caller.getMethodName(), caller.getLineNumber());
    }

    private static boolean isIgnore() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[4];
        String callerClazzName = caller.getClassName();
        return ignoreClassList.contains(callerClazzName);
    }

    /**
     * This method is used to concat all of the log message.
     *
     * @param messages These messages you would like logged.
     *
     * @return The string representation of the data in this messages.
     */
    private static String concatMessage(String... messages) {
        if (null == messages || messages.length < 1) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message);
        }
        return sb.toString();
    }

}
