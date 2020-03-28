package com.daaaanil.weather.weather;

import android.util.Log;


import com.daaaanil.weather.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class Weather {
    public final String dayOfWeek;
    public final String minTemp;
    public final String maxTemp;
    public final String humidity;
    public final String description;
    public final String iconURl;

    public Weather(long timeStamp, double minTemp, double maxTemp, double humidity, String description, String iconName) {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(0);
        this.dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(5*(minTemp - 32)/5) + "\u00B0C";
        this.maxTemp = numberFormat.format(5*(maxTemp - 32)/5) + "\u00B0C";
        this.humidity = NumberFormat.getPercentInstance().format(humidity/100.0);
        this.description = description;
        this.iconURl = "http://openweathermap.org/img/wn/" + iconName + ".png";
    }

    private static String convertTimeStampToDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp*1000);
        TimeZone tz = TimeZone.getDefault();
        calendar.add(Calendar.MILLISECOND, tz.getOffset((calendar.getTimeInMillis())));
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE");
        Log.i("Days","Day: " + dateFormatter.format(calendar.getTime()));
        return dateFormatter.format(calendar.getTime());
    }
}
