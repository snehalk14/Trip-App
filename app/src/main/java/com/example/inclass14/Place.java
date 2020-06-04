//InClass 14
//File Name: Group12_Inclass14
//Sanika Pol
//Snehal Kekane
package com.example.inclass14;

import androidx.annotation.Nullable;

public class Place {
    private double latitude,longitude;
    private String icon,name,tripDesc;

    public Place() {
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTripDesc() {
        return tripDesc;
    }

    public void setTripDesc(String tripDesc) {
        this.tripDesc = tripDesc;
    }

    @Override
    public String toString() {
        return "Place{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", tripDesc='" + tripDesc + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Place p = (Place) obj;
        if(this.getName().equals(p.getName()))
            return true;
        return  false;
    }
}
