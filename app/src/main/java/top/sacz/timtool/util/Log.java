package top.sacz.timtool.util;

import androidx.annotation.NonNull;

public class Log {
    private static final String TAG = "TimTool";

    public static void v(@NonNull String msg) {
        android.util.Log.v(TAG, msg);
    }

    public static void v(@NonNull String msg, @NonNull Throwable tr) {
        android.util.Log.v(TAG, msg, tr);
    }

    public static void v(@NonNull Throwable tr) {
        android.util.Log.v(TAG, tr.toString(), tr);
    }

    public static void d(@NonNull String msg) {
        android.util.Log.d(TAG, msg);
    }

    public static void d(@NonNull String msg, @NonNull Throwable tr) {
        android.util.Log.d(TAG, msg, tr);
    }

    public static void d(@NonNull Throwable tr) {
        android.util.Log.d(TAG, tr.toString(), tr);
    }

    public static void i(@NonNull String msg) {
        android.util.Log.i(TAG, msg);
    }

    public static void i(@NonNull String msg, @NonNull Throwable tr) {
        android.util.Log.i(TAG, msg, tr);
    }

    public static void i(@NonNull Throwable tr) {
        android.util.Log.i(TAG, tr.toString(), tr);
    }

    public static void w(@NonNull String msg) {
        android.util.Log.w(TAG, msg);
    }

    public static void w(@NonNull String msg, @NonNull Throwable tr) {
        android.util.Log.w(TAG, msg, tr);
    }

    public static void w(@NonNull Throwable tr) {
        android.util.Log.w(TAG, tr.toString(), tr);
    }

    public static void e(@NonNull String msg) {
        android.util.Log.e(TAG, msg);
    }

    public static void e(@NonNull String msg, @NonNull Throwable tr) {
        android.util.Log.e(TAG, msg, tr);
    }

    public static void e(@NonNull Throwable tr) {
        android.util.Log.e(TAG, tr.toString(), tr);
    }

    @NonNull
    public static String getStackTraceString(@NonNull Throwable tr) {
        return android.util.Log.getStackTraceString(tr);
    }
}
