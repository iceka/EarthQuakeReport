package com.iceka.earthquakereport.models;

import com.google.gson.annotations.SerializedName;

public class Earthquake {
    @SerializedName("mag")
    private double magnitude;
    @SerializedName("place")
    private String location;
    @SerializedName("time")
    private long time;
    @SerializedName("url")
    private String url;

    public Earthquake() {
    }

    public Earthquake(double magnitude, String location, long time, String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.time = time;
        this.url = url;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }
}
