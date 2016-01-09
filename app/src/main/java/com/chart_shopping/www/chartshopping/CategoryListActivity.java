package com.chart_shopping.www.chartshopping;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chart_shopping.www.chartshopping.categories.CostCategory;
import com.chart_shopping.www.chartshopping.database.Database;
import com.chart_shopping.www.chartshopping.database.DatabaseContract;
import com.chart_shopping.www.chartshopping.database.Purchase;
import com.chart_shopping.www.chartshopping.database.PurchaseAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CategoryListActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int CATEGORY_LIST_DIALOG = 0;

    private EditText organization, date, time, value;
    private ListView category_list;
    Database database;
    Purchase[] purchases;
    Purchase purchase;

    private String month, year;
    private int selected_category;

    DialogInterface.OnClickListener dialog_click_listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            purchase = new Purchase();
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    addLine();
                    break;
                case Dialog.BUTTON_NEUTRAL:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        selected_category = getIntent().getIntExtra("selected_category", 0);
        month = getIntent().getStringExtra("month");
        year = getIntent().getStringExtra("year");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(CostCategory.categories[selected_category]);
        toolbar.setBackgroundColor(CostCategory.colors[selected_category]);
        setSupportActionBar(toolbar);

        category_list = (ListView) findViewById(R.id.category_list);
        category_list.setOnItemClickListener(this);

        database = new Database(this);
        database.openWrite();

        listViewUpdate();
    }

    public void listViewUpdate() {
        String question = DatabaseContract.CATEGORY + " = ? AND "
                + "strftime('%m', " + DatabaseContract.DATE + ") = ? AND "
                + "strftime('%Y', " + DatabaseContract.DATE + ") = ?";
        purchases = database.getPurchases(question, new String[]{
                Integer.toString(selected_category), month, year});
        if (purchases == null) {
            category_list.setVisibility(View.INVISIBLE);
            return;
        }
        category_list.setVisibility(View.VISIBLE);
        PurchaseAdapter main_adapter = new PurchaseAdapter(this, purchases);
        category_list.setAdapter(main_adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category_list, menu);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.category_list_action_settings:
                return true;
            case R.id.category_list_action_add_line:
                showDialog(CATEGORY_LIST_DIALOG);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("InflateParams")
    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
        dialog_builder.setTitle(R.string.category_list_dialog);
        RelativeLayout view = (RelativeLayout) getLayoutInflater()
                .inflate(R.layout.category_list_dialog, null);
        dialog_builder.setPositiveButton(R.string.category_list_dialog_positive, dialog_click_listener);
        dialog_builder.setNegativeButton(R.string.category_list_dialog_neutral, dialog_click_listener);
        dialog_builder.setView(view);
        return dialog_builder.create();
    }

    @SuppressLint("SimpleDateFormat")
    @SuppressWarnings("deprecation")
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        if (id == CATEGORY_LIST_DIALOG) {
            organization = (EditText) dialog.getWindow().findViewById(R.id.dialog_organization);
            date = (EditText) dialog.getWindow().findViewById(R.id.dialog_date);
            time = (EditText) dialog.getWindow().findViewById(R.id.dialog_time);
            value = (EditText) dialog.getWindow().findViewById(R.id.dialog_value);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date.setText(dateFormat.format(new Date()));
            dateFormat = new SimpleDateFormat("hh:mm");
            time.setText(dateFormat.format(new Date()));
            organization.setText("");
            value.setText("");
        }
    }

    private void addLine() {
        purchase.organization = String.valueOf(organization.getText());
        purchase.date = String.valueOf(date.getText());
        purchase.time = String.valueOf(time.getText());
        try {
            purchase.value = -Math.abs(Float.valueOf(String.valueOf(value.getText())));
        } catch (Exception e) {
            Toast.makeText(this, "Purchase not added : Invalid value", Toast.LENGTH_LONG).show();
            return;
        }
        purchase.category = selected_category;
        if (purchase.organization.equals("")) {
            Toast.makeText(this, "Purchase not added : Empty organization", Toast.LENGTH_LONG).show();
            return;
        }

        database.addLine(DatabaseContract.TABLE_NAME, purchase);
        listViewUpdate();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        //database.deleteLine(DatabaseContract.TABLE_NAME, purchases[position].id);
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
