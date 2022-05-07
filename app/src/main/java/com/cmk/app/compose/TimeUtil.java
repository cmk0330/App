package com.cmk.app.compose;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeUtil {

    /**
     * @return 格式化后的当前时间
     */
    public static String getCurrTime() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String time = df.format(new Date(System.currentTimeMillis()));
        System.out.println("时间戳-->" + System.currentTimeMillis());
        return df.format(new Date(System.currentTimeMillis()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void test() {
        List<Double> doubles = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            doubles.add(i + 0.23);
        }
        Double[] doubles1 = doubles.toArray(new Double[0]);
        System.out.println(doubles1);
    }

    /**
     * @param format 传入指定格式
     */
    public static String getCurrTime(TimeFormat format) {
        SimpleDateFormat df = null;
        switch (format) {
            case YYYY:
                df = new SimpleDateFormat("yyyy");
                break;
            case MM:
                df = new SimpleDateFormat("MM");
                break;
            case DD:
                df = new SimpleDateFormat("DD");
                break;
            case HH:
                df = new SimpleDateFormat("HH");
                break;
            case mm:
                df = new SimpleDateFormat("mm");
                break;
            case ss:
                df = new SimpleDateFormat("ss");
                break;
        }
        return df.format(new Date(System.currentTimeMillis()));
    }


    /**
     * 使用Calendar 可不用封装直接用
     */
    public static void getCurrDate() {
        Calendar cal = Calendar.getInstance();
        // 赋值时年月日时分秒常用的6个值，注意月份下标从0开始，所以取月份要+1
        System.out.println("年:" + cal.get(Calendar.YEAR));
        System.out.println("月:" + (cal.get(Calendar.MONTH) + 1));
        System.out.println("日:" + cal.get(Calendar.DAY_OF_MONTH));
        System.out.println("时:" + cal.get(Calendar.HOUR_OF_DAY));
        System.out.println("分:" + cal.get(Calendar.MINUTE));
        System.out.println("秒:" + cal.get(Calendar.SECOND));
        int time = cal.get(Calendar.SECOND) + cal.get(Calendar.MINUTE) * 60 + cal.get(Calendar.HOUR_OF_DAY) * 3600;
        System.out.println("时分秒时间戳----》" + time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        Date date = new Date(7200);
        String res = simpleDateFormat.format(date);
        String s = secToTime(7200);
        String s1 = msecToTime(time);
        System.out.println("时间戳转时间1----》" + res);
        System.out.println("时间戳转时间2----》" + s);
        System.out.println("时间戳转代号秒----》" + s1);
    }

    public static String secToTime(int seconds) {
        int hour = seconds / 3600;
        int minute = (seconds - hour * 3600) / 60;
        int second = (seconds - hour * 3600 - minute * 60);

        StringBuffer sb = new StringBuffer();
        if (hour >= 0) {
            if (hour < 10)
                sb.append("0").append(hour).append(":");
            else
                sb.append(hour).append(":");
        }
        if (minute >= 0) {
            if (minute < 10)
                sb.append("0").append(minute).append(":");
            else
                sb.append(minute).append(":");
        }
        if (second >= 0) {
            if (second < 10)
                sb.append("0").append(second);
            else
                sb.append(second);
        }
//        if (second == 0) {
//            sb.append("<1秒");
//        }
        return sb.toString();
    }

    public static String msecToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        int millisecond = 0;
        if (time <= 0)
            return "00:00:00.000";
        else {
            second = time / 1000;
            minute = second / 60;
            millisecond = time % 1000;
            if (second < 60) {
                timeStr = "00:00:" + unitFormat(second) + "." + unitFormat2(millisecond);
            } else if (minute < 60) {
                second = second % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second) + "." + unitFormat2(millisecond);
            } else {// 数字>=3600 000的时候
                hour = minute / 60;
                minute = minute % 60;
                second = second - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second) + "."
                        + unitFormat2(millisecond);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {// 时分秒的格式转换
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String unitFormat2(int i) {// 毫秒的格式转换
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "00" + Integer.toString(i);
        else if (i >= 10 && i < 100) {
            retStr = "0" + Integer.toString(i);
        } else
            retStr = "" + i;
        return retStr;
    }

    enum TimeFormat {
        YYYY, MM, DD, HH, mm, ss
    }
}
