package com.humber.assignment3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BusinessAdap.OnBusinessListener {

    LocationListener locationListener;
    LocationManager locationManager;
    //https://api.yelp.com/v3/businesses/search?latitude=37.786882&longitude=-122.399972
    private static final String URI = "https://api.yelp.com/v3";
    //private static final String URL = "/businesses/north-india-restaurant-san-francisco";
    private static final String SEARCH = "/businesses/search?term=delis&latitude=";

    //Bearer WvsBvmyE-NXas0vT3WC77qWJSdD2aobeFCpopzZivDeQWpX5YgfAYkHYrDRxAdoOwfouQqRdXDg0bFyFv6RSBIU4nFwg6UpTk53uo8T1PNi-6Loov-n0l5wmDkqKXnYx
    private static final String API_KEY = "Bearer WvsBvmyE-NXas0vT3WC77qWJSdD2aobeFCpopzZivDeQWpX5YgfAYkHYrDRxAdoOwfouQqRdXDg0bFyFv6RSBIU4nFwg6UpTk53uo8T1PNi-6Loov-n0l5wmDkqKXnYx";
    LatLng currentLoc;
    RecyclerView businessRecycler;
    ArrayList<Business> listOfBusiness;
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listOfBusiness = new ArrayList<>();
        businessRecycler = findViewById(R.id.businessRecycler);
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE );


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLoc = new LatLng(location.getLatitude(),location.getLongitude());
                Toast.makeText(MainActivity.this, "Location changed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if(Build.VERSION.SDK_INT < 23){
            //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50 ,20,locationListener); //deprecated and unnecessary
        }else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60, 20, locationListener);
                //locationManager.requestLocationUpdates();
                //Location location = locationManager.get
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                currentLoc = new LatLng(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
            }
        }

        SingletonRq requestQueueSingleton = SingletonRq.getInstance(this);
        queue = requestQueueSingleton.getRequestQueue();
        getBusinessData(queue);


    }

    private boolean getBusinessData(RequestQueue q) {
        ArrayList<Business> temp = new ArrayList<>();
       // String business_api_url = URI + URL;
        Log.d("syDebug", "LatLng: " + currentLoc.latitude+"," + currentLoc.longitude);
        String search_api_url = URI + SEARCH + currentLoc.latitude + "&longitude=" + currentLoc.longitude;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, search_api_url,null, new Response.Listener<JSONObject>()
        {
                    @Override
                    public void onResponse(JSONObject response) {
                        final Business business = new Business();
                        try {
                            JSONArray jsonArray = response.getJSONArray("businesses");
                            for(int i = 0; i < jsonArray.length(); i++){

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                business.setName(jsonObject.getString("name"));
                                Log.d("test","Business name: " + business.getName());

                                String imageUrl = jsonObject.getString("image_url");




//                                java.net.URL url = null;
//                                try {
//                                    url = new URL(imageUrl);
//                                } catch (MalformedURLException e) {
//                                    e.printStackTrace();
//                                }
//
//                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                                business.setImage(bmp);
//                                GetImage image = new GetImage(imageUrl);
//                                image.doInBackground();
//                                business.setImage(image.bitmap);

                                Glide.with(MainActivity.this).load(imageUrl).override(200,200).placeholder(new ColorDrawable(Color.BLACK)).
                                        into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        BitmapDrawable bitmapDrawable = (BitmapDrawable)resource;
                                        business.setImage(bitmapDrawable.getBitmap());
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {

                                    }
                                });
//                                Glide.with(MainActivity.this).asBitmap().load(imageUrl);
//                                Glide.with(MainActivity.this).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
//                                    @Override
//                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                        business.setImage(resource);
//                                    }
//
//                                    @Override
//                                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                                    }
//                                });
                                //business.setImage(new DownloadImageTask().doInBackground(imageUrl));


                                JSONObject location = jsonObject.getJSONObject("location");
                                business.setLocation(location.getString("address1"));
                                business.setViews(jsonObject.getLong("review_count"));
                                JSONArray categories = jsonObject.getJSONArray("categories");
                                for(int j=0; j<categories.length();j++){
                                    JSONObject aliasTitle = categories.getJSONObject(j);
                                    business.setDescription(aliasTitle.getString("title"));
                                }
                                business.setRating(jsonObject.getDouble("rating"));
                                addBusiness(business);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        BusinessAdap businessAdapter = new BusinessAdap(getBusinessList(), getApplicationContext());
                        businessRecycler.setAdapter(businessAdapter);
                        businessRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }
        ){
            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", API_KEY);
                return headers;
            }
        };

        q.add(jsonObjectRequest);
        return true;
    }

    private ArrayList<Business> getBusinessList() {
        return listOfBusiness;
    }

    private void addBusiness(Business b) {
        listOfBusiness.add(b);
    }

    @Override
    public void onBusinessClick(int position) {
        Intent intent = new Intent(this, BusinessActivity.class);
        startActivity(intent);
    }

    @GlideModule
    public class GlideApp extends AppGlideModule {

    }

//    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
////        ImageView bmImage;
//
//        public DownloadImageTask() {
//        }
//
//        protected Bitmap doInBackground(String... urls) {
//            String urldisplay = urls[0];
//            Bitmap bitmap = null;
//            try {
//                InputStream in = new java.net.URL(urldisplay).openStream();
//                bitmap = BitmapFactory.decodeStream(in);
//            } catch (Exception e) {
//                Log.e("Error", e.getMessage()+" didn't work, this is the image URL: " + urldisplay);
//                e.printStackTrace();
//            }
//            return bitmap;
//        }

//        protected void onPostExecute(Bitmap result) {
//            bmImage.setImageBitmap(result);
//        }
//    }

//    static class GetImage extends AsyncTask<String, Void, Bitmap> {
//        String url;
//        Bitmap bitmap;
//        public GetImage(String url){
//            this.url = url;
//        }
//        @Override
//        protected Bitmap doInBackground(String... url) {
//            String stringUrl = this.url;
//            bitmap = null;
//            InputStream inputStream;
//            try {
//                inputStream = new java.net.URL(stringUrl).openStream();
//                bitmap = BitmapFactory.decodeStream(inputStream);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//        @Override
//        protected void onPostExecute(Bitmap bitmap){
//            super.onPostExecute(bitmap);
//        }
//    }

}
