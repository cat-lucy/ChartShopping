package com.chart_shopping.www.chartshopping.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chart_shopping.www.chartshopping.R;
import com.chart_shopping.www.chartshopping.database.Purchase;

public class PurchaseAdapter extends BaseAdapter {

    private Purchase[] purchases;
    private LayoutInflater layoutInflater;

    public PurchaseAdapter(final Context context, Purchase[] purchases) {
        this.purchases = purchases;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return purchases.length;
    }

    @Override
    public Object getItem(int position) {
        return purchases[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = layoutInflater.inflate(R.layout.new_purchase_item, parent, false);

        TextView organization = (TextView) view.findViewById(R.id.new_purchase_organization);
        TextView value = (TextView) view.findViewById(R.id.new_purchase_value);
        TextView date_time = (TextView) view.findViewById(R.id.new_purchase_date_time);

        organization.setText(purchases[position].organization);
        value.setText(Float.toString(purchases[position].value));
        date_time.setText(purchases[position].date + "   " + purchases[position].time);

        return view;
    }
}
