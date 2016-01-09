package com.chart_shopping.www.chartshopping.processing;

import android.annotation.SuppressLint;
import android.util.Log;

import com.chart_shopping.www.chartshopping.database.Purchase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SmsParser {

    private final String VISA = "VISA4681";
    private final String BALANCE = "Баланс:";
    private final String REFUND = "возврат";
    private final String BUY = "покупка";
    private final String DISBURSEMENT = "выдача";
    private final String MONEY = "наличных";
    private final String MEANS = "покупки";
    private final String ENROLLMENT = "зачисление";
    private String text, word;
    private int text_length;
    private float num;
    public float balance;

    public SmsParser(String text) {
        this.text = text;
    }

    public Purchase parse() {
        Purchase purchase = new Purchase();
        text_length = text.length();
        int i = 0;
        i = createWord(i);
        String firstWord = word;
        if (!firstWord.equals(VISA))
            return null;
        while (i < text_length) {
            i = createWord(++i);
            switch (word) {
                case BALANCE:
                    createNumber(++i);
                    balance = num;
                    return purchase;
                case BUY:
                    i = createNumber(++i);
                    purchase.value = -num;
                    i = createOrganization(i);
                    purchase.organization = word;
                    break;
                case ENROLLMENT:
                    i = createNumber(++i);
                    purchase.value = num;
                    i = createOrganization(i);
                    purchase.organization = word;
                    break;
                case REFUND:
                    i = createWord(++i);
                    if (word.equals(MEANS)) {
                        i = createNumber(++i);
                        purchase.value = num;
                        i = createOrganization(i);
                        purchase.organization = word;
                    }
                    break;
                case DISBURSEMENT:
                    i = createWord(++i);
                    if (word.equals(MONEY)) {
                        i = createNumber(++i);
                        purchase.value = -num;
                        i = createOrganization(i);
                        purchase.organization = word;
                    }
                    break;
                default:
                    if (checkDate()) purchase.date = currentDate();
                    else if (checkTime()) purchase.time = word;
                    break;
            }
        }
        return null;
    }

    private boolean checkDate() { // check
        int length = word.length();
        if (length != 8) return false;
        for (int i = 0; i < length; i++) {
            if (i == 2 || i == 5)
                if (word.charAt(i) != '.')
                    return false;
                else continue;
            if (word.charAt(i) < '0' || word.charAt(i) > '9')
                return false;
        }
        return true;
    }

    @SuppressLint("SimpleDateFormat")
    private String currentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }

    private boolean checkTime() { // check
        int length = word.length();
        if (length != 5) return false;
        for (int i = 0; i < length; i++) {
            if (i == 2)
                if (word.charAt(i) != ':')
                    return false;
                else continue;
            if (word.charAt(i) < '0' || word.charAt(i) > '9')
                return false;
        }
        return true;
    }

    private int createWord(int pos) { //check
        word = "";
        while (pos < text_length && text.charAt(pos) != ' ') {
            word += text.charAt(pos);
            pos++;
        }
        return pos;
    }

    private int createNumber(int pos) { //check
        num = 0;
        while (pos < text_length && text.charAt(pos) >= '0' && text.charAt(pos) <= '9') {
            num = num * 10 + (text.charAt(pos) - '0');
            pos++;
        }
        if (pos < text_length && text.charAt(pos) == '.') {
            pos++;
            double divisor = 1;
            while (pos < text_length && text.charAt(pos) >= '0' && text.charAt(pos) <= '9') {
                num = num * 10 + (text.charAt(pos) - '0');
                divisor *= 10;
                pos++;
            }
            num /= divisor;
        }
        Log.d("fgh", num + "");
        if (pos < text_length && text.charAt(pos) == ' ') pos++;
        return pos;
    }

    private int createOrganization(int pos) { //check
        String org = "";
        int prev_pos = pos;
        pos = createWord(++pos);
        while (!word.equals(BALANCE)) {
            org += word + " ";
            prev_pos = pos;
            pos = createWord(++pos);
        }
        pos = prev_pos;
        word = org;
        return pos;
    }
}
