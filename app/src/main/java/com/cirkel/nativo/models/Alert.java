package com.cirkel.nativo.models;

import android.location.Location;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Alert {

    public String address;
    public Location location;
    public String state;

    public Alert(String address, Location location, String state) {
        this.address = address;
        this.location = location;
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("location", location);
        result.put("address", address);
        result.put("state", state);
        return result;
    }
}
