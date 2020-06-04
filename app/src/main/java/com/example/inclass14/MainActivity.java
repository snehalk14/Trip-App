//InClass 14
//File Name: Group12_Inclass14
//Sanika Pol
//Snehal Kekane
package com.example.inclass14;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TripAdapter.iTrip,PlacesInTripAdapter.iPlaceInTrip{

    ImageView iv_addtrip;
    private RecyclerView tripRecycler;
    private RecyclerView.Adapter mAdapter = null;
    public ArrayList<Trip> trips;
    private static String TAG = "demo";
    String docId;
    String desc;
    public static String DOC_ID = "DOC_ID";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Trips");

        iv_addtrip = findViewById(R.id.iv_addtrip);
        iv_addtrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addTrip = new Intent(MainActivity.this,AddTrip.class);
                startActivity(addTrip);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


        tripRecycler = findViewById(R.id.tripsRecycler);
        tripRecycler.setHasFixedSize(true);
        tripRecycler.setLayoutManager(new LinearLayoutManager(this));
        tripRecycler.setAdapter(mAdapter);

        trips = new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Trips")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Trip trip = document.toObject(Trip.class);
                                Log.d(TAG,"Trip from db:" + trip);
                                trips.add(trip);
                            }
                            Log.d(TAG,"Total trips : " + trips.size());
                            mAdapter = new TripAdapter(trips,MainActivity.this);
                            tripRecycler.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Exception + " + e);
                    }
                });
    }

    @Override
    public void addPlace(int position) {
        desc = trips.get(position).getDescription();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Trips")
                .whereEqualTo("description", desc)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,  "Cliclkeed on: " + document.getId() + " => " + document.getData());
                                docId = document.getId();
                                Log.d(TAG,"docId is stored: " + docId);
                                Intent addPlace = new Intent(MainActivity.this,AddPlaces.class);
                                addPlace.putExtra(DOC_ID,docId);
                                startActivity(addPlace);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    @Override
    public void displayLocations(int position) {
        desc = trips.get(position).getDescription();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Trips")
                .whereEqualTo("description", desc)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,  "Cliclkeed on: " + document.getId() + " => " + document.getData());
                                docId = document.getId();
                                Log.d(TAG,"docId is stored: " + docId);
                                Intent loc = new Intent(MainActivity.this,LocationOfPlaces.class);
                                loc.putExtra(DOC_ID,docId);
                                startActivity(loc);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void deletePlace(final int position,String desc) {
        trips.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Trips")
                .whereEqualTo("description", desc)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG,  "Trip from which the place is: " + document.getId() + " => " + document.getData());

                                Trip t = document.toObject(Trip.class);
                                ArrayList<Place> storedPlaces = t.getPlaces();
                                Place place = storedPlaces.get(position);
                                storedPlaces.remove(place);

                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference tripRef = db.collection("Trips").document(document.getId());
                                //Log.d(TAG ,"docId"  + docId);
                                tripRef
                                        .update("places", storedPlaces)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                db.collection("Trips")
                                                        .get()
                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if (task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                                        Trip trip = document.toObject(Trip.class);
                                                                        Log.d(TAG,"Trip from db:" + trip);
                                                                        trips.add(trip);
                                                                    }
                                                                    mAdapter.notifyDataSetChanged();
                                                                } else {
                                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                                }
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d(TAG, "Exception + " + e);
                                                            }
                                                        });

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
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
