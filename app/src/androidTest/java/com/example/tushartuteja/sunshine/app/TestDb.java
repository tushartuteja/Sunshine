package com.example.tushartuteja.sunshine.app;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.tushartuteja.sunshine.app.data.WeatherContract;
import com.example.tushartuteja.sunshine.app.data.WeatherDbHelper;

/**
 * Created by tushartuteja on 26/06/15.
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG ="TEST-DB";


    public void testCreateDb() throws Throwable{
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb(){
        String testName = "North Pole";
        String testLocationSetting = "99705";
        Double testLatitude = 64.772;
        Double testLongitude = -147.355;

        WeatherDbHelper weatherDbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = weatherDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(WeatherContract.LocationEntry.COLUMN_CITY_NAME, testName);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LAT, testLatitude);
        contentValues.put(WeatherContract.LocationEntry.COLUMN_COORD_LONG, testLongitude);

        long locationRowEntry = db.insert(WeatherContract.LocationEntry.TABLE_NAME,null, contentValues);

        assertTrue(locationRowEntry != -1);

        Log.d(LOG_TAG, "Insert Row Db: " + locationRowEntry);


        String[] columns = {
                WeatherContract.LocationEntry._ID,
                WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
                WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                WeatherContract.LocationEntry.COLUMN_COORD_LAT,
                WeatherContract.LocationEntry.COLUMN_COORD_LONG

        };


        Cursor cursor = db.query(WeatherContract.LocationEntry.TABLE_NAME, columns,null,null,null,null,null);

        if (cursor.moveToFirst()){
            int locationIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING);
            String location = cursor.getString(locationIndex);

            int latIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LAT);
            double latitude = cursor.getDouble(latIndex);

            int longIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_COORD_LONG);
            double longitude = cursor.getDouble(longIndex);

            int nameIndex = cursor.getColumnIndex(WeatherContract.LocationEntry.COLUMN_CITY_NAME);
            String name = cursor.getString(nameIndex);

            assertEquals(location, testLocationSetting);
            assertEquals(testName, name);
            assertEquals(testLatitude, latitude);
            assertEquals(testLongitude, longitude);


        }else{
            fail("No values returned");
        }
    }
}
