package com.iceka.earthquakereport.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;
import com.iceka.earthquakereport.R;
import com.iceka.earthquakereport.models.Earthquake;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class EarthquakeAdapter extends RecyclerView.Adapter<EarthquakeAdapter.MyViewHolder> {

    private static final String LOCATION_SEPARATOR = " of ";

    private View.OnClickListener onItemClickListener;
    private List<Earthquake> earthquakeList;
    private Context mContext;

    public EarthquakeAdapter(Context context, List<Earthquake> earthquakes) {
        this.mContext = context;
        this.earthquakeList = earthquakes;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_earthquake_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Earthquake earthquake = earthquakeList.get(position);

        holder.mMagnitude.setText(String.valueOf(earthquake.getMagnitude()));

        GradientDrawable magnitudeCircle = (GradientDrawable) holder.mMagnitude.getBackground();
        int magnitudeColor = getMagnitudeColor(earthquake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);

        String originalLocation = earthquake.getLocation();
        String locationOffset;
        String primaryLocation;
        if (originalLocation.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalLocation.split(LOCATION_SEPARATOR);

            locationOffset = parts[0] + LOCATION_SEPARATOR;
            primaryLocation = parts[1];
        } else {
            locationOffset = "Near the ";
            primaryLocation = originalLocation;
        }

        holder.mLocationOffset.setText(locationOffset);
        holder.mPrimaryLocation.setText(primaryLocation);

        Date dateObject = new Date(earthquake.getTime());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd LLL yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(dateObject);

        holder.mDate.setText(formattedDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        String formattedTime = timeFormat.format(dateObject);

        holder.mTime.setText(formattedTime);
    }

    @Override
    public int getItemCount() {
        return earthquakeList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mMagnitude;
        TextView mLocationOffset;
        TextView mPrimaryLocation;
        TextView mDate;
        TextView mTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mMagnitude = itemView.findViewById(R.id.magnitude);
            mLocationOffset = itemView.findViewById(R.id.location_offset);
            mPrimaryLocation = itemView.findViewById(R.id.primary_location);
            mDate = itemView.findViewById(R.id.date);
            mTime = itemView.findViewById(R.id.time);

            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }

    private int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) Math.floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(mContext, magnitudeColorResourceId);
    }

    public void setItemClickListener(View.OnClickListener clickListener) {
        onItemClickListener = clickListener;
    }
}
