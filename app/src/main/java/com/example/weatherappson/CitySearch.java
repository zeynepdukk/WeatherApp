package com.example.weatherappson;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CitySearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_search);

        final EditText cityEditText = findViewById(R.id.id_cityEdit);
        final Button searchButton = findViewById(R.id.id_citySearch);



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchedCity = cityEditText.getText().toString();

                if (!TextUtils.isEmpty(searchedCity)) {
                    Intent intent = new Intent();
                    intent.putExtra("searchedCity", searchedCity);

                    // Set the result as OK and send the intent back to the calling activity
                    setResult(RESULT_OK, intent);

                    // Finish the CitySearch activity
                    finish();
                } else {
                    Toast.makeText(CitySearch.this, "Please enter a city", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
}




