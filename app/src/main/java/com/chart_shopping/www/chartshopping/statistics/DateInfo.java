package com.chart_shopping.www.chartshopping.statistics;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateInfo {

    private static final String[] months = {
            "Январь",
            "Февраль",
            "Март",
            "Апрель",
            "Май",
            "Июнь",
            "Июль",
            "Август",
            "Сентябрь",
            "Октябрь",
            "Ноябрь",
            "Декабрь"
    };

    @SuppressLint("SimpleDateFormat")
    public static String getMonthNumber() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
        return dateFormat.format(new Date());
    }

    @SuppressLint("SimpleDateFormat")
    public static String getYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(new Date());
    }

    public static String getMonthName() {
        return months[Integer.parseInt(getMonthNumber()) - 1];
    }
}
