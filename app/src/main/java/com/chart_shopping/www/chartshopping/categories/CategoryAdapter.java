package com.chart_shopping.www.chartshopping.categories;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chart_shopping.www.chartshopping.R;


public class CategoryAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;

    public CategoryAdapter(final Context context) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return CostCategory.categories.length;
    }

    @Override
    public Object getItem(int position) {
        return CostCategory.categories[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = layoutInflater.inflate(R.layout.selection_dialog_item, parent, false);

        TextView category = (TextView) view.findViewById(R.id.category);

        category.setText(CostCategory.categories[position]);
        //category.setTextColor(CostCategory.colors[position]);

        return view;
    }
}