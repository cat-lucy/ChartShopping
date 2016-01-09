package com.chart_shopping.www.chartshopping.statistics;

public class Rounder {

    public static int fullRound(Float value) {
        if ((value - value.intValue()) * 2 > 1)
            return value.intValue() + 1;
        else return value.intValue();
    }

    public static float secondDigitRound (Float value) {
        float round_value = fullRound(value * 100);
        return round_value / 100;
    }
}
