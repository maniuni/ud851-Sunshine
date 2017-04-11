package com.example.android.sunshine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private TextView dayForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dayForecast = (TextView) findViewById(R.id.day_forecast);

        // TODO (2) Display the weather forecast that was passed from MainActivity
        Intent intentFromMainActivity = getIntent();

        if(intentFromMainActivity.hasExtra(Intent.EXTRA_TEXT)) {
            String weatherForDay = intentFromMainActivity.getStringExtra(Intent.EXTRA_TEXT);
            dayForecast.setText(weatherForDay);
        }
    }
}