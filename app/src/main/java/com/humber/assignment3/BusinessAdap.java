package com.humber.assignment3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BusinessAdap extends RecyclerView.Adapter<BusinessAdap.ViewHolder> {

    private ArrayList<Business> listOfBusiness;
    private LayoutInflater layoutInflater;
    public Context ctx;


    public BusinessAdap(ArrayList<Business> listOfBusiness,  Context ctx) {
        this.listOfBusiness = listOfBusiness;
        this.layoutInflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public BusinessAdap.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.business,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessAdap.ViewHolder holder, int position) {

        final Business business = listOfBusiness.get(position);

        holder.businessName.setText(business.getName());
        holder.businessCats.setText(business.getDescription());
        holder.businessImage.setImageBitmap(business.getImage());
        holder.businessRating.setText(String.valueOf(business.getRating()));
        holder.businessUrl.setText(business.getUrl());
        holder.businessUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(business.getUrl()));
                ctx.startActivity(i);
            }
        });
        holder.businessReviews.setText(String.valueOf(business.getViews()));
        holder.businessLocation.setText(business.getLocation());
    }

    @Override
    public int getItemCount() {
        return listOfBusiness.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView businessImage;
        TextView businessName;
        TextView businessCats;
        TextView businessLocation;
        TextView businessRating;
        TextView businessUrl;
        TextView businessReviews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            businessImage = itemView.findViewById(R.id.businessImage);
            businessName = itemView.findViewById(R.id.businessNameText);
            businessCats = itemView.findViewById(R.id.businessCategoriesText);
            businessLocation = itemView.findViewById(R.id.businessLocationText);
            businessRating = itemView.findViewById(R.id.businessRating);
            businessUrl = itemView.findViewById(R.id.businessURL);
            businessReviews = itemView.findViewById(R.id.businessReviewCount);
        }
    }
    public interface OnBusinessListener{
        void onBusinessClick(int position);
    }
}
