//InClass 14
//File Name: Group12_Inclass14
//Sanika Pol
//Snehal Kekane
package com.example.inclass14;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    ArrayList<Place> places;
    private static iPlace placeOps;

    public PlaceAdapter(ArrayList<Place> places, iPlace placeOps) {
        this.places = places;
        this.placeOps = placeOps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.places,parent,false);
        PlaceAdapter.ViewHolder viewHolder = new PlaceAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Place place = places.get(position);
        holder.tv_place.setText(place.getName());
        Picasso.get().load(place.getIcon()).into(holder.iv_placeIcon);

    }

    @Override
    public int getItemCount() {
        return places.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_place;
        ImageView iv_addPlace,iv_placeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_addPlace = itemView.findViewById(R.id.iv_addPlaceToTrip);
            iv_placeIcon = itemView.findViewById(R.id.iv_placeIcon);
            tv_place = itemView.findViewById(R.id.tv_place);

            iv_addPlace.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placeOps.addPlacetoTrip(getAdapterPosition());
                }
            });
        }
    }

    public interface iPlace{
        public void addPlacetoTrip(int position);
    }

}
