package com.simul.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.mousebird.maply.AttrDictionary;
import com.mousebird.maply.ComponentObject;
import com.mousebird.maply.GlobeController;
import com.mousebird.maply.GlobeMapFragment;
import com.mousebird.maply.MaplyBaseController;
import com.mousebird.maply.MarkerInfo;
import com.mousebird.maply.Point2d;
import com.mousebird.maply.QuadImageTileLayer;
import com.mousebird.maply.RemoteTileInfo;
import com.mousebird.maply.RemoteTileSource;
import com.mousebird.maply.ScreenMarker;
import com.mousebird.maply.SelectedObject;
import com.mousebird.maply.SphericalMercatorCoordSystem;
import com.mousebird.maply.VectorObject;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.utils.CommonMethods;
import com.simul.utils.MapPostTypeGet;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class HelloGlobeFragment extends GlobeMapFragment {

    SharedPreferences preferences;

    String user_id, post_type = "Latest";

    String myCity;

    boolean change = false;

    public static MapPostTypeGet mapPostTypeGet;

    MarkerProperties properties;

    ArrayList<String> userIdList, profilePicList;
    ArrayList<Double> lat, lon;
    ArrayList<String> displaySymtomsList;

    ArrayList<Bitmap> profileList;

    Bitmap bitmap;

    Location loc;
    LocationManager locationManager;

    double MyLat;
    double MyLong;

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle inState) {
        super.onCreateView(inflater, container, inState);

        preferences = getContext().getSharedPreferences(getContext().getPackageName(), Context.MODE_PRIVATE);

        user_id = preferences.getString(ServerCredentials.USER_ID, null);

        preferences.edit().putString("MND", "MND").apply();

        userIdList = new ArrayList<>();
        profilePicList = new ArrayList<>();
        profileList = new ArrayList<>();
        lat = new ArrayList<>();
        lon = new ArrayList<>();
        displaySymtomsList = new ArrayList<>();

        mapPostTypeGet = new MapPostTypeGet() {
            @Override
            public void getType(String get_post_type, String city) {
                post_type = get_post_type;
                if (post_type.equals("My Location")) {
                    if (city.equals("Not Found")) {
                        Toast.makeText(getContext(), "Please enable gps !!!", Toast.LENGTH_SHORT).show();
                    } else {
                        myCity = city;
                        change = true;
                        serverCallForMapLocationData();
                    }

                } else if (post_type.equals("Latest")) {
                    change = true;
                    serverCallForGetMapLatestPost();
                } else {

                    // post_type = preferences.getString("MND", null);
                    change = true;
                    serverCallForGetUserData();
                }
                //Toast.makeText(getContext(), post_type, Toast.LENGTH_SHORT).show();
            }
        };

        boolean cha = preferences.getBoolean("Change", false);

        if (!cha) {
            serverCallForGetMapLatestPost();
        }


        // Do app specific setup logic.

        return baseControl.getContentView();
    }

    private void serverCallForGetMapLatestPost() {

        displaySymtomsList.clear();

        final ProgressDialog progress = new ProgressDialog(getContext(), R.style.MyTheme);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.show();

        if (CommonMethods.checkInternetConnection(getContext())) {

            lat.clear();
            lon.clear();
            userIdList.clear();
            profilePicList.clear();
            profileList.clear();

            String url = ServerUrls.SIMUL_MAP_LATEST_DATA;

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        progress.dismiss();

                        // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            lat.clear();
                            lon.clear();
                            userIdList.clear();
                            profilePicList.clear();
                            profileList.clear();

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                properties = new MarkerProperties();

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                String user_id = dataObject.getString(ServerCredentials.USER_ID);
                                String profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                String city = dataObject.getString(ServerCredentials.CITY);
                                String display_simul = dataObject.getString(ServerCredentials.DISPLAY_SIMUL);
                                String f = " ";
                                if (Geocoder.isPresent()) {
                                    try {
                                        String location = city;
                                        Geocoder gc = new Geocoder(getContext());
                                        List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                                        List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                                        for (Address a : addresses) {
                                            if (a.hasLatitude() && a.hasLongitude()) {
                                                ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                                                lat.add(a.getLatitude());
                                                lon.add(a.getLongitude());
                                            }
                                        }
                                    } catch (IOException e) {
                                        // handle the exception
                                    }
                                }

                                properties.user_id = user_id;
                                properties.prfile_pic = profile_pic;
                                properties.city = city;

                                if (city.equals("Not Found")) {

                                } else {
                                    userIdList.add(user_id);
                                    profilePicList.add(profile_pic);
                                    profileList.add(bitmap);
                                    displaySymtomsList.add(display_simul);


                                }


                            }
                            insertMarkers();
                        } else {
                            showDialog();
                            insertMarkers();
                            // Toast.makeText(getContext(), "No Data For This Field.", Toast.LENGTH_SHORT).show();
                            // fragmentNotificationBinding.linMotificatioNotFound.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            });

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(getContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void serverCallForMapLocationData() {

        displaySymtomsList.clear();

        final ProgressDialog progress = new ProgressDialog(getContext(), R.style.MyTheme);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.show();

        if (CommonMethods.checkInternetConnection(getContext())) {

            lat.clear();
            lon.clear();
            userIdList.clear();
            profilePicList.clear();
            profileList.clear();

            String url = ServerUrls.SIMUL_MAP_LOACTION_DATA;

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        progress.dismiss();

                        // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            lat.clear();
                            lon.clear();
                            userIdList.clear();
                            profilePicList.clear();
                            profileList.clear();

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                properties = new MarkerProperties();

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                String user_id = dataObject.getString(ServerCredentials.USER_ID);
                                String profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                String city = dataObject.getString(ServerCredentials.CITY);
                                String display_simul = dataObject.getString(ServerCredentials.DISPLAY_SIMUL);
                                String f = "";

                                if (Geocoder.isPresent()) {
                                    try {
                                        String location = city;
                                        Geocoder gc = new Geocoder(getContext());
                                        List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                                        List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                                        for (Address a : addresses) {
                                            if (a.hasLatitude() && a.hasLongitude()) {
                                                ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                                                lat.add(a.getLatitude());
                                                lon.add(a.getLongitude());
                                            }
                                        }
                                    } catch (IOException e) {
                                        // handle the exception
                                    }
                                }

                                properties.user_id = user_id;
                                properties.prfile_pic = profile_pic;
                                properties.city = city;

                                userIdList.add(user_id);
                                profilePicList.add(profile_pic);
                                profileList.add(bitmap);
                                displaySymtomsList.add(display_simul);

                            }
                            insertMarkers();
                        } else {
                            showDialog();
                            insertMarkers();
                            // Toast.makeText(getContext(), "No Data For This Field.", Toast.LENGTH_SHORT).show();
                            // fragmentNotificationBinding.linMotificatioNotFound.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.CITY, myCity);//Cancer
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(getContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected MapDisplayType chooseDisplayType() {
        return MapDisplayType.Globe;
    }

    @Override
    protected void controlHasStarted() {
        // setup base layer tiles

        String cacheDirName = "stamen_watercolor";
        File cacheDir = new File(getActivity().getCacheDir(), cacheDirName);
        cacheDir.mkdir();
        RemoteTileSource remoteTileSource = new RemoteTileSource(new RemoteTileInfo("http://tile.stamen.com/watercolor/", "png", 0, 18));
        remoteTileSource.setCacheDir(cacheDir);
        SphericalMercatorCoordSystem coordSystem = new SphericalMercatorCoordSystem();

        // globeControl is the controller when using MapDisplayType.Globe
        // mapControl is the controller when using MapDisplayType.Map
        QuadImageTileLayer baseLayer = new QuadImageTileLayer(globeControl, coordSystem, remoteTileSource);
        baseLayer.setImageDepth(1);
        baseLayer.setSingleLevelLoading(false);
        baseLayer.setUseTargetZoomLevel(false);
        baseLayer.setCoverPoles(true);
        baseLayer.setHandleEdges(true);

        // add layer and position
        globeControl.addLayer(baseLayer);
        globeControl.animatePositionGeo(-4.6704803, 40.5023056, 1, 1.0);
        globeControl.setZoomLimits(0.0001, 1);

        // Set controller to be gesture delegate.
        // Needed to allow selection.
        globeControl.gestureDelegate = this;

        // Add GeoJSON
        /*final String url = "https://s3.amazonaws.com/whirlyglobedocs/tutorialsupport/RUS.geojson";
        GeoJsonHttpTask task = new GeoJsonHttpTask(globeControl);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);*/

        //insertSpheres();
        //insertMarkers();
        //insertLabels();
    }

    @Override
    protected void preControlCreated() {
        globeSettings.clearColor = Color.rgb(242, 242, 242);
    }

    public class MarkerProperties {
        public String user_id;
        public String prfile_pic;
        public String city;
       /* public String time;
        public String post;
        public String like;
        public String comment;*/
    }

    private void serverCallForGetUserData() {

        displaySymtomsList.clear();

        final ProgressDialog progress = new ProgressDialog(getContext(), R.style.MyTheme);
        progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progress.setCanceledOnTouchOutside(false);
        progress.setIndeterminate(true);
        progress.show();

        if (CommonMethods.checkInternetConnection(getContext())) {

            lat.clear();
            lon.clear();
            userIdList.clear();
            profilePicList.clear();
            profileList.clear();

            String url = ServerUrls.SIMUL_MAP_USER;

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        progress.dismiss();

                        // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            lat.clear();
                            lon.clear();
                            userIdList.clear();
                            profilePicList.clear();
                            profileList.clear();

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                properties = new MarkerProperties();

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                String user_id = dataObject.getString(ServerCredentials.USER_ID);
                                String profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                String city = dataObject.getString(ServerCredentials.CITY);
                                String display_simul = dataObject.getString(ServerCredentials.DISPLAY_SIMUL);
                                String f = "";

                                if (city.equals("Not Found")) {

                                } else {
                                    if (Geocoder.isPresent()) {
                                        try {
                                            String location = city;
                                            Geocoder gc = new Geocoder(getContext());
                                            List<Address> addresses = gc.getFromLocationName(location, 5); // get the found Address Objects

                                            List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                                            for (Address a : addresses) {
                                                if (a.hasLatitude() && a.hasLongitude()) {
                                                    ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                                                    lat.add(a.getLatitude());
                                                    lon.add(a.getLongitude());
                                                }
                                            }
                                        } catch (IOException e) {
                                            // handle the exception
                                        }
                                    }

                                    properties.user_id = user_id;
                                    properties.prfile_pic = profile_pic;
                                    properties.city = city;

                                    userIdList.add(user_id);
                                    profilePicList.add(profile_pic);
                                    profileList.add(bitmap);
                                    displaySymtomsList.add(display_simul);
                                }

                            }
                            insertMarkers();
                        } else {
                            userIdList.clear();
                            profilePicList.clear();
                            profileList.clear();
                            insertMarkers();

                            showDialog();
                            // Toast.makeText(getContext(), "No Data For This Field.", Toast.LENGTH_SHORT).show();
                            // fragmentNotificationBinding.linMotificatioNotFound.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.POST_TYPE, post_type);//Cancer
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(getContext(), R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog() {

        final Dialog dialog = new Dialog(getContext());

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_map_no_post);

        dialog.setCanceledOnTouchOutside(false);

        dialog.setTitle("Custom Dialog");

        dialog.show();

        Window window = dialog.getWindow();
        //window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        TextView txt_Cancel = dialog.findViewById(R.id.txt_Cancel);

        txt_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().finish();
                serverCallForGetMapLatestPost();
                dialog.dismiss();
            }
        });

    }

    public static Bitmap padBitmap(Bitmap bitmap) {
        int paddingX;
        int paddingY;

        if (bitmap.getWidth() == bitmap.getHeight()) {
            paddingX = 0;
            paddingY = 0;
        } else if (bitmap.getWidth() > bitmap.getHeight()) {
            paddingX = 0;
            paddingY = bitmap.getWidth() - bitmap.getHeight();
        } else {
            paddingX = bitmap.getHeight() - bitmap.getWidth();
            paddingY = 0;
        }

        Bitmap paddedBitmap = Bitmap.createBitmap(
                bitmap.getWidth() + paddingX,
                bitmap.getHeight() + paddingY,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF); // this represents white color
        canvas.drawBitmap(
                bitmap,
                paddingX / 2,
                paddingY / 2,
                new Paint(Paint.FILTER_BITMAP_FLAG));

        return paddedBitmap;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, String display_symptoms) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);


        if (display_symptoms.equals("Diabetes")) {
            p.setColor(Color.parseColor("#0296FF"));
        } else if (display_symptoms.equals("Cancer")) {
            p.setColor(Color.parseColor("#FFD519"));
        } else if (display_symptoms.equals("Mental Health")) {
            p.setColor(Color.parseColor("#4CD964"));
        } else if (display_symptoms.equals("Genetic Discordes")) {
            p.setColor(Color.parseColor("#B00296FF"));
        } else if (display_symptoms.equals("Age Associated Dieases")) {
            p.setColor(Color.parseColor("#4730B8"));
        } else if (display_symptoms.equals("Heart Diases")) {
            p.setColor(Color.parseColor("#FB496D"));
        } else if (display_symptoms.equals("MND")) {
            p.setColor(Color.parseColor("#056CFE"));
        } else if (display_symptoms.equals("Asthma")) {
            p.setColor(Color.parseColor("#4CD964"));
        } else if (display_symptoms.equals("Trauma")) {
            p.setColor(Color.parseColor("#3C81B5"));
        } else if (display_symptoms.equals("Tumors")) {
            p.setColor(Color.parseColor("#FFD519"));
        } else if (display_symptoms.equals("Obesity")) {
            p.setColor(Color.parseColor("#4730B8"));
        } else if (display_symptoms.equals("Sexually Transmitted Diseases")) {
            p.setColor(Color.parseColor("#BFFB496D"));
        } else {
            p.setColor(Color.parseColor("#BFFB496D"));
        }
        //  p.setColor(Color.parseColor("#FB496D"));


        p.setStrokeWidth(10);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
    }

    private void insertMarkers() {

        List<ScreenMarker> markers = new ArrayList<>();

        MarkerInfo markerInfo = new MarkerInfo();

        Point2d markerSize = new Point2d(144, 144);

        // Point2d markerSize = new Point2d(144, 144);
        // Moskow - Москва
        /*MarkerProperties properties = new MarkerProperties();
        properties.name = "Jane_plain";
        properties.time = "2h";
        properties.post = "It has survived not only five centuries, but also the leap not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.";
        properties.comment = "8.4k";
        properties.like = "76.0k";*/

        for (int i = 0; i < userIdList.size(); i++) {

            MarkerProperties properties = new MarkerProperties();
            properties.user_id = userIdList.get(i);
            final ScreenMarker moskow = new ScreenMarker();
            moskow.loc = Point2d.FromDegrees(lon.get(i), lat.get(i)); // Longitude, Latitude
            final int finalI = i;
            Picasso.get().load(profilePicList.get(i)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                  /*  bitmap = getCroppedBitmap(bitmap);
                    bitmap = padBitmap(bitmap);*/
                    bitmap = getRoundedCornerBitmap(bitmap, displaySymtomsList.get(finalI));
                    moskow.image = bitmap;

                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
            // moskow.image = bitmap;
            moskow.size = markerSize;
            moskow.selectable = true;
            moskow.userObject = properties;
            markers.add(moskow);
            if (change) {
                markers.clear();
                change = false;
                insertMarkers();
            }
        }

        int pqq = markers.size();
        int k = 0;


       /* //  Saint Petersburg - Санкт-Петербург
        // 	Saint Petersburg - Санкт-Петербург
        *//*properties = new MarkerProperties();
        properties.name = "Ravi";
        properties.time = "1h";
        properties.post = "It has survived not only five centuries, but also the leap not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.";
        properties.comment = "2.4k";
        properties.like = "66.0k";*//*
        ScreenMarker stPetersburg = new ScreenMarker();
        stPetersburg.loc = Point2d.FromDegrees(30.3, 59.95); // Longitude, Latitude
        stPetersburg.image = icon;
        stPetersburg.size = markerSize;
        stPetersburg.selectable = true;
        stPetersburg.userObject = properties;
        markers.add(stPetersburg);

        // Novosibirsk - Новосибирск
        *//*properties = new MarkerProperties();
        properties.name = "Raj";
        properties.time = "3h";
        properties.post = "It has survived not only five centuries, but also the leap not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.";
        properties.comment = "1.4k";
        properties.like = "60.0k";*//*
        ScreenMarker novosibirsk = new ScreenMarker();
        novosibirsk.loc = Point2d.FromDegrees(82.95, 55.05); // Longitude, Latitude
        novosibirsk.image = icon;
        novosibirsk.size = markerSize;
        novosibirsk.selectable = true;
        novosibirsk.userObject = properties;
        markers.add(novosibirsk);

        // Yekaterinburg - Екатеринбург
        *//*properties = new MarkerProperties();
        properties.name = "Roy";
        properties.time = "3h";
        properties.post = "It has survived not only five centuries, but also the leap not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.";
        properties.comment = "4.4k";
        properties.like = "60.0k";*//*
        ScreenMarker yekaterinburg = new ScreenMarker();
        yekaterinburg.loc = Point2d.FromDegrees(60.583333, 56.833333); // Longitude, Latitude
        yekaterinburg.image = icon;
        yekaterinburg.size = markerSize;
        yekaterinburg.selectable = true;
        yekaterinburg.userObject = properties;
        markers.add(yekaterinburg);

        // Nizhny Novgorod - Нижний Новгород
       *//* properties = new MarkerProperties();
        properties.name = "Aim";
        properties.time = "1h";
        properties.post = "It has survived not only five centuries, but also the leap not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged.";
        properties.comment = "5.4k";
        properties.like = "97.2k";*//*
        ScreenMarker nizhnyNovgorod = new ScreenMarker();
        nizhnyNovgorod.loc = Point2d.FromDegrees(72.5714, 23.0225); // Longitude, Latitude
        nizhnyNovgorod.image = icon;
        nizhnyNovgorod.size = markerSize;
      //  nizhnyNovgorod.rotation = Math.PI;
        nizhnyNovgorod.selectable = true;
        nizhnyNovgorod.userObject = properties;
        markers.add(nizhnyNovgorod);*/

        // Add your markers to the map controller.
        ComponentObject markersComponentObject = globeControl.addScreenMarkers(markers, markerInfo, MaplyBaseController.ThreadMode.ThreadAny);
        String paa = "";

    }

    @Override
    public void userDidSelect(GlobeController mapControl, SelectedObject[] selObjs, Point2d loc, Point2d screenLoc) {
        String msg = "Selected feature count: " + selObjs.length;
        MarkerProperties properties = null;
        for (SelectedObject obj : selObjs) {
            // GeoJSON
            if (obj.selObj instanceof VectorObject) {
                VectorObject vectorObject = (VectorObject) obj.selObj;
                AttrDictionary attributes = vectorObject.getAttributes();
                String adminName = attributes.getString("ADMIN");
                msg += "\nVector Object: " + adminName;
            }
            // Screen Marker
            else if (obj.selObj instanceof ScreenMarker) {
                ScreenMarker screenMarker = (ScreenMarker) obj.selObj;
                properties = (MarkerProperties) screenMarker.userObject;
                // msg += "\nScreen Marker: " + properties.city + ", " + properties.subject +", " + properties.name + ", " + properties.time +  ", " + properties.comment + ", " + properties.post + ", " + properties.like;
            }
        }
        UserMapLocationFragment parentFrag = ((UserMapLocationFragment) HelloGlobeFragment.this.getParentFragment());
        parentFrag.mapUserListener.userData(properties.user_id, post_type);
        //Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

    private Bitmap addBoderBorder(Bitmap bmp, int borderSize) {
        Bitmap bmpWithBorder = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(bmpWithBorder);
        canvas.drawColor(Color.parseColor("#FFD519"));
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return bmpWithBorder;
    }

}
