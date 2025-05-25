package com.lineage.server.utils;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class CommonUtil {
    /**
     * 2011.08.05 金額表示
     *
     */
    public static String numberFormat(int number) {
        try {
            NumberFormat nf = NumberFormat.getInstance();
            return nf.format(number);
        } catch (Exception e) {
            return Integer.toString(number);
        }
    }

    /**
     * 2011.08.05 ランダム関数
     *
     */
    public static int random(int number) {
        Random rnd = new Random();
        return rnd.nextInt(number);
    }

    /**
     * 2011.08.05 ランダム関数
     *
     */
    public static int random(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }

    /**
     * 2011.08.30 データフォーマット
     *
     */
    public static String dateFormat(String type) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.KOREA);
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * 2011.08.30 データフォーマット
     *
     */
    public static String dateFormat(String type, Timestamp date) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.KOREA);
        return sdf.format(date.getTime());
    }

    /**
     * 2011.08.31 指定時間までの残り時間
     *
     */
    public static int getRestTime(int hh) {
        int hour = Integer.parseInt(dateFormat("HH"));
        int minute = Integer.parseInt(dateFormat("mm"));
        int time = 0;
        time = (hh - hour) * 60 - minute;
        return time;
    }
}
