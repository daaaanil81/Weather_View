package com.daaaanil.weather.ui.Other_one;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.daaaanil.weather.R;
import com.daaaanil.weather.weather.DatabaseDescription.Contact;
import com.daaaanil.weather.weather.Weather;
import com.daaaanil.weather.weather.WeatherArrayAdapter;
import com.daaaanil.weather.weather.WeatherDatabaseHelper;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class OtherCityFragment1 extends Fragment {

    private Button homeButton;
    private TextView home_editView;
    private ArrayList<Weather> weatherList = new ArrayList<>();
    private WeatherArrayAdapter weatherArrayAdapter;
    private ListView weatherListView;
    public View root;
    String current_text;
    WeatherDatabaseHelper wdh;
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_other_1, container, false);
        home_editView = root.findViewById(R.id.other_locationEditText_1);
        wdh = new WeatherDatabaseHelper(getActivity());
        homeButton = root.findViewById(R.id.other_button_1);
        weatherListView = getActivity().findViewById(R.id.listView);
        weatherArrayAdapter = new WeatherArrayAdapter(getActivity(), weatherList);
        weatherListView.setAdapter(weatherArrayAdapter);

        SQLiteDatabase db = wdh.getWritableDatabase(); // Object sql
        String selection = Contact.COLUMN_NAME + " = ?";
        String[] selectionArgs = { Contact.COLUMN_OTHER_1 };
        Cursor cursor = db.query(Contact.TABLE_NAME, null, selection, selectionArgs, null, null, null);
        if(cursor.moveToFirst()) {
            int placeColIndex = cursor.getColumnIndex(Contact.COLUMN_PLACE);
            home_editView.setText(cursor.getString(placeColIndex));
            URL url = createURL(cursor.getString(placeColIndex));
            if (url != null) {
                dismissKeyboard(home_editView);
                GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                getLocalWeatherTask.execute(url);
            } else {
                Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.invalid_url, Snackbar.LENGTH_LONG).show();
            }
        }
        homeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                current_text = home_editView.getText().toString(); // Text from EditView
                SQLiteDatabase db = wdh.getWritableDatabase(); // Object sql
                String selection = Contact.COLUMN_NAME + " = ?";
                String[] selectionArgs = { Contact.COLUMN_OTHER_1 };
                Cursor cursor = db.query(Contact.TABLE_NAME, null, selection, selectionArgs, null, null, null);
                if(!current_text.isEmpty()) {
                    if(cursor.moveToFirst()) {
                        int idColIndex = cursor.getColumnIndex("id");
                        int nameColIndex = cursor.getColumnIndex("name");
                        int emailColIndex = cursor.getColumnIndex("place");
                            Log.d("Debug",
                                    "ID = " + cursor.getInt(idColIndex) +
                                            ", name = " + cursor.getString(nameColIndex) +
                                            ", place = " + cursor.getString(emailColIndex));
                        ContentValues values = new ContentValues();
                        values.put(Contact.COLUMN_NAME, Contact.COLUMN_OTHER_1);
                        values.put(Contact.COLUMN_PLACE, current_text);
                        selection = Contact.COLUMN_NAME + " LIKE ?";
                        db.update(
                                Contact.TABLE_NAME,
                                values,
                                selection,
                                selectionArgs);
                    }
                    else {
                        ContentValues cv = new ContentValues();
                        cv.put(Contact.COLUMN_NAME, Contact.COLUMN_OTHER_1);
                        cv.put(Contact.COLUMN_PLACE, current_text);
                        db.insert(Contact.TABLE_NAME, null, cv);
                        Log.i("Debug", "Add other");
                    }
                    URL url = createURL(home_editView.getText().toString());
                    if(url != null) {
                        dismissKeyboard(home_editView);
                        GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
                        getLocalWeatherTask.execute(url);
                    }
                    else{
                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.invalid_url,Snackbar.LENGTH_LONG).show();
                    }
                }
                else{
                    Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.check_edit, Snackbar.LENGTH_LONG).show();
                }
            }
        });
        wdh.close();
        return root;
    }
    private void dismissKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }
    private URL createURL(String city){
        String apiKey = getString(R.string.api_key);
        String baseUrl = getString(R.string.web_service_url);
        try{
            String urlString = baseUrl + URLEncoder.encode(city, "UTF-8") + "&units=imperial&appid=" + apiKey;
            return new URL(urlString);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(URL... urls) {
            HttpURLConnection connection = null;
            try{
                Log.i("Debug", urls[0].toString());
                connection = (HttpURLConnection)urls[0].openConnection();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                        String line;
                        while((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e){
                        Log.e("Read status", "Read error");
                        Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.read_error,Snackbar.LENGTH_LONG).show();
                    }
                    return new JSONObject(builder.toString());
                }
                else {
                    Log.e("Connect status", "Connection error: 1");
                    Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.connect_error,Snackbar.LENGTH_LONG).show();
                }
            }
            catch(Exception e){
                Log.e("Connect status", "Connection error: 2");
                Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout), R.string.connect_error,Snackbar.LENGTH_LONG).show();
            }
            finally {
                connection.disconnect();
                Log.i("Debug", "Close connection");
            }
            Log.i("Debug", "Close doInBackground");
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject weather) {
            Log.i("Debug", "onPostExecute");
            if(weather == null)
                return;
            convertJSONtoArrayList(weather);
            weatherArrayAdapter.notifyDataSetChanged(); // rebind to ListView
            weatherListView.smoothScrollToPosition(0); // scroll to top
        }
    }
    private void convertJSONtoArrayList(JSONObject forecast){
        weatherList.clear();
        try{
            JSONArray list = forecast.getJSONArray("list");
            double minTemp = 500;
            double maxTemp = 0;
            double humidity = 0;
            double k = 0;
            String description;
            String icon;

            for(int i = 0; i < list.length(); i++){
                JSONObject day = list.getJSONObject(i);
                String time = day.getString("dt_txt").substring(11);
                JSONObject temperatures = day.getJSONObject("main");
                JSONObject weather = day.getJSONArray("weather").getJSONObject(0);

                if(time.equals("00:00:00")){
                    minTemp = temperatures.getDouble("temp_min");
                    maxTemp = temperatures.getDouble("temp_max");
                    humidity = temperatures.getDouble("humidity");
                    k = 0;
                    continue;
                }
                if(minTemp > temperatures.getDouble("temp_min"))
                    minTemp = temperatures.getDouble("temp_min");
                if(maxTemp < temperatures.getDouble("temp_max"))
                    maxTemp = temperatures.getDouble("temp_max");
                if(humidity < temperatures.getDouble("humidity"))
                    humidity = temperatures.getDouble("humidity");
                description = weather.getString("description");
                icon = weather.getString("icon");
                k++;

                if(time.equals("21:00:00")) {
                    Log.i("Debug", "Min: "+ minTemp + " Max: " + maxTemp + " Humidity: " + humidity + " Desc: " + description + " Icon: " + icon);
                    weatherList.add(new Weather(
                            day.getLong("dt"),
                            minTemp,
                            maxTemp,
                            humidity,
                            description,
                            icon));
                }
            }

        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }
}