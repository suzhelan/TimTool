package top.sacz.timtool.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    /**
     * 格式化时间
     */
    public static String formatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }

    /**
     * 格式化时间
     */
    public static String formatTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        return format.format(new Date(time));
    }

    /**
     * 格式化时间
     */
    public static String formatTime(long time, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(time);
    }
}
