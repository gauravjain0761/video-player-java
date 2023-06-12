package com.app.mvpdemo.util;

import android.text.TextUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间操作工具类
 *
 * @author weiwen
 * @created 2022/2/16 5:01 PM
 */
public class DataUtils {

    private static final String TAG = "DataUtils";

    /**
     * 判断是否同一天
     *
     * @param time1 毫秒级时间戳
     * @param time2 毫秒级时间戳
     * @return
     */
    public static boolean isCommonDay(long time1, long time2) {
        String format = "yyyy-MM-dd";
        String format1 = convertDateTimeToYMD(time1, format);
        String format2 = convertDateTimeToYMD(time2, format);
        return TextUtils.equals(format1, format2);
    }

    /**
     * 判断是否今天
     *
     * @param time 毫秒级时间戳
     * @return
     */
    public static boolean isToday(long time) {
        return isCommonDay(time, System.currentTimeMillis());
    }

    /**
     * 判断是否昨天
     *
     * @param time 毫秒级时间戳
     * @return
     */
    public static boolean isYesterday(long time) {
        return isCommonDay(time, System.currentTimeMillis() - 24 * 60 * 60 * 1000);
    }

    /**
     * 判断date和当前日期是否在同一周内
     *
     * @param date
     * @return
     */
    public static boolean isSameWeekWithToday(Date date) {

        if (date == null) {
            return false;
        }

        // 0.先把Date类型的对象转换Calendar类型的对象
        Calendar todayCal = Calendar.getInstance();
        Calendar dateCal = Calendar.getInstance();

        todayCal.setTime(new Date());
        dateCal.setTime(date);

        // 1.比较当前日期在年份中的周数是否相同
        if (todayCal.get(Calendar.WEEK_OF_YEAR) == dateCal.get(Calendar.WEEK_OF_YEAR)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 同一年
     *
     * @param date1
     * @return
     */
    public static Boolean isSameYear(Date date1) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        Date date2 = new Date();
        cal2.setTime(date2);
        boolean isSameYear = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
        return isSameYear;
    }

    /**
     * @param lo
     * @return
     */
    public static String longToDialogDate(long lo) {
        Date data = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("KK:mm a");
        return sd.format(data);
    }


    /**
     * 日期转变成星期
     *
     * @param datetime 日期
     * @return
     */

    public static String dateToWeek(String datetime, String[] weekdays) {
        SimpleDateFormat f = new SimpleDateFormat("MM-dd,yyyy");
        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = f.parse(datetime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK)-1;
        if (w < 0)
            w = 0;
        return weekdays[w];
    }

    /**
     * @param lo
     * @return
     */
    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd,yyyy");
        return sd.format(date);
    }

