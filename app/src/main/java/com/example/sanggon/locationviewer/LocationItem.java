package com.example.sanggon.locationviewer;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class LocationItem {
    private String title;
    private String description;
    private String address;
    private HashMap<String, String> hours;
    private Bitmap image;
    private String url;

    public LocationItem(String title, String description, String address,
                        HashMap<String, String> hours, Bitmap image, String url) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.hours = hours;
        this.image = image;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public HashMap<String, String> getHours() {
        return hours;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}
