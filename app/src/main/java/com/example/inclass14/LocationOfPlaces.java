//InClass 14
//File Name: Group12_Inclass14
//Sanika Pol
//Snehal Kekane
package com.example.inclass14;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LocationOfPlaces extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static String TAG = "demo";
    LatLngBounds latLngBounds;
    ArrayList<Place> places;
    Trip trip;
    String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_of_places);

        if(getIntent()!=null && getIntent().getExtras()!=null){
            docId = getIntent().getExtras().getString(MainActivity.DOC_ID);

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

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
                                        Log.d(TAG,"Retrieved trip is: " + trip.toString());
                                        places = trip.getPlaces();
                                        if(places.size()!=0) {
                                            LatLngBounds.Builder latlngBuilder = getLatLngBoundsfromPlaces(trip.getPlaces());
                                            latLngBounds = latlngBuilder.build();
                                            for (Place place:places){
                                                mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLatitude(),place.getLongitude())).title(place.getName()));
                                            }
                                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds,100));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
                                        }
                                        else{
                                            LatLng tripDetails = new LatLng(trip.getLatitude(), trip.getLongitude());
                                            mMap.addMarker(new MarkerOptions().position(tripDetails).title(trip.getDescription()));
                                            mMap.moveCamera(CameraUpdateFactory.newLatLng(tripDetails));
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
        });

        /*// Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
    }

    private LatLngBounds.Builder getLatLngBoundsfromPlaces(ArrayList<Place> places){

        ArrayList<LatLng> latLngs = new ArrayList<>();

        for(Place place:places){
            LatLng latlng = new LatLng(place.getLatitude(),place.getLongitude());
            latLngs.add(latlng);
        }

        LatLngBounds.Builder latlngBuilder = new LatLngBounds.Builder();
        for(LatLng latLng:latLngs){
            latlngBuilder.include(latLng);
        }

        return latlngBuilder;

    }

}
