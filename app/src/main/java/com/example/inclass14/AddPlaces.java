//InClass 14
//File Name: Group12_Inclass14
//Sanika Pol
//Snehal Kekane
package com.example.inclass14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class AddPlaces extends AppCompatActivity implements PlaceAdapter.iPlace {

    private String docId;
    private Trip trip;
    private static String TAG = "demo";
    private ArrayList<Place> places;
    private RecyclerView placeRecycler;
    private RecyclerView.Adapter mAdapter = null;
    private Place place;
    private String tripDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_places);
        places = new ArrayList<>();

        setTitle("Add Place");

        placeRecycler = findViewById(R.id.placesRecycler);
        placeRecycler.setHasFixedSize(true);
        placeRecycler.setLayoutManager(new LinearLayoutManager(this));
        placeRecycler.setAdapter(mAdapter);

        if (isConnected()) {
            if(getIntent()!=null && getIntent().getExtras()!=null){
                docId = getIntent().getExtras().getString(MainActivity.DOC_ID);
                Log.d(TAG,"DocId of trip is : " + docId);


                FirebaseFirestore db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("Trips").document(docId);
                docRef.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        trip = document.toObject(Trip.class);
                                        tripDesc = trip.getDescription();
                                        Log.d(TAG,"Retrieved trip is: " + trip.toString());
                                        new GetPlaceDetails().execute(trip);
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG,"Exception in getting trip details: " + e);
                            }
                        });
            }
        }
        else{
            Log.d(TAG,"Not connected");
            Toast.makeText(AddPlaces.this, "Not Connected", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !((NetworkInfo) networkInfo).isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }


    class GetPlaceDetails extends AsyncTask<Trip,Void, ArrayList<Place>>{
        @Override
        protected ArrayList<Place> doInBackground(Trip... trips) {
            HttpURLConnection connection = null;
            Trip tripToAdd = trips[0];
            String loc = tripToAdd.getLatitude() + "," + tripToAdd.getLongitude();

            try{
                String place_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +"key="
                        + getResources().getString(R.string.api_key) + "&location=" + loc + "&radius=" + 1000;
                Log.d(TAG,"place_URL " + place_URL);
                URL urlB = new URL(place_URL);

                connection = (HttpURLConnection) urlB.openConnection();
                connection.connect();

                if(connection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                    String json = IOUtils.toString(connection.getInputStream(), "UTF8");

                    JSONObject root = new JSONObject(json);
                    JSONArray results = root.getJSONArray("results");
                    for(int i=0;i<results.length();i++){
                        JSONObject location = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location");
                        Place place = new Place();
                        place.setLatitude(location.getDouble("lat"));
                        place.setLongitude(location.getDouble("lng"));
                        place.setName(results.getJSONObject(i).getString("name"));
                        place.setIcon(results.getJSONObject(i).getString("icon"));
                        place.setTripDesc(tripDesc);
                        //Log.d(TAG,"Place : " + place.toString());
                        places.add(place);
                    }
                    return places;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {
            super.onPostExecute(places);
            Log.d(TAG,"places size : " + places.size());
            mAdapter = new PlaceAdapter(places,AddPlaces.this);
            placeRecycler.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void addPlacetoTrip(int position) {
        place = places.get(position);
        Log.d(TAG,"Place " + place);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Trips").document(docId);
        docRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                Trip t = document.toObject(Trip.class);
                                ArrayList<Place> storedPlaces = t.getPlaces();
                                if(storedPlaces.contains(place)){
                                    Toast.makeText(AddPlaces.this, "Place is already added", Toast.LENGTH_SHORT).show();
                                }else {
                                    storedPlaces.add(place);
                                    Log.d(TAG,"Trip in which place is to added is: " + t.toString());

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    DocumentReference tripRef = db.collection("Trips").document(document.getId());
                                    //Log.d(TAG ,"docId"  + docId);
                                    tripRef
                                            .update("places", storedPlaces)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error updating document", e);
                                                }
                                            });
                                }

                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Exception in getting trip details: " + e);
                    }
                });



    }
}


