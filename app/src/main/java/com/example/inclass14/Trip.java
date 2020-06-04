//InClass 14
//File Name: Group12_Inclass14
//Sanika Pol
//Snehal Kekane
package com.example.inclass14;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Trip implements Serializable {
    private double latitude,longitude;
    private String name,description, id;
    private ArrayList<Place> places;

    public Trip() {
        this.places = new ArrayList<Place>();
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Place> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id='" + id + '\'' +
                ", places=" + places +
                '}';
    }

    public HashMap toHashMap(){
        HashMap<String, Object> tripMap = new HashMap<>();
        tripMap.put("id",this.id);
        tripMap.put("name",this.name);
        tripMap.put("description",this.description);
        tripMap.put("latitude",this.latitude);
        tripMap.put("longitude",this.longitude);
        tripMap.put("places",this.places);
        return tripMap;
    }
}
