package com.lineage.managerUI;

import com.lineage.server.model.Instance.L1ItemInstance;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class CommonUtil {
    /**
     * 2011.08.05 金額顯示
     *
     * @param number
     * @return
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
     * 2011.08.05 隨機函數
     *
     * @param number
     * @return
     */
    public static int random(int number) {
        Random rnd = new Random();
        return rnd.nextInt(number);
    }

    /**
     * 2011.08.05 隨機函數
     *
     * @param lbound
     * @param ubound
     * @return
     */
    public static int random(int lbound, int ubound) {
        return (int) ((Math.random() * (ubound - lbound + 1)) + lbound);
    }

    /**
     * 2011.08.30 數據格式
     *
     * @param type
     * @return
     */
    public static String dateFormat(String type) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.KOREA);
        return sdf.format(Calendar.getInstance().getTime());
    }

    /**
     * 2011.08.30 數據格式
     *
     * @param type
     * @return
     */
    public static String dateFormat(String type, Timestamp date) {
        SimpleDateFormat sdf = new SimpleDateFormat(type, Locale.KOREA);
        return sdf.format(date.getTime());
    }

    /**
     * 2011.08.31 項目結束時間
     *
     * @param item
     * @param minute
     */
    public static void SetTodayDeleteTime(L1ItemInstance item, int minute) {
        Timestamp deleteTime = null;
        deleteTime = new Timestamp(System.currentTimeMillis() + (60000 * minute));
        item.set_time(deleteTime);
    }

    /**
     * 2011.08.31 項目指定結束時間（今天剩下的時間計算）系統。
     *
     * @param item
     */
    public static void SetTodayDeleteTime(L1ItemInstance item) {
        int hour = Integer.parseInt(dateFormat("HH"));
        int minute = Integer.parseInt(dateFormat("mm"));
        int time = 0;
        if (hour <= 22 && minute < 30) {
            time = (23 - hour) * 60 + (60 - minute) - 60;
        } else {
            time = (23 - hour) * 60 + (60 - minute) - 60 + (24 * 60);
        }
        Timestamp deleteTime = null;
        deleteTime = new Timestamp(System.currentTimeMillis() + (60000 * time));
        item.set_time(deleteTime);
    }

    /**
     * 2011.08.31 指定的剩餘時間
     *
     * @param item
     */
    public static int getRestTime(int hh) {
        int hour = Integer.parseInt(dateFormat("HH"));
        int minute = Integer.parseInt(dateFormat("mm"));
        int time = 0;
        time = (hh - hour) * 60 - minute;
        return time;
    }
}