    /**
     * @param lo
     * @return
     */
    public static String longToThisYearDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("MM-dd");
        return sd.format(date);
    }

    /**
     * @param time   秒级时间
     * @param format
     * @return
     */
    public static String convertSecondTime(long time, String format) {
        SimpleDateFormat sim = new SimpleDateFormat(format, Locale.getDefault());
        return sim.format(time * 1000);

    }

    public static String convertTimeToYM(String time, String format) {
        SimpleDateFormat sim = new SimpleDateFormat(format, Locale.getDefault());
        String ym = null;
        try {
            Date d = sim.parse(time);
            ym = sim.format(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ym;

    }

    public static String convertTimeToYMD(String time, String format) {
        SimpleDateFormat sim = new SimpleDateFormat(format, Locale.getDefault());
        SimpleDateFormat simYMD = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String ymd = null;
        try {
            Date d = sim.parse(time);
            ymd = simYMD.format(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ymd;

    }

    public static String convertDateTimeToYM(String time, String format) {
        SimpleDateFormat sim = new SimpleDateFormat(format, Locale.getDefault());
        String ym = null;
        try {
            Date d = sim.parse(time);
            SimpleDateFormat sim1 = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
            ym = sim1.format(d);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ym;

    }

    /**
     * @param time   毫秒级时间戳
     * @param format
     * @return
     */
    public static String convertDateTimeToYMD(long time, String format) {
        String ym = null;
        Date d = new Date(time);
        SimpleDateFormat sim1 = new SimpleDateFormat(format, Locale.getDefault());
        ym = sim1.format(d);
        return ym;

    }

    /**
     * 年月日转换成秒
     *
     * @param time
     * @param format
     * @return
     */
    public static long convertYMDToSeconds(String time, String format) {
        if (time == null || time.trim().equals(""))
            return 0;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(time);
            return (long) (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String convertDateTimeToHm(String time) {
        SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String hm = null;
        SimpleDateFormat sim1 = new SimpleDateFormat("HH:mm");
        try {
            Date d1 = sim.parse(time);
            hm = sim1.format(d1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return hm;

    }

    public static String convertTimeToChineseMonth(String ym) {
        String monthString = ym.substring(5);
        if ((Integer.parseInt(monthString) < 10)) {
            monthString = ym.substring(6);
        } else {
            monthString = ym.substring(5);
        }
        return monthString;
    }

    /**
     * @param str YYYY-MM-DD HH:mm:ss string
     * @return 今天, 昨天, 前天, n天前
     */
    public static String formatSomeDay(String str) {
        return formatSomeDay(parseCalendar(str));
    }

    /**
     * YYYY-MM-DD HH:mm:ss格式的时间字符串转换为{@link Calendar}类型
     *
     * @param str YYYY-MM-DD HH:mm:ss格式字符串
     * @return {@link Calendar}
     */
    public static Calendar parseCalendar(String str) {
        Matcher matcher = UniversalDatePattern.matcher(str);
        Calendar calendar = Calendar.getInstance();
        if (!matcher.find()) return null;
        calendar.set(
                matcher.group(1) == null ? 0 : toInt(matcher.group(1)),
                matcher.group(2) == null ? 0 : toInt(matcher.group(2)) - 1,
                matcher.group(3) == null ? 0 : toInt(matcher.group(3)),
                matcher.group(4) == null ? 0 : toInt(matcher.group(4)),
                matcher.group(5) == null ? 0 : toInt(matcher.group(5)),
                matcher.group(6) == null ? 0 : toInt(matcher.group(6))
        );
        return calendar;
    }

    private static final Pattern UniversalDatePattern = Pattern.compile(
            "([0-9]{4})-([0-9]{2})-([0-9]{2})[\\s]+([0-9]{2}):([0-9]{2}):([0-9]{2})"
    );

    /**
     * format 例如：YYYY-MM-DD HH:mm:ss格式的时间字符串转换为{@link Calendar}类型
     *
     * @param str
     * @param format
     * @return
     */
    public static Calendar parseCalendar(String str, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(date);
        return calendar;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * @param time 秒级时间戳
     * @return 今天, 昨天, 前天, n天前
     */
    public static String formatSomeDay(long time) {
        String str = convertSecondTime(time, "yyyy-MM-dd HH:mm:ss");
        return formatSomeDay(parseCalendar(str));
    }

    /**
     * @param calendar {@link Calendar}
     * @return 今天, 昨天,
     */
    public static String formatSomeDay(Calendar calendar) {
        if (calendar == null) return "?天前";
        Calendar mCurrentDate = Calendar.getInstance();
        int currentYear = new Date().getYear();
        long crim = mCurrentDate.getTimeInMillis(); // current
        long trim = calendar.getTimeInMillis(); // target
        long diff = crim - trim;

        int year = mCurrentDate.get(Calendar.YEAR);
        int month = mCurrentDate.get(Calendar.MONTH);
        int day = mCurrentDate.get(Calendar.DATE);

        int year1 = calendar.get(Calendar.YEAR);
        int month1 = calendar.get(Calendar.MONTH);
        int day1 = calendar.get(Calendar.DATE);

        mCurrentDate.set(year, month, day, 0, 0, 0);
        if (trim >= mCurrentDate.getTimeInMillis()) {
            return "今天 " + (month1 + 1) + "." + day1;
        }
        mCurrentDate.set(year, month, day - 1, 0, 0, 0);
        if (trim >= mCurrentDate.getTimeInMillis()) {
            return "昨天 " + (month1 + 1) + "." + day1;
        }
        if (year1 == currentYear) {
            return (month1 + 1) + "." + day1;
        } else {
            return year1 + "." + (month1 + 1) + "." + day1;
        }
    }
    /**
     * @param calendar {@link Calendar}
     * @return 今天, 昨天, 前天, n天前
     */
/*    public static String formatSomeDay(Calendar calendar) {
        if (calendar == null) return "?天前";
        Calendar mCurrentDate = Calendar.getInstance();
        long crim = mCurrentDate.getTimeInMillis(); // current
        long trim = calendar.getTimeInMillis(); // target
        long diff = crim - trim;

        int year = mCurrentDate.get(Calendar.YEAR);
        int month = mCurrentDate.get(Calendar.MONTH);
        int day = mCurrentDate.get(Calendar.DATE);

        mCurrentDate.set(year, month, day, 0, 0, 0);
        if (trim >= mCurrentDate.getTimeInMillis()) {
            return "今天";
        }
        mCurrentDate.set(year, month, day - 1, 0, 0, 0);
        if (trim >= mCurrentDate.getTimeInMillis()) {
            return "昨天";
        }
        mCurrentDate.set(year, month, day - 2, 0, 0, 0);
        if (trim >= mCurrentDate.getTimeInMillis()) {
            return "前天";
        }
        return String.format("%s天前", diff / AlarmManager.INTERVAL_DAY);
    }*/


    /**
     * 秒转换成时分秒
     *
     * @param time
     * @return
     */
    public static String timeToHms(int time) {
        int hour = 0;
        int minutes = 0;
        int sencond = 0;
        int temp = time % 3600;
        if (time > 3600) {
            hour = time / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    minutes = temp / 60;
                    if (temp % 60 != 0) {
                        sencond = temp % 60;
                    }
                } else {
                    sencond = temp;
                }
            }
        } else {
            minutes = time / 60;
            if (time % 60 != 0) {
                sencond = time % 60;
            }
        }
        return (hour < 10 ? ("0" + hour) : hour) + ":" + (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (sencond < 10 ? ("0" + sencond) : sencond);
    }

    /**
     * 毫秒转换成时分秒
     *
     * @param millSeconds 毫秒
     * @return
     */
    public static String millsToHms2(long millSeconds) {
        int hours = (int) (millSeconds / 1000 / 60 / 60);
        int minis = (int) (millSeconds / 1000 / 60 % 60);
        int seconds = (int) (millSeconds / 1000 % 60);
        int mills = (int) (millSeconds % 1000 / 10);
        String minAndSecond = (minis > 9 ? minis : ("0" + minis)) + ":" +
                (seconds > 9 ? seconds : ("0" + seconds)) + "." + (mills > 9 ? mills : ("0" + mills));
        if (hours > 0) {
            return (hours > 9 ? hours : ("0" + hours)) + ":" + (minis > 9 ? minis : ("0" + minis)) + ":" +
                    (seconds > 9 ? seconds : ("0" + seconds));
        } else {
            return minAndSecond;
        }
    }

    /**
     * 毫秒转换成时分秒
     *
     * @param millSeconds 毫秒
     * @return
     */
    public static String millsToHms(long millSeconds) {
        int hours = (int) (millSeconds / 1000 / 60 / 60);
        int minis = (int) (millSeconds / 1000 / 60 % 60);
        int seconds = (int) (millSeconds / 1000 % 60);
        String minAndSecond = (minis > 9 ? minis : ("0" + minis)) + ":" +
                (seconds > 9 ? seconds : ("0" + seconds));
        if (hours > 0) {
            return (hours > 9 ? hours : ("0" + hours)) + ":" + minAndSecond;
        } else {
            return minAndSecond;
        }
    }
    /**
     * 毫秒转换成分秒毫秒
     *
     * @param millSeconds 毫秒
     * @return
     */
//    public static String millsTomsms(long millSeconds) {
//        int minis = (int) (millSeconds / 1000 / 60);
//        int seconds = (int) (millSeconds / 1000 % 60);
//        int mills = (int) (millSeconds / 100);
//        String minAndSecond = (minis > 9 ? minis : ("0" + minis)) + ":" +
//                (seconds > 9 ? seconds : ("0" + seconds));
//        if (hours > 0) {
//            return (hours > 9 ? hours : ("0" + hours)) + ":" + minAndSecond;
//        } else {
//            return minAndSecond;
//        }
//    }

    /**
     * 秒转换成时分秒
     *
     * @param time
     * @return
     */
    public static String timeToMs(int time) {
        int minutes = time / 60;
        int sencond = 0;
        if (time % 60 != 0) {
            sencond = time % 60;
        }
        return (minutes < 10 ? ("0" + minutes) : minutes) + ":" + (sencond < 10 ? ("0" + sencond) : sencond);
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return defValue;
    }

    /**
     * short[] 转 byte[]
     */
    public static byte[] toBytes(short[] src) {
        int count = src.length;
        byte[] dest = new byte[count << 1];
        for (int i = 0; i < count; i++) {
            dest[i * 2] = (byte) (src[i]);
            dest[i * 2 + 1] = (byte) (src[i] >> 8);
        }

        return dest;
    }


    /**
     * short[] 转 byte[]
     */
    public static byte[] toBytes(short src) {
        byte[] dest = new byte[2];
        dest[0] = (byte) (src);
        dest[1] = (byte) (src >> 8);

        return dest;
    }

    /**
     * int 转 byte[]
     */
    public static byte[] toBytes(int i) {
        byte[] b = new byte[4];
        b[0] = (byte) (i & 0xff);
        b[1] = (byte) ((i >> 8) & 0xff);
        b[2] = (byte) ((i >> 16) & 0xff);
        b[3] = (byte) ((i >> 24) & 0xff);
        return b;
    }


    /**
     * String 转 byte[]
     */
    public static byte[] toBytes(String str) {
        return str.getBytes();
    }

    /**
     * long类型转成byte数组
     */
    public static byte[] toBytes(long number) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, number);
        return buffer.array();
    }

    public static int toInt(byte[] src, int offset) {
        return ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
    }

    public static int toInt(byte[] src) {
        return toInt(src, 0);
    }

    /**
     * 字节数组到long的转换.
     */
    public static long toLong(byte[] b) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(b, 0, b.length);
        return buffer.getLong();
    }

    /**
     * byte[] 转 short[]
     * short： 2字节
     */
    public static short[] toShorts(byte[] src) {
        int count = src.length >> 1;
        short[] dest = new short[count];
        for (int i = 0; i < count; i++) {
            dest[i] = (short) ((src[i * 2] & 0xff) | ((src[2 * i + 1] & 0xff) << 8));
        }
        return dest;
    }

    public static byte[] merger(byte[] bt1, byte[] bt2) {
        byte[] bt3 = new byte[bt1.length + bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

    public static String toString(byte[] b) {
        return Arrays.toString(b);
    }
}
