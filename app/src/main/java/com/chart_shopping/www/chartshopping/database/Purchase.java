package com.chart_shopping.www.chartshopping.database;

import android.text.Editable;

public class Purchase {

    public long id;
    public String date, time, organization;
    public float value;
    public int category;

    public Purchase() {
        date = "";
        time = "";
        value = 0;
        organization = "";
        category = -1;
    }

    public Purchase(long id, String date, String time, float value, String organization) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.value = value;
        this.organization = organization;
        this.category = -1;
    }

    public Purchase(long id, String date, String time, float value, String organization, int category) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.value = value;
        this.organization = organization;
        this.category = category;
    }
}
