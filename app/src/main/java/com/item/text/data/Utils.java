package com.item.text.data;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wuzongjie on 2018/9/25
 */
public class Utils {

    /**
     * 数据的解析赋值
     *
     * @param array 数据源
     * @param tag   时间有所不同的设置
     * @return ArrayList<KLineBean>
     */
    public static ArrayList<KLineBean> parseKLine(JSONArray array, int tag) {
        ArrayList<KLineBean> beans = new ArrayList<>();
        for (int i = 0, len = array.length(); i < len; i++) {
            JSONArray data = array.optJSONArray(i);
            String pattern = "MM-dd HH:mm";
            if (tag == 4 || tag == 6 || tag == 7) {
                pattern = "yyyy-MM-dd";
            }
            String date = getFormatTime(pattern, new Date(data.optLong(0)));
            //K线实体类
            KLineBean kLineData = new KLineBean(date, (float) data.optDouble(1), (float) data.optDouble(4),
                    (float) data.optDouble(2), (float) data.optDouble(3), (float) data.optDouble(5));
            beans.add(kLineData);
        }
        return beans;
    }

    /**
     * 将固定格式转化成时间戳（默认 yyyy-MM-dd HH:mm:ss）
     */
    public static long getTimeMillis(String format, String dateString) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        try {
            Date date = sdf.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将时间戳转化成固定格式（默认 yyyy-MM-dd HH:mm:ss 当前时间 ）
     */
    public static String getFormatTime(String format, Date date) {
        if (TextUtils.isEmpty(format)) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }


    /**
     * 将时间戳转date
     */
    public static Date getDate(String pattern, Long dateString) {
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        String d = format.format(dateString);
        Date date = null;
        try {
            date = format.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
