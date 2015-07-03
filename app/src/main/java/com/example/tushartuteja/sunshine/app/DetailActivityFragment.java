package com.example.tushartuteja.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.ShareActionProvider;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tushartuteja.sunshine.app.data.WeatherContract;

import org.w3c.dom.Text;

import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }

    private static final int FORECAST_LOADER = 0;
    private String mLocation;

    private String mForecastrStr;
    private String date;
    private SimpleCursorAdapter mForecastAdapter;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };


    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        // The SimpleCursorAdapter will take data from the database through the
        // Loader and use it to populate the ListView it's attached to.
        mForecastAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_forecast,
                null,
                // the column names to use to fill the textviews
                new String[]{WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
                },
                // the textviews to fill with the data pulled from the columns above
                new int[]{R.id.list_item_date_textview,
                        R.id.list_item_forecast_textview,
                        R.id.list_item_high_textview,
                        R.id.list_item_low_textview
                },
                0
        );

        mForecastAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                boolean isMetric = Utility.isMetric(getActivity());
                switch (columnIndex) {
                    case COL_WEATHER_MAX_TEMP:
                    case COL_WEATHER_MIN_TEMP: {
                        // we have to do some formatting and possibly a conversion
                        ((TextView) view).setText(Utility.formatTemperature(
                                cursor.getDouble(columnIndex), isMetric));
                        return true;
                    }
                    case COL_WEATHER_DATE: {
                        String dateString = cursor.getString(columnIndex);
                        TextView dateView = (TextView) view;
                        dateView.setText(Utility.formatDate(dateString));
                        return true;
                    }
                }
                return false;
            }
        });

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        Intent intent = getActivity().getIntent();

        date = intent.getStringExtra(intent.EXTRA_TEXT);






        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mShareActionProvider != null ){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent(){
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastrStr);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                mLocation, date);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {

        TextView date = (TextView)getActivity().findViewById(R.id.detail_date_textview);
        TextView desc = (TextView)getActivity().findViewById(R.id.detail_forecast_textview);
        TextView min = (TextView)getActivity().findViewById(R.id.detail_low_textview);
        TextView max = (TextView)getActivity().findViewById(R.id.detail_high_textview);


        if (data.moveToFirst()){
            date.setText(data.getString(COL_WEATHER_DATE));
            desc.setText(data.getString(COL_WEATHER_DESC));
            min.setText(data.getString(COL_WEATHER_MIN_TEMP));
            max.setText(data.getString(COL_WEATHER_MAX_TEMP));


        }



    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
