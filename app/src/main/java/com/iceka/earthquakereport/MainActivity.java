package com.iceka.earthquakereport;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<Earthquake> earthquakes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_earthquake);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        retrieveData();

        AndroidNetworking.initialize(getApplicationContext());


    }

    private void retrieveData() {
        AndroidNetworking.get(Constant.BASE_URL)
                .addQueryParameter("format", "geojson")
                .addQueryParameter("minmagnitude", "6")
                .addQueryParameter("limit", "10")
                .setTag("TESTING")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray features = response.getJSONArray("features");
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
                            EarthquakeAdapter adapter = new EarthquakeAdapter(getApplicationContext(), earthquakes);
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
}
