package cn.edu.zju.qcw.android.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by SQ on 2017/5/20.
 */

public class DateUtil {
    public static String formatDateString(String format, String dateStr) {
        try {
            return new SimpleDateFormat(format).format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String date2String(String toFormat, Date date) {
        return new SimpleDateFormat(toFormat).format(date);
    }

    public static Date string2Date(String format, String dateStr) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
