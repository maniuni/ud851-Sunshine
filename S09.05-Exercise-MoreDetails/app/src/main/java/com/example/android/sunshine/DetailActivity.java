/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineDateUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

public class DetailActivity extends AppCompatActivity  implements
        LoaderManager.LoaderCallbacks<Cursor>{
//      TODO (21) Implement LoaderManager.LoaderCallbacks<Cursor>

    /*
     * In this Activity, you can share the selected day's forecast. No social sharing is complete
     * without using a hashtag. #BeTogetherNotTheSame
     */
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

//  TODO (18) Create a String array containing the names of the desired data columns from our ContentProvider
    public String[] columns = new String[] {
        WeatherContract.WeatherEntry.COLUMN_DATE,
        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
        WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
        WeatherContract.WeatherEntry.COLUMN_PRESSURE,
        WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
        WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
        WeatherContract.WeatherEntry.COLUMN_DEGREES
    };
//  TODO (19) Create constant int values representing each column name's position above
    public static int dateColumnIndex = 0;
    public static int maxTempColumnIndex = 1;
    public static int minTempColumnIndex = 2;
    public static int humidityColumnIndex = 3;
    public static int pressureColumnIndex = 4;
    public static int windColumnIndex = 5;
    public static int weatherIdColumnIndex = 6;
    public static int degreesColumnIndex= 7;

//  TODO (20) Create a constant int to identify our loader used in DetailActivity
    private final int ID_DETAIL_LOADER = 55;

    /* A summary of the forecast that can be shared by clicking the share button in the ActionBar */
    private String mForecastSummary;

//  TODO (15) Declare a private Uri field called mUri
    private Uri mUri;

//  TODO (10) Remove the mWeatherDisplay TextView declaration

//  TODO (11) Declare TextViews for the date, description, high, low, humidity, wind, and pressure
    private TextView mDateTextView;
    private TextView mDescriptionTextView;
    private TextView mHighTempTextView;
    private TextView mLowTempTextView;
    private TextView mHumidityTextView;
    private TextView mPressureTextView;
    private TextView mWindTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      TODO (12) Remove mWeatherDisplay TextView
//      TODO (13) Find each of the TextViews by ID
        mDateTextView = (TextView) findViewById(R.id.tv_date);
        mDescriptionTextView = (TextView) findViewById(R.id.tv_weather_description);
        mHighTempTextView = (TextView) findViewById(R.id.tv_high_temperature);
        mLowTempTextView = (TextView) findViewById(R.id.tv_low_temperature);
        mHumidityTextView = (TextView) findViewById(R.id.tv_humidity);
        mPressureTextView = (TextView) findViewById(R.id.tv_pressure);
        mWindTextView = (TextView) findViewById(R.id.tv_wind);

//      TODO (14) Remove the code that checks for extra text
//      TODO (16) Use getData to get a reference to the URI passed with this Activity's Intent
        mUri = getIntent().getData();
        if (mUri == null) {
            throw new NullPointerException("Invalid uri");
        }
//      TODO (17) Throw a NullPointerException if that URI is null
//      TODO (35) Initialize the loader for DetailActivity
        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Get the ID of the clicked item */
        int id = item.getItemId();

        /* Settings menu item clicked */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item clicked */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

//  TODO (22) Override onCreateLoader
//          TODO (23) If the loader requested is our detail loader, return the appropriate CursorLoader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (id == ID_DETAIL_LOADER) {
            String[] date = new String[] {mUri.getLastPathSegment()};
            return new CursorLoader(
                    this,
                    mUri,
                    columns,
                    null,
                    null,
                    null);

        }
        else {
            throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

//  TODO (24) Override onLoadFinished
//      TODO (25) Check before doing anything that the Cursor has valid data
//      TODO (26) Display a readable data string
//      TODO (27) Display the weather description (using SunshineWeatherUtils)
//      TODO (28) Display the high temperature
//      TODO (29) Display the low temperature
//      TODO (30) Display the humidity
//      TODO (31) Display the wind speed and direction
//      TODO (32) Display the pressure
//      TODO (33) Store a forecast summary in mForecastSummary
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* We have valid data, continue on to bind the data to the UI */
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            /* No data to display, simply return and do nothing */
            return;
        }
        String date = SunshineDateUtils.getFriendlyDateString(this, data.getLong(dateColumnIndex), true);
        String description = SunshineWeatherUtils.getStringForWeatherCondition(this, data.getInt(weatherIdColumnIndex));
        String highTemperature = SunshineWeatherUtils.formatTemperature(this, data.getDouble(minTempColumnIndex));
        String minTemperature = SunshineWeatherUtils.formatTemperature(this, data.getDouble(maxTempColumnIndex));
        String humidity = data.getInt(humidityColumnIndex) + " %";
        String pressure = data.getInt(pressureColumnIndex) + " hPA";
        String wind = SunshineWeatherUtils.
                getFormattedWind(this, data.getFloat(windColumnIndex), data.getFloat(degreesColumnIndex));
        mDateTextView.setText(date);
        mDescriptionTextView.setText(description);
        mHighTempTextView.setText(highTemperature);
        mLowTempTextView.setText(minTemperature);
        mHumidityTextView.setText(humidity);
        mPressureTextView.setText(pressure);
        mWindTextView.setText(wind);
        mForecastSummary =
                date +
                " - " + description +
                " - " + highTemperature +
                "/" + minTemperature +
                " - " + humidity +
                " - " + pressure +
                " - " + wind;
    }

//  TODO (34) Override onLoaderReset, but don't do anything in it yet
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}