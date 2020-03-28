package com.daaaanil.weather.weather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.daaaanil.weather.R;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {


    private static class ViewHolder {
        ImageView conditionImageView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }
    private Map<String, Bitmap> bitmaps = new HashMap<>();
    private ArrayList<Weather> weatherList;
    private Context ctx;
    private LayoutInflater lInflater;
    public WeatherArrayAdapter(Context context, ArrayList<Weather> forecast){
        super(context, -1, forecast);
        ctx = context;
        weatherList = forecast;
        lInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Debug", "getView");
        Weather day = getItem(position);
        ViewHolder viewHolder;
        if(convertView == null) {

            viewHolder = new ViewHolder();
            convertView = lInflater.inflate(R.layout.list_item, parent, false);
            viewHolder.conditionImageView = convertView.findViewById(R.id.conditionImageView);
            viewHolder.dayTextView = convertView.findViewById(R.id.dayTextView);
            viewHolder.lowTextView = convertView.findViewById(R.id.lowTextView);
            viewHolder.hiTextView = convertView.findViewById(R.id.hiTextView);
            viewHolder.humidityTextView = convertView.findViewById(R.id.humidityTextView);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        if(bitmaps.containsKey(day.iconURl))
        {
            viewHolder.conditionImageView.setImageBitmap(bitmaps.get(day.iconURl));
        }
        else {
            Log.i("Debug",day.iconURl);
            new LoadImageTask(viewHolder.conditionImageView).execute(day.iconURl);
        }
        Context context = getContext();
        viewHolder.dayTextView.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
        viewHolder.lowTextView.setText(context.getString(R.string.low_temp, day.minTemp));
        viewHolder.hiTextView.setText(context.getString(R.string.high_temp, day.maxTemp));
        viewHolder.humidityTextView.setText(context.getString(R.string.humidity, day.humidity));
        Log.i("Debug","Return getView Adapter");
        return convertView;
    }
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;
        public LoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            try{
                Log.i("Debug",strings[0]);
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                try(InputStream inputStream = connection.getInputStream()){
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(strings[0], bitmap);
                    Log.i("Debug","Recv bitmap");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.i("Debug","Set Image");
            imageView.setImageBitmap(bitmap);
        }
    }
}
