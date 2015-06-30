package com.example.tushartuteja.sunshine.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.tushartuteja.sunshine.app.data.WeatherContract;
import com.example.tushartuteja.sunshine.app.data.WeatherDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by tushartuteja on 26/06/15.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG ="TEST-DB";
    public static String cityName = "North Pole";


    public void testCreateDb() throws Throwable{
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public ContentValues getLocationContentValues(){
        String testLocationSetting = "99705";
        Double testLatitude = 64.772;
        Double testLongitude = -147.355;

        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, cityName);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return contentValues;

    }

    public ContentValues getWeatherContentValues(long locationRowId){
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, 1.4);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP, 1.5);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP, 1.6);
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherContract.WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }
    public static void validateCursor(ContentValues values, Cursor valueCursor){

        Set<Map.Entry<String, Object>> valueSet = values.valueSet();

        for(Map.Entry<String, Object> entry: valueSet){

            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);



            String expectedValue = entry.getValue().toString();


            assertFalse(-1 == idx);
            assertEquals(expectedValue, valueCursor.getString(idx));



        }

    }


    public void testInsertReadDb(){


        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = weatherDbHelper.getWritableDatabase();

        ContentValues locationContentValues = getLocationContentValues();


        long locationRowEntry = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, locationContentValues);

        assertTrue(locationRowEntry != -1);

        Log.d(LOG_TAG, "Insert Row Db: " + locationRowEntry);




        Cursor locationCursor = db.query(WeatherContract.LocationEntry.TABLE_NAME, null,null,null,null,null,null);

        if (locationCursor.moveToFirst()){
            validateCursor(locationContentValues, locationCursor);

            ContentValues weatherContentValues = getWeatherContentValues(locationRowEntry);


            long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherContentValues);
            assertTrue(weatherRowId != -1);

            Cursor weatherCursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME, null,null,null,null,null,null);
            weatherCursor.moveToFirst();
            validateCursor(weatherContentValues, weatherCursor);


        }else{
            fail("No values returned");
        }

    }
}
