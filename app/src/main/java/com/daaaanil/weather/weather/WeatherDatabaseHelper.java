package com.daaaanil.weather.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.daaaanil.weather.weather.DatabaseDescription.Contact;


public class WeatherDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "WeatherCity.db";

    public WeatherDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DB debug", Contact.CREATE_CITY_TABLE);
        db.execSQL(Contact.CREATE_CITY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Contact.DROP_TABLE);
        db.execSQL(Contact.CREATE_CITY_TABLE);
    }

}
