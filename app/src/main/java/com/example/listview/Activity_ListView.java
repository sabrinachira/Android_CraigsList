package com.example.listview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Activity_ListView extends AppCompatActivity implements Spinner.OnItemSelectedListener {

    Spinner spinner;
    List<String> spinList;
    ListView my_listview;
    List<BikeData> bikeList;
    private String MYURL = "http://www.pcs.cnu.edu/~kperkins/bikes/";
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences myPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Change title to indicate sort by
        setTitle("Sort by:");

        if (!ConnectivityCheck.isNetworkReachable(this) && !ConnectivityCheck.isWifiReachable(this)) {
            clear_bike_list();
            create_no_network();
        } else {
            final AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            //listview that you will operate on
            my_listview = (ListView) findViewById(R.id.lv);
            //Listener for my_listview
            my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    BikeData selectedBike = bikeList.get(i);
                    builder.setMessage(selectedBike.toString());
                    builder.setPositiveButton("Ok", null);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        // recovering the instance state
        if (savedInstanceState != null) {
            MYURL = savedInstanceState.getString("myURL");
        }

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        setupSimpleSpinner();

        //set the listview onclick listener
        if (ConnectivityCheck.isNetworkReachable(this) || ConnectivityCheck.isWifiReachable(this)) {

            setupListViewOnClickListener();

            //TODO call a thread to get the JSON list of bikes

            setPreferenceChangeListener();
            DownloadTask DT = new DownloadTask(this);
            DT.execute(MYURL + "bikes.json"); //this has
        }

        //TODO when it returns it should process this data with bindData
    }

    /*
     * onResume is called when the application is closed and resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (MYURL != null && (ConnectivityCheck.isNetworkReachable(this) || ConnectivityCheck.isWifiReachable(this))) {
            setPreferenceChangeListener();
            DownloadTask DT = new DownloadTask(this);
            DT.execute(MYURL + "bikes.json"); //this has
            bindData(MYURL);
        } else {
        }
    }

    /*
     * onSaveInstanceState saves the current instance of MYURL and saves it for when the application is accessed again
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (!ConnectivityCheck.isNetworkReachable(this) && !ConnectivityCheck.isWifiReachable(this)) {
            create_no_network();
        } else {
            savedInstanceState.putString("myURL", this.MYURL);
        }
    }

    /*
        * setPreferenceChangeListener looks for if there is a change in the JSON info selector
        */
    public void setPreferenceChangeListener() {
        if (!ConnectivityCheck.isNetworkReachable(this) && !ConnectivityCheck.isWifiReachable(this)) {
            create_no_network();
        } else {
            myPreference = PreferenceManager.getDefaultSharedPreferences(this);
            if (listener == null) {
                listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

                        if (key.equals(getString(R.string.PREF_LIST))) {
                            clear_bike_list();
                            MYURL = myPreference.getString("PREF_LIST", MYURL);
                            DownloadTask DT = new DownloadTask(Activity_ListView.this);
                            DT.execute(MYURL + "bikes.json");
                        }
                    }
                };
            }
            myPreference.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    private void setupListViewOnClickListener() {
        //TODO you want to call my_listviews setOnItemClickListener with a new instance of android.widget.AdapterView.OnItemClickListener() {
    }

    /**
     * Takes the string of bikes, parses it using JSONHelper
     * Sets the adapter with this list using a custom row layout and an instance of the CustomAdapter
     * binds the adapter to the Listview using setAdapter
     *
     * @param JSONString complete string of all bikes
     */
    public void bindData(String JSONString) {
        if (bikeList != null) bikeList.clear();
        if (JSONString == null) {
            Context context = getApplicationContext();
            popup();
        } else {
            bikeList = JSONHelper.parseAll(JSONString);
        }
        CustomAdapter myAdapter = new CustomAdapter(this, R.layout.listview_row_layout, bikeList, MYURL);
        my_listview.setAdapter(myAdapter);

    }


    /**
     * create a data adapter to fill above spinner with choices(Company,Location and Price),
     * bind it to the spinner
     * Also create a OnItemSelectedListener for this spinner so
     * when a user clicks the spinner the list of bikes is resorted according to selection
     * dontforget to bind the listener to the spinner with setOnItemSelectedListener!
     */
    private void setupSimpleSpinner() {
        spinList = Arrays.asList(getResources().getStringArray(R.array.sortable_fields));

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, spinList));
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent myIntent_action_setting = new Intent(Activity_ListView.this, SettingsActivity.class);
                startActivity(myIntent_action_setting);
                break;
            case R.id.button:
                bikeList.clear();
                DownloadTask DT = new DownloadTask(Activity_ListView.this);
                DT.execute(MYURL + "bikes.json");

        }
        return true;
    }

    private void setMyCustomAdapter() {
        CustomAdapter myAdapter = new CustomAdapter(this, R.layout.listview_row_layout, bikeList, MYURL);
        my_listview.setAdapter(myAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        //position is the position in the spinList which contains the values in the values/array/sortable_fields
        final String sortParam = spinList.get(position);

        if (!sortParam.equals("Price") && bikeList != null) {
            Collections.sort(bikeList, new Comparator<BikeData>() {
                @Override
                public int compare(BikeData o1, BikeData o2) {
                    return o1.getTag(sortParam).compareTo(o2.getTag(sortParam));
                }
            });
            setMyCustomAdapter();
        }
        if (sortParam.equals("Price") && bikeList != null) {
            Collections.sort(bikeList, new Comparator<BikeData>() {
                @Override
                public int compare(BikeData o1, BikeData o2) {
                    return Double.valueOf(o1.getTag(sortParam)).compareTo(Double.valueOf(o2.getTag(sortParam)));
                }
            });
            setMyCustomAdapter();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void refresh(View view) {
        if (!ConnectivityCheck.isNetworkReachable(this) && !ConnectivityCheck.isWifiReachable(this)) {
            create_no_network();
        } else {
            bikeList.clear();
            DownloadTask DT = new DownloadTask(Activity_ListView.this);
            DT.execute(MYURL + "bikes.json");
            spinner.setSelection(0);
        }
    }

    private void clear_bike_list() {
        if (bikeList != null)
            bikeList.clear();
    }

    private void popup() {
        Context context = getApplicationContext();
        CharSequence text = "ERROR when connecting to: " + MYURL + " Server returned 404";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void create_no_network() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_message);
        builder.setTitle(R.string.dialog_title);
        builder.setPositiveButton("Ok", null);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}