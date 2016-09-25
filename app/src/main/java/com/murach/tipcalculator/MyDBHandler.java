package com.murach.tipcalculator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthewmagee on 9/25/16.
 */
public class MyDBHandler extends SQLiteOpenHelper {

    //define database variables
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "tipFinal2.db";
    public static final String TABLE_TIPS = "TIPS";
    public static final String COLUMN_ID = "_id";
    public static final String bill_date = "bill_date";
    public static final String bill_amount = "bill_amount";
    public static final String tip_percent = "tip_percent";


    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String query =  "CREATE TABLE " + TABLE_TIPS +"(" +
                COLUMN_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                bill_date   + " INTEGER, " +
                bill_amount + " REAL, " +
                tip_percent + " REAL" +
                ")";

        sqLiteDatabase.execSQL(query);

        insetValues(sqLiteDatabase);


    }

    private void insetValues(SQLiteDatabase sqLiteDatabse) {

        long millis = System.currentTimeMillis() % 1000;

       String qry = "INSERT INTO TIPS (bill_date, bill_amount, tip_percent) VALUES ("+millis+",40.05,15);";
        String qry2 = "INSERT INTO TIPS (bill_date, bill_amount, tip_percent) VALUES (1,50.10,20);";
        sqLiteDatabse.execSQL(qry);
        sqLiteDatabse.execSQL(qry2);


    }

    //print out the database as a string
    public List<Tip> getTips(){
        List<Tip> tips = new ArrayList<Tip>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " +TABLE_TIPS + " WHERE 1";

        //cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //move to the first row in your result

        c.moveToFirst();

        while(!c.isAfterLast()){

            Tip product = cursorToProduct(c);
            tips.add(product);
            Log.e("Tips: ", DatabaseUtils.dumpCurrentRowToString(c));
            c.moveToNext();

        }

        db.close();

        return tips;
    }

    private Tip cursorToProduct(Cursor c) {

        Tip tip = new Tip();
        tip.setId(c.getInt(0));
        tip.setTipPercent(c.getFloat(1));

        return tip;
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPS);
        onCreate(sqLiteDatabase);

    }

    public void addTips(Tip tip) {

        ContentValues values = new ContentValues();
        values.put(bill_date,tip.getDateMillis());
        values.put(bill_amount,tip.getBillAmount());
        values.put(tip_percent, tip.getTipPercent());
        //get reference to the database
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_TIPS, null, values);
        db.close();

    }

    public Tip getLastTip(){

        Tip tip = new Tip();
        SQLiteDatabase db = getWritableDatabase();
        String query = "select * from TIPS where _id = (select max(_id) from TIPS);";

        //cursor point to a location in your results
        Cursor c = db.rawQuery(query, null);
        //move to the first row in your result

        c.moveToFirst();

            tip.setDateMillis(c.getLong(1));
            tip.setBillAmount(c.getFloat(2));
            tip.setTipPercent(c.getFloat(3));
            c.moveToNext();

        db.close();

        return tip;
    }

    public String averageTip(){

        String avg = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "select avg(tip_percent) from TIPS;";
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();

        float f = c.getFloat(0);
         avg = Float.toString(f);


        db.close();

        return avg;
    }
}
