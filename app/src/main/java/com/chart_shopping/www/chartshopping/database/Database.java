package com.chart_shopping.www.chartshopping.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.concurrent.DelayQueue;

public class Database {

    private Context context;
    private DatabaseHelper database_helper;
    private SQLiteDatabase database;

    public Database(Context context) {
        this.context = context;
    }

    public void openRead() {
        database_helper = new DatabaseHelper(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.VERSION);
        database = database_helper.getReadableDatabase();
    }

    public void openWrite() {
        database_helper = new DatabaseHelper(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.VERSION);
        database = database_helper.getWritableDatabase();
    }

    public void close() {
        if (database_helper != null)
            database_helper.close();
    }

    public Purchase[] getPurchases(String question, String[] categories) {
        Purchase[] purchases = null;
        int count;
        Cursor cursor = database.query(DatabaseContract.TABLE_NAME, null,
                question, categories, null, null, null);
        assert cursor != null;
        count = cursor.getCount();
        if (count != 0) {
            cursor.moveToFirst();
            purchases = new Purchase[count];
            Purchase purchase;
            for (int i = count - 1; i >= 0; i--) {
                purchase = new Purchase(cursor.getLong(cursor.getColumnIndex(DatabaseContract.ID)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.TIME)),
                        cursor.getFloat(cursor.getColumnIndex(DatabaseContract.VALUE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseContract.ORGANIZATION)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseContract.CATEGORY)));
                purchases[i] = purchase;
                cursor.moveToNext();
            }
        }
        cursor.close();
        return purchases;
    }

    private ContentValues getContentValues (Purchase purchase) {
        ContentValues line = new ContentValues();
        line.put(DatabaseContract.DATE, purchase.date);
        line.put(DatabaseContract.TIME, purchase.time);
        line.put(DatabaseContract.VALUE, purchase.value);
        line.put(DatabaseContract.ORGANIZATION, purchase.organization);
        line.put(DatabaseContract.CATEGORY, purchase.category);
        return line;
    }

    public void changeCategory (String table, Purchase purchase) {
        long id = purchase.id;
        database.update(table, getContentValues(purchase), DatabaseContract.ID + " = " + id,
                null);
    }

    public void addLine(String table, Purchase purchase) {
        database.insert(table, null, getContentValues(purchase));
    }

    public void deleteLine(String table, long id) {
        database.delete(table, DatabaseContract.ID + " = " + id, null);
    }

    public void dropTable(String table) {
        database.execSQL("drop table " + table);
    }

    class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL("create table " + DatabaseContract.TABLE_NAME + " ("
                    + DatabaseContract.ID + " integer primary key autoincrement,"
                    + DatabaseContract.DATE + " date,"
                    + DatabaseContract.TIME + " time,"
                    + DatabaseContract.ORGANIZATION + " text,"
                    + DatabaseContract.VALUE + " float,"
                    + DatabaseContract.CATEGORY + " int"
                    + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
