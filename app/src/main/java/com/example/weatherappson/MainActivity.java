package com.example.weatherappson;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int CITY_SEARCH_REQUEST_CODE = 1001;
    static double lat;
    static double lon;
    static int indexfor=5;
    private TextView cityTextView, tempTextView, mainTextView, humidityTextView, windTextView, timeTextView;
    private ImageView weatherImageView;
    private EditText cityEditText ;
    private Button searchButton;
    private TextView[] forecastDayTextViews = new TextView[6];
    private TextView[] forecastIconTextViews = new TextView[6];
    private TextView[] forecastTempTextViews = new TextView[6];


    private static final String apiKey = "d408a7a28f98008638f1ead43fa87e97";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", "onCreate: Start");

        searchButton=findViewById(R.id.id_citySearch);
        cityTextView = findViewById(R.id.id_city);
        tempTextView = findViewById(R.id.id_degree);
        mainTextView = findViewById(R.id.id_main);
        humidityTextView = findViewById(R.id.id_humidity);
        windTextView = findViewById(R.id.id_wind);
        timeTextView = findViewById(R.id.id_time);
        weatherImageView = findViewById(R.id.id_weatherImage);
        forecastDayTextViews[0] = findViewById(R.id.id_forecastDay1);
        forecastDayTextViews[1] = findViewById(R.id.id_forecastDay2);
        forecastDayTextViews[2] = findViewById(R.id.id_forecastDay3);
        forecastDayTextViews[3] = findViewById(R.id.id_forecastDay4);
        forecastDayTextViews[4] = findViewById(R.id.id_forecastDay5);
        forecastDayTextViews[5] = findViewById(R.id.id_forecastDay5);

        //WeatherByCity("Istanbul");
        //WeatherByLatLon(lat,lon);
        WeatherByCity("istanbul");
        System.out.printf(String.valueOf(lat), lon);



        LocationUtils.getCurrentLocation(this, new LocationUtils.LocationListenerCallback() {
            @Override
            public void onLocationReceived(double lat, double lon) {
                Log.d("MainActivity", "onLocationReceived: Latitude - " + lat + ", Longitude - " + lon);
                WeatherByLatLon(lat, lon);
            }
        });
        Log.d("MainActivity", "onCreate: End");



    }

    private void WeatherByLatLon(double lat, double lon) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
        new FetchWeatherTask().execute(apiUrl);
    }


    private void WeatherByCity(String city) {
        String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&appid=" + apiKey + "&units=metric";
        new FetchWeatherTask().execute(apiUrl);
    }
