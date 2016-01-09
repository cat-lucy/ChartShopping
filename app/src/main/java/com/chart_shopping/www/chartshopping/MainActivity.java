package com.chart_shopping.www.chartshopping;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chart_shopping.www.chartshopping.R;
import com.chart_shopping.www.chartshopping.categories.CategoryAdapter;
import com.chart_shopping.www.chartshopping.database.Database;
import com.chart_shopping.www.chartshopping.database.DatabaseContract;
import com.chart_shopping.www.chartshopping.database.Purchase;
import com.chart_shopping.www.chartshopping.database.PurchaseAdapter;
import com.chart_shopping.www.chartshopping.statistics.StatisticsActivity;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final String PURCHASE_WITHOUT_CATEGORY = "-1";
    private static final int CATEGORY_DIALOG = 0;

    private ListView new_purchase;
    private Database database;
    Purchase[] purchases;
    Purchase purchase;

    DialogInterface.OnClickListener dialog_click_listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            purchase.category = which;
            database.changeCategory(DatabaseContract.TABLE_NAME, purchase);
            listViewUpdate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton refresh_fab = (FloatingActionButton) findViewById(R.id.refresh_fab);
        refresh_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listViewUpdate();
            }
        });

        new_purchase = (ListView) findViewById(R.id.new_purchase);
        new_purchase.setOnItemClickListener(this);
        database = new Database(this);
        listViewUpdate();
    }

    public void listViewUpdate() {
        database.openWrite();
        String question = DatabaseContract.CATEGORY + "= ?";
        purchases = database.getPurchases(question, new String[]{PURCHASE_WITHOUT_CATEGORY});
        if (purchases == null) {
            new_purchase.setVisibility(View.INVISIBLE);
            return;
        }
        new_purchase.setVisibility(View.VISIBLE);
        PurchaseAdapter main_adapter = new PurchaseAdapter(this, purchases);
        new_purchase.setAdapter(main_adapter);
        purchase = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.main_action_settings:
                return true;
            case R.id.main_action_statistic:
                intent = new Intent(this, StatisticsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder dialog_builder = new AlertDialog.Builder(this);
        if (id == CATEGORY_DIALOG) {
            dialog_builder.setTitle(R.string.category_dialog);
            CategoryAdapter category_adapter = new CategoryAdapter(this);
            dialog_builder.setAdapter(category_adapter, dialog_click_listener);
        }
        return dialog_builder.create();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showDialog(CATEGORY_DIALOG);
        purchase = purchases[position];
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
