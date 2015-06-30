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
public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG ="TEST-PROVIDER";
    public static String cityName = "North Pole";


    public void testDeleteDb() throws Throwable{
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);

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

    public void testInsertReadProvider(){


        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = weatherDbHelper.getWritableDatabase();

        ContentValues locationContentValues = getLocationContentValues();


        long locationRowEntry = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, locationContentValues);

        assertTrue(locationRowEntry != -1);

        Log.d(LOG_TAG, "Insert Row Db: " + locationRowEntry);

        Log.d(LOG_TAG, "Insert Row Db: " + locationRowEntry);





        Cursor cursor = mContext.getContentResolver().query(WeatherContract.LocationEntry.CONTENT_URI, null,null,null,null);

        if (cursor.moveToFirst()){
         validateCursor(locationContentValues,cursor);



        }else{
            fail("No values returned");
        }

        cursor = mContext.getContentResolver().query(WeatherContract.LocationEntry.buildLocationUri(locationRowEntry), null, null, null, null);

        if (cursor.moveToFirst()){
           validateCursor(locationContentValues,cursor);


        }else{
            fail("No values returned");
        }



        ContentValues weatherContentValues = getWeatherContentValues(locationRowEntry);


        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, weatherContentValues);
        assertTrue(weatherRowId != -1);

        Cursor weatherCursor = mContext.getContentResolver().query(WeatherContract.WeatherEntry.CONTENT_URI, null,null,null,null);
        weatherCursor.moveToFirst();
        validateCursor(weatherContentValues, weatherCursor);



    }


    public void testGetType(){
        String type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.CONTENT_URI);
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocation(testLocation));
        assertEquals(WeatherContract.WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        type = mContext.getContentResolver().getType(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(testLocation,testDate));
        assertEquals(WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE, type);

        type = mContext.getContentResolver().getType(WeatherContract.LocationEntry.CONTENT_URI);
        assertEquals(WeatherContract.LocationEntry.CONTENT_TYPE, type);

        type = mContext.getContentResolver( ).getType(WeatherContract.LocationEntry.buildLocationUri(1L));
        assertEquals(WeatherContract.LocationEntry.CONTENT_ITEM_TYPE, type);

    }
}