/*
    private void changeCity(){
        String searchedCity = cityEditText.getText().toString().trim();
        if(!searchedCity.isEmpty()){
            String city = searchedCity;
            WeatherByCity(city);
        }
        else {
            Toast.makeText(this, "Please enter a city", Toast.LENGTH_SHORT).show();
        }
    }



 */


    /* private void WeatherByCity(String searchedCity) {
        Log.d("WeatherApp", "Fetching weather for city: " + searchedCity);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.openweathermap.org/data/2.5/forecast?q=" + searchedCity + "&appid=" + apiKey + "&units=metric")
                .get().build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Response response = client.newCall(request).execute();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.e("WeatherApp", "API request failed", e);
                }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String data = response.body().string();
                Log.d("WeatherApp", "API response: " + data);
                try {
                    JSONObject json = new JSONObject(data);
                    JSONObject city = json.getJSONObject("city");
                    JSONObject coord = city.getJSONObject("coord");
                    double lat = Double.parseDouble(coord.getString("lat"));
                    double lon = Double.parseDouble(coord.getString("lon"));

                    WeatherByLatLon(lat,lon);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    } catch (IOException e) {
        e.printStackTrace();
    }
}


     */

    public void showPopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle item clicks
                if (item.getItemId() == R.id.id_currentLocation) {

                    LocationUtils.getCurrentLocation(MainActivity.this, new LocationUtils.LocationListenerCallback() {
                        @Override
                        public void onLocationReceived(double latitude, double longitude) {
                            // Call WeatherByLatLon with obtained coordinates
                            WeatherByLatLon(latitude, longitude);
                        }
                    });

                    return true;
                } else if (item.getItemId() == R.id.id_otherCity) {

                    Intent intent = new Intent(MainActivity.this, CitySearch.class);
                    int CITY_SEARCH_REQUEST_CODE=1001;
                    startActivityForResult(intent, CITY_SEARCH_REQUEST_CODE);
                    return true;
                } else {
                    return false;
                }
            }
        });

        popupMenu.show();
    }

    private class FetchWeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                Scanner scanner = new Scanner(inputStream);
                scanner.useDelimiter("\\A");

                if (scanner.hasNext()) {
                    return scanner.next();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
              /*  try {
                    JSONObject json = new JSONObject(result);
                    JSONArray list = json.getJSONArray("list");
                    JSONObject firstDay = list.getJSONObject(0);

                    JSONObject main = firstDay.getJSONObject("main");
                    double temp = main.getDouble("temp");
                    int humidity = main.getInt("humidity");

                    JSONArray weatherArray = firstDay.getJSONArray("weather");
                    JSONObject weather = weatherArray.getJSONObject(0);
                    String mainDescription = weather.getString("main");
                    String icon = weather.getString("icon");

                    JSONObject wind = firstDay.getJSONObject("wind");
                    double windSpeed = wind.getDouble("speed");

                    String time = firstDay.getString("dt_txt");

                    // Update UI elements
                    cityTextView.setText("Istanbul");
                    tempTextView.setText(String.valueOf(temp));
                    mainTextView.setText(mainDescription);
                    humidityTextView.setText(String.valueOf(humidity));
                    windTextView.setText(String.valueOf(windSpeed));
                    timeTextView.setText(time);

                    // Load weather image using Picasso library (add Picasso dependency in build.gradle)
                    Picasso.get().load("https://openweathermap.org/img/w/" + icon + ".png").into(weatherImageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
                try {

                    JSONObject json=new JSONObject(result);
                    TextView[] forecast = new TextView[5];
                    TextView[] forecastTemp=new TextView[5];
                    ImageView[] forecastIcons=new ImageView[5];
                    IdAssign(forecast,forecastTemp,forecastIcons);

                    indexfor=5;
                    for (int i=0;i<forecast.length;i++){
                        forecastCal(forecast[i],forecastTemp[i],forecastIcons[i],indexfor,json);
                    }

                    JSONArray list=json.getJSONArray("list");
                    JSONObject objects = list.getJSONObject(0);
                    JSONArray array=objects.getJSONArray("weather");
                    JSONObject object=array.getJSONObject(0);

                    String description=object.getString("description");
                    String icons=object.getString("icon");

                    Date currentDate=new Date();
                    String dateString=currentDate.toString();
                    String[] dateSplit=dateString.split(" ");
                    String date=dateSplit[0]+", "+dateSplit[1] +" "+dateSplit[2];

                    JSONObject Main=objects.getJSONObject("main");
                    double temparature=Main.getDouble("temp");
                    String Temp=Math.round(temparature)+"°C";
                    double Humidity=Main.getDouble("humidity");
                    String hum=Math.round(Humidity)+"%";

                    JSONObject Wind=objects.getJSONObject("wind");
                    String windValue=Wind.getString("speed")+" "+"km/h";

                    JSONObject CityObject=json.getJSONObject("city");
                    String City=CityObject.getString("name");

                    setDataText(cityTextView,City);
                    setDataText(tempTextView,Temp);
                    setDataText(mainTextView,description);
                    setDataImage(weatherImageView,icons);
                    setDataText(timeTextView,date);
                    setDataText(humidityTextView,hum);
                    setDataText(windTextView,windValue);


                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }
    private void setDataText(final TextView text, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                text.setText(value);
            }
        });
    }
    private void setDataImage(final ImageView ImageView, final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (value){
                    case "02d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w02d)); break;
                    case "04d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w04d)); break;
                    case "09d": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w09d)); break;
                    case "09n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w09d)); break;
                    case "10n": ImageView.setImageDrawable(getResources().getDrawable(R.drawable.w10d)); break;


                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CITY_SEARCH_REQUEST_CODE && resultCode == RESULT_OK) {
            String searchedCity = data.getStringExtra("searchedCity");
            Log.d("MainActivity", "Received searched city: " + searchedCity);
            WeatherByCity(searchedCity);
        }
    }
    private void IdAssign(TextView[] forecast,TextView[] forecastTemp,ImageView[] forecastIcons){
        forecast[0]=findViewById(R.id.id_forecastDay1);
        forecast[1]=findViewById(R.id.id_forecastDay2);
        forecast[2]=findViewById(R.id.id_forecastDay3);
        forecast[3]=findViewById(R.id.id_forecastDay4);
        forecast[4]=findViewById(R.id.id_forecastDay5);
        forecastTemp[0]=findViewById(R.id.id_forecastTemp1);
        forecastTemp[1]=findViewById(R.id.id_forecastTemp2);
        forecastTemp[2]=findViewById(R.id.id_forecastTemp3);
        forecastTemp[3]=findViewById(R.id.id_forecastTemp4);
        forecastTemp[4]=findViewById(R.id.id_forecastTemp5);
        forecastIcons[0]=findViewById(R.id.id_forecastIcon1);
        forecastIcons[1]=findViewById(R.id.id_forecastIcon2);
        forecastIcons[2]=findViewById(R.id.id_forecastIcon3);
        forecastIcons[3]=findViewById(R.id.id_forecastIcon4);
        forecastIcons[4]=findViewById(R.id.id_forecastIcon5);

    }
    private void forecastCal(TextView forecast,TextView forecastTemp,ImageView forecastIcons,int index,JSONObject json) throws JSONException {
        JSONArray list=json.getJSONArray("list");
        for (int i=index; i<list.length(); i++) {
            JSONObject object = list.getJSONObject(i);

            String dt=object.getString("dt_txt"); // dt_text.format=2020-06-26 12:00:00
            String[] a=dt.split(" ");
            if ((i==list.length()-1) && !a[1].equals("12:00:00")){
                String[] dateSplit=a[0].split("-");
                Calendar calendar=new GregorianCalendar(Integer.parseInt(dateSplit[0]),Integer.parseInt(dateSplit[1])-1,Integer.parseInt(dateSplit[2]));
                Date forecastDate=calendar.getTime();
                String dateString=forecastDate.toString();
                String[] forecastDateSplit=dateString.split(" ");
                String date=forecastDateSplit[0]+", "+forecastDateSplit[1] +" "+forecastDateSplit[2];
                setDataText(forecast, date);

                JSONObject Main=object.getJSONObject("main");
                double temparature=Main.getDouble("temp");
                String Temp=Math.round(temparature)+"°";
                setDataText(forecastTemp,Temp);

                JSONArray array=object.getJSONArray("weather");
                JSONObject object1=array.getJSONObject(0);
                String icons=object1.getString("icon");
                setDataImage(forecastIcons,icons);

                return;
            }
            else if (a[1].equals("12:00:00")){

                String[] dateSplit=a[0].split("-");
                Calendar calendar=new GregorianCalendar(Integer.parseInt(dateSplit[0]),Integer.parseInt(dateSplit[1])-1,Integer.parseInt(dateSplit[2]));
                Date forecastDate=calendar.getTime();
                String dateString=forecastDate.toString();
                String[] forecastDateSplit=dateString.split(" ");
                String date=forecastDateSplit[0]+", "+forecastDateSplit[1] +" "+forecastDateSplit[2];
                setDataText(forecast, date);


                JSONObject Main=object.getJSONObject("main");
                double temparature=Main.getDouble("temp");
                String Temp=Math.round(temparature)+"°";
                setDataText(forecastTemp,Temp);

                JSONArray array=object.getJSONArray("weather");
                JSONObject object1=array.getJSONObject(0);
                String icons=object1.getString("icon");
                setDataImage(forecastIcons,icons);


                indexfor=i+1;
                return;
            }
        }
    }


}

