package com.iceka.earthquakereport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.iceka.earthquakereport.adapters.EarthquakeAdapter;
import com.iceka.earthquakereport.models.Earthquake;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView mRecyclerView;
    private List<Earthquake> earthquakes = new ArrayList<>();
    private EarthquakeAdapter adapter;
    private TextView mEmptyEarthquake;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_earthquake);
        mEmptyEarthquake = findViewById(R.id.empty_view);
        mLoadingIndicator = findViewById(R.id.loading_indicator);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        AndroidNetworking.initialize(getApplicationContext());

        retrieveData();
    }

    private void retrieveData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        AndroidNetworking.get(Constant.BASE_URL)
                .addQueryParameter("format", "geojson")
                .addQueryParameter("limit", "10")
                .addQueryParameter("orderby", orderBy)
                .addQueryParameter("minmagnitude", minMagnitude)
                .setTag("TESTING")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
                            if (features.length() > 0) {
                                for (int i = 0; i < features.length(); i++) {
                                    JSONObject current = features.getJSONObject(i);
                                    JSONObject properties = current.getJSONObject("properties");
                                    earthquakes.add(new Earthquake(
                                            properties.getDouble("mag"),
                                            properties.getString("place"),
                                            properties.getLong("time"),
                                            properties.getString("url")
                                    ));
                                }
                                mEmptyEarthquake.setVisibility(View.GONE);
                            } else {
                                mEmptyEarthquake.setVisibility(View.VISIBLE);
                                mEmptyEarthquake.setText("Tidak ada Data");
                            }
                            mLoadingIndicator.setVisibility(View.GONE);
                            adapter = new EarthquakeAdapter(getApplicationContext(), earthquakes);
                            mRecyclerView.setAdapter(adapter);
                            adapter.setItemClickListener(onItemClickListener);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private View.OnClickListener onItemClickListener = view -> {
        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
        int position = viewHolder.getAdapterPosition();

        Earthquake earthquake = earthquakes.get(position);

        Toast.makeText(MainActivity.this, "You Clicked: " + earthquake.getMagnitude(), Toast.LENGTH_SHORT).show();
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_settings) {
            startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_min_magnitude_key)) || key.equals(getString(R.string.settings_order_by_key))) {
            earthquakes.clear();

            mEmptyEarthquake.setVisibility(View.GONE);
            mLoadingIndicator.setVisibility(View.VISIBLE);

        }
    }

}
