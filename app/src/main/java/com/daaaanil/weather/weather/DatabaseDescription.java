package com.daaaanil.weather.weather;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription{
    public static final String AUTHORITY = "com.daaaanil.weather";

    public static final class Contact implements BaseColumns {
        public static final String TABLE_NAME = "cities";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PLACE = "place";
        public static final String COLUMN_HOME = "home";
        public static final String COLUMN_OTHER_1 = "other1";
        public static final String CREATE_CITY_TABLE = "CREATE TABLE cities( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, place TEXT NOT NULL);";
        static final String DROP_TABLE="DROP TABLE IF EXISTS "+ TABLE_NAME;
    }
}
