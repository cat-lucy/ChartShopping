package com.chart_shopping.www.chartshopping.statistics;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chart_shopping.www.chartshopping.CategoryListActivity;
import com.chart_shopping.www.chartshopping.R;
import com.chart_shopping.www.chartshopping.categories.CostCategory;
import com.chart_shopping.www.chartshopping.database.Database;
import com.chart_shopping.www.chartshopping.database.DatabaseContract;
import com.chart_shopping.www.chartshopping.database.Purchase;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String PURCHASE_WITHOUT_CATEGORY = "-1";

    private TextView category_text, sum_of_category;
    private Chart chart;
    private Toolbar toolbar;

    private Database database;
    private String month, year;
    private int categories_length, selected_category;
    private float total_sum;
    private float[] purchases_sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        toolbar = (Toolbar) findViewById(R.id.statistics_toolbar);
        toolbar.setOnClickListener(this);

        Button left = (Button) findViewById(R.id.left);
        Button right = (Button) findViewById(R.id.right);
        FloatingActionButton category_list_button = (FloatingActionButton)
                findViewById(R.id.category_list_button);

        chart = (Chart) findViewById(R.id.chart);
        category_text = (TextView) findViewById(R.id.category_text);
        sum_of_category = (TextView) findViewById(R.id.sum_of_category);

        database = new Database(this);
        database.openRead();

        left.setOnClickListener(this);
        right.setOnClickListener(this);
        category_list_button.setOnClickListener(this);

        selected_category = 0;
        month = DateInfo.getMonthNumber();
        year = DateInfo.getYear();

        calculation();
    }

    @Override
    protected void onRestart () {
        super.onRestart();
        calculation();
    }

    private void calculation() {
        categories_length = CostCategory.categories.length;
        String question = DatabaseContract.CATEGORY + " != ? AND "
                + "strftime('%m', " + DatabaseContract.DATE + ") = ? AND "
                + "strftime('%Y', " + DatabaseContract.DATE + ") = ?";
        Purchase[] purchases = database.getPurchases(question,
                new String[]{PURCHASE_WITHOUT_CATEGORY, month, year});
        total_sum = 0;
        purchases_sum = new float[categories_length];
        for (int i = 0; i < categories_length; i++) {
            purchases_sum[i] = 0;
        }
        if (purchases != null) {
            for (Purchase purchase : purchases) {
                purchases_sum[purchase.category] += purchase.value;
                total_sum += purchase.value;
            }
            float[] purchases_percentage = new float[categories_length];
            for (int i = 0; i < categories_length; i++) {
                purchases_percentage[i] = purchases_sum[i] / total_sum;
            }
            chart.setValue(purchases_percentage);
        }
        toolbar.setTitle(DateInfo.getMonthName() + " : "
                + Integer.toString(Rounder.fullRound(-total_sum)) + "р.");
        setSupportActionBar(toolbar);
        update();
    }

    private void update() {
        chart.selectCategory(selected_category);
        category_text.setText(CostCategory.categories[selected_category]);
        if (purchases_sum[selected_category] != 0)
            sum_of_category.setText("Сумма : " + Integer.toString(Rounder.fullRound(
                    -purchases_sum[selected_category])) + "р ( " +
                    Float.toString(Rounder.secondDigitRound(purchases_sum[selected_category]
                            / total_sum * 100)) + "% ) ");
        else
            sum_of_category.setText("На такое денег не тратим");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left:
                if (selected_category == 0)
                    selected_category = categories_length - 1;
                else
                    selected_category--;
                break;
            case R.id.right:
                if (selected_category == categories_length - 1)
                    selected_category = 0;
                else
                    selected_category++;
                break;
            case R.id.category_list_button:
                Intent intent = new Intent(getApplicationContext(), CategoryListActivity.class);
                intent.putExtra("selected_category", selected_category);
                intent.putExtra("month", month);
                intent.putExtra("year", year);
                startActivity(intent);
                break;
        }
        update();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.statistics_action_settings:
                return true;
            case R.id.statistics_action_pick_month:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();
    }
}
