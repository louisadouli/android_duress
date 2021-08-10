package com.simul.fragment;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simul.R;
import com.simul.activity.CommentActivity;
import com.simul.activity.MainActivity;
import com.simul.activity.StartConversationActivity;
import com.simul.activity.ViewOthersProfileActivity;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.FragmentUserMapLocationBinding;
import com.simul.utils.CommonMethods;
import com.simul.utils.MapUserListener;
import com.simul.utils.ShowProgrssBarListner;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.simul.activity.MainActivity.selection;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserMapLocationFragment extends Fragment implements LocationListener{

    private FragmentUserMapLocationBinding fragmentUserMapLocationBinding;

    private Context context;

    Location loc;
    LocationManager locationManager;

    double MyLat;
    double MyLong;

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    boolean alreadyLiked;

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    private SharedPreferences preferences;

    List<String> symptoms, allSymtoms;

    private String simul_symptoms, my_id, post_user_id, post_id, post_type, question, time_duration, number_of_comment, number_of_view, number_of_likes, total_report, profile_pic;

    private String name, display_simul, post_fav, report,question_img,city = "Not Found";

    private boolean cardView1, cardView2 = false;

    MapUserListener mapUserListener;

    ShowProgrssBarListner showProgrssBarListner;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentUserMapLocationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_map_location, container, false);

        idMapping();

        getGpsData();

        setUpSymptomsInView();

        setClickListener();

        clickValue();

        return fragmentUserMapLocationBinding.getRoot();
    }

    private void idMapping() {

        context = getContext();

        selection = 3;

        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        my_id = preferences.getString(ServerCredentials.USER_ID, null);

        //fragmentUserMapLocationBinding.ivEarth.setImage(ImageSource.resource(R.drawable.ic_map_earth));
    }

    private void clickValue() {

        mapUserListener = new MapUserListener() {
            @Override
            public void userData(String user_id, String get_post_type) {
                //Toast.makeText(context, "" + user_id, Toast.LENGTH_SHORT).show();
                post_type = get_post_type;
                if(post_type.equals("My Location") || post_type.equals("Latest")){
                    severCallForLastPostForLatestAndMyLocatios(user_id);
                }else {
                    serverCallForGetUserLastPost(user_id, post_type);
                }

            }
        };

        showProgrssBarListner = new ShowProgrssBarListner() {
            @Override
            public void progressStatus(String status) {
                if(status.equals("true")){
                    fragmentUserMapLocationBinding.pbMap.setVisibility(View.VISIBLE);
                }else {
                    fragmentUserMapLocationBinding.pbMap.setVisibility(View.GONE);
                }
            }
        };
    }

    private void severCallForLastPostForLatestAndMyLocatios(final String user_id) {

        if (CommonMethods.checkInternetConnection(getContext())) {

            fragmentUserMapLocationBinding.pbMap.setVisibility(View.VISIBLE);

            //fragmentNotificationBinding.pbNotification.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_GET_USER_LAST_POST_FROM_LOCATION_LATEST;

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentUserMapLocationBinding.pbMap.setVisibility(View.GONE);

                        // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                post_id = dataObject.getString(ServerCredentials.POST_ID);
                                post_user_id = dataObject.getString(ServerCredentials.USER_ID);
                                question = dataObject.getString(ServerCredentials.QUESTION);
                                String post_symptoms = dataObject.getString(ServerCredentials.SYMPTOMS);
                                time_duration = dataObject.getString(ServerCredentials.TIME_DURATION);
                                number_of_comment = dataObject.getString(ServerCredentials.NUM_OF_COMMENT);
                                number_of_likes = dataObject.getString(ServerCredentials.NUM_OF_LIKES);
                                number_of_view = dataObject.getString(ServerCredentials.NUM_OF_VIEW);
                                total_report = dataObject.getString("total_report");
                                profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                name = dataObject.getString(ServerCredentials.NAME);
                                display_simul = dataObject.getString(ServerCredentials.DISPLAY_SIMUL);
                                post_fav = dataObject.getString("post_fav");
                                report = dataObject.getString(ServerCredentials.REPORT);
                                if (dataObject.has("question_img")) {
                                    question_img = dataObject.getString("question_img");
                                } else {
                                    question_img = "No_Image";
                                }

                                fragmentUserMapLocationBinding.cardUserPost.setVisibility(View.VISIBLE);
                                Picasso.get().load(profile_pic).placeholder(R.drawable.ic_dummy).into(fragmentUserMapLocationBinding.ivMsgProfilePic);
                                if (display_simul.equals("Diabetes")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#0296FF"));
                                } else if (display_simul.equals("Cancer")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FFD519"));
                                } else if (display_simul.equals("Mental Health")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4CD964"));
                                } else if (display_simul.equals("Genetic Discordes")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
                                } else if (display_simul.equals("Age Associated Dieases")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4730B8"));
                                } else if (display_simul.equals("Heart Diases")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FB496D"));
                                } else if (display_simul.equals("MND")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#056CFE"));
                                } else if (display_simul.equals("Asthma")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4CD964"));
                                } else if (display_simul.equals("Trauma")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
                                } else if (display_simul.equals("Tumors")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FFD519"));
                                } else if (display_simul.equals("Obesity")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4730B8"));
                                } else if (display_simul.equals("Sexually Transmitted Diseases")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
                                } else {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
                                }
                                fragmentUserMapLocationBinding.txtPostUserName.setText(name);
                                fragmentUserMapLocationBinding.txtMsgTime.setText(time_duration);
                                fragmentUserMapLocationBinding.txtPost.setText(question);
                                fragmentUserMapLocationBinding.txtTotalComments.setText(number_of_comment);
                                fragmentUserMapLocationBinding.txtLike.setText(number_of_likes);

                                if (post_symptoms.equals("Diabetes")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#0296FF"));
                                } else if (post_symptoms.equals("Cancer")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FFD519"));
                                } else if (post_symptoms.equals("Mental Health")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4CD964"));
                                } else if (post_symptoms.equals("Genetic Discordes")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#B00296FF"));
                                } else if (post_symptoms.equals("Age Associated Dieases")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4730B8"));
                                } else if (post_symptoms.equals("Heart Diases")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FB496D"));
                                } else if (post_symptoms.equals("MND")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#056CFE"));
                                } else if (post_symptoms.equals("Asthma")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4CD964"));
                                } else if (post_symptoms.equals("Trauma")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#3C81B5"));
                                } else if (post_symptoms.equals("Tumors")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FFD519"));
                                } else if (post_symptoms.equals("Obesity")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4730B8"));
                                } else if (post_symptoms.equals("Sexually Transmitted Diseases")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
                                } else {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#BFFB496D"));
                                }

                                fragmentUserMapLocationBinding.txtHelthIsuusName.setText(post_symptoms);

                                if (post_fav.equals("1")) {
                                    alreadyLiked = true;
                                    Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                                    fragmentUserMapLocationBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                                } else {
                                    alreadyLiked = false;
                                    Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                                    fragmentUserMapLocationBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                                }

                            }
                        } else {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                    params.put(ServerCredentials.ID,my_id);
                    params.put(ServerCredentials.USER_ID, user_id);
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

    private void setClickListener() {

        fragmentUserMapLocationBinding.txtMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putBoolean("Change",true).apply();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(UserMapLocationFragment.this).attach(UserMapLocationFragment.this).commit();
                HelloGlobeFragment.mapPostTypeGet.getType("My Location",city);
            }
        });

        fragmentUserMapLocationBinding.txtLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putBoolean("Change",true).apply();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(UserMapLocationFragment.this).attach(UserMapLocationFragment.this).commit();
                HelloGlobeFragment.mapPostTypeGet.getType("Latest","Not Found");
            }
        });

        fragmentUserMapLocationBinding.ivStartConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, StartConversationActivity.class);
                startActivity(i);
            }
        });

        fragmentUserMapLocationBinding.ivMapUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HomeFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        fragmentUserMapLocationBinding.ivMsgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewOthersProfileActivity.class);
                i.putExtra(ServerCredentials.VIEW_USER_ID, post_user_id);
                context.startActivity(i);
            }
        });

        fragmentUserMapLocationBinding.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!alreadyLiked){
                    int count = Integer.valueOf(fragmentUserMapLocationBinding.txtLike.getText().toString());
                    int total = count + 1;
                    fragmentUserMapLocationBinding.txtLike.setText(""+total);
                    Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                    fragmentUserMapLocationBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    alreadyLiked = true;
                }
                serverCallForLikePostQuestion(post_id, "1");
            }
        });

        fragmentUserMapLocationBinding.txtDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alreadyLiked){
                    int count = Integer.valueOf(fragmentUserMapLocationBinding.txtLike.getText().toString());
                    int total = count - 1;
                    fragmentUserMapLocationBinding.txtLike.setText(""+total);
                    Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                    fragmentUserMapLocationBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    alreadyLiked = false;
                }else {
                    Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                    fragmentUserMapLocationBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                }
                serverCallForLikePostQuestion(post_id, "0");
            }
        });

        fragmentUserMapLocationBinding.txtTotalComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, CommentActivity.class);
                i.putExtra("POST_USER_ID", post_user_id);
                i.putExtra(ServerCredentials.POST_ID, post_id);
                i.putExtra(ServerCredentials.PROFILE_PIC, profile_pic);
                i.putExtra(ServerCredentials.NAME, name);
                i.putExtra(ServerCredentials.TIME_DURATION, time_duration);
                i.putExtra(ServerCredentials.QUESTION, question);
                i.putExtra(ServerCredentials.NUM_OF_LIKES, number_of_likes);
                i.putExtra(ServerCredentials.NUM_OF_COMMENT, number_of_comment);
                i.putExtra(ServerCredentials.NUM_OF_VIEW, number_of_view);
                i.putExtra(ServerCredentials.POST_LIKE, post_fav);
                i.putExtra("Question_img", question_img);
                i.putExtra(ServerCredentials.REPORT, report);
                i.putExtra(ServerCredentials.DISPLAY_SIMUL, display_simul);
                startActivity(i);
            }
        });

        fragmentUserMapLocationBinding.txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, CommentActivity.class);
                i.putExtra("POST_USER_ID", post_user_id);
                i.putExtra(ServerCredentials.POST_ID, post_id);
                i.putExtra(ServerCredentials.PROFILE_PIC, profile_pic);
                i.putExtra(ServerCredentials.NAME, name);
                i.putExtra(ServerCredentials.TIME_DURATION, time_duration);
                i.putExtra(ServerCredentials.QUESTION, question);
                i.putExtra(ServerCredentials.NUM_OF_LIKES, number_of_likes);
                i.putExtra(ServerCredentials.NUM_OF_COMMENT, number_of_comment);
                i.putExtra(ServerCredentials.NUM_OF_VIEW, number_of_view);
                i.putExtra(ServerCredentials.POST_LIKE, post_fav);
                i.putExtra("Question_img", question_img);
                i.putExtra(ServerCredentials.REPORT, report);
                i.putExtra(ServerCredentials.DISPLAY_SIMUL, display_simul);
                startActivity(i);
            }
        });


    }

    private void setUpSymptomsInView() {

        simul_symptoms = preferences.getString(ServerCredentials.SIMUL_SYMPTOMS, null);

        String str[] = simul_symptoms.split(",");

        symptoms = new ArrayList<String>();
        allSymtoms = new ArrayList<>();

        symptoms = Arrays.asList(str);

        allSymtoms.addAll(symptoms);

        if (!allSymtoms.contains("Diabetes")) {
            allSymtoms.add("Diabetes");
        }

        if (!allSymtoms.contains("Cancer")) {
            allSymtoms.add("Cancer");
        }

        if (!allSymtoms.contains("Mental Health")) {
            allSymtoms.add("Mental Health");
        }

        if (!allSymtoms.contains("Genetic Discordes")) {
            allSymtoms.add("Genetic Discordes");
        }

        if (!allSymtoms.contains("Age Associated Dieases")) {
            allSymtoms.add("Age Associated Dieases");
        }

        if (!allSymtoms.contains("Heart Diases")) {
            allSymtoms.add("Heart Diases");
        }

        if (!allSymtoms.contains("MND")) {
            allSymtoms.add("MND");
        }

        if (!allSymtoms.contains("Asthma")) {
            allSymtoms.add("Asthma");
        }

        if (!allSymtoms.contains("Trauma")) {
            allSymtoms.add("Trauma");
        }

        if (!allSymtoms.contains("Tumors")) {
            allSymtoms.add("Tumors");
        }

        if (!allSymtoms.contains("Obesity")) {
            allSymtoms.add("Obesity");
        }

        if (!allSymtoms.contains("Sexually Transmitted Diseases")) {
            allSymtoms.add("Sexually Transmitted Diseases");
        }


        /*symptoms.add("Diabetes");
        symptoms.add("Cancer");
        symptoms.add("Mental Health");
        symptoms.add("Genetic Discordes");
        symptoms.add("Age Associated Dieases");
        symptoms.add("Heart Diases");
        symptoms.add("MND");
        symptoms.add("Asthma");
        symptoms.add("Trauma");
        symptoms.add("Tumors");
        symptoms.add("Obesity");
        symptoms.add("Sexually Transmitted Diseases");*/

        float d = this.getResources().getDisplayMetrics().density;
        int marginStart = (int) (10 * d);
        int paddingStartEnd = (int) (30 * d);
        int paddingTopBotoom = (int) (8 * d);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(marginStart, 0, 0, 0);

        for (int i = 0; i < allSymtoms.size(); i++) {

            final TextView textView = new TextView(context);
            textView.setText(allSymtoms.get(i));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(14);

            if (allSymtoms.get(i).equals("Diabetes")) {
                textView.setBackground(getResources().getDrawable(R.drawable.diabetes_bg));
            } else if (allSymtoms.get(i).equals("Cancer")) {
                textView.setBackground(getResources().getDrawable(R.drawable.cancer_bg));
            } else if (allSymtoms.get(i).equals("Mental Health")) {
                textView.setBackground(getResources().getDrawable(R.drawable.mental_helth_bg));
            } else if (allSymtoms.get(i).equals("Genetic Discordes")) {
                textView.setBackground(getResources().getDrawable(R.drawable.genetic_discordes_bg));
            } else if (allSymtoms.get(i).equals("Age Associated Dieases")) {
                textView.setBackground(getResources().getDrawable(R.drawable.age_associated_dieases_bg));
            } else if (allSymtoms.get(i).equals("Heart Diases")) {
                textView.setBackground(getResources().getDrawable(R.drawable.heart_diases_bg));
            } else if (allSymtoms.get(i).equals("MND")) {
                textView.setBackground(getResources().getDrawable(R.drawable.mnd_bg));
            } else if (allSymtoms.get(i).equals("Asthma")) {
                textView.setBackground(getResources().getDrawable(R.drawable.asthma_bg));
            } else if (allSymtoms.get(i).equals("Trauma")) {
                textView.setBackground(getResources().getDrawable(R.drawable.trauma_bg));
            } else if (allSymtoms.get(i).equals("Tumors")) {
                textView.setBackground(getResources().getDrawable(R.drawable.tumors_bg));
            } else if (allSymtoms.get(i).equals("Obesity")) {
                textView.setBackground(getResources().getDrawable(R.drawable.obesity_bg));
            } else if (allSymtoms.get(i).equals("Sexually Transmitted Diseases")) {
                textView.setBackground(getResources().getDrawable(R.drawable.sexually_transmitted_bg));
            } else {
                textView.setBackground(getResources().getDrawable(R.drawable.sexually_transmitted_bg));
            }

            textView.setLayoutParams(params);
            textView.setPadding(paddingStartEnd, paddingTopBotoom, paddingStartEnd, paddingTopBotoom);
            textView.setTextColor(getResources().getColor(R.color.white));

            textView.setId(i);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    preferences.edit().putBoolean("Change",true).apply();
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    ft.detach(UserMapLocationFragment.this).attach(UserMapLocationFragment.this).commit();
                    HelloGlobeFragment.mapPostTypeGet.getType(allSymtoms.get(view.getId()),"Not Found");
                }
            });


            fragmentUserMapLocationBinding.layout.addView(textView);
        }


        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_type = textView.getText().toString();
                serverCallForQuestionList(post_type);
            }
        });*/

    }

    private void serverCallForGetUserLastPost(final String user_id, final String post_type) {
        if (CommonMethods.checkInternetConnection(getContext())) {

            fragmentUserMapLocationBinding.pbMap.setVisibility(View.VISIBLE);

            //fragmentNotificationBinding.pbNotification.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_GET_USER_LAST_POST;

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentUserMapLocationBinding.pbMap.setVisibility(View.GONE);

                        // fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                post_id = dataObject.getString(ServerCredentials.POST_ID);
                                post_user_id = dataObject.getString(ServerCredentials.USER_ID);
                                question = dataObject.getString(ServerCredentials.QUESTION);
                                String post_symptoms = dataObject.getString(ServerCredentials.SYMPTOMS);
                                time_duration = dataObject.getString(ServerCredentials.TIME_DURATION);
                                number_of_comment = dataObject.getString(ServerCredentials.NUM_OF_COMMENT);
                                number_of_likes = dataObject.getString(ServerCredentials.NUM_OF_LIKES);
                                number_of_view = dataObject.getString(ServerCredentials.NUM_OF_VIEW);
                                total_report = dataObject.getString("total_report");
                                profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                name = dataObject.getString(ServerCredentials.NAME);
                                display_simul = dataObject.getString(ServerCredentials.DISPLAY_SIMUL);
                                post_fav = dataObject.getString("post_fav");
                                report = dataObject.getString(ServerCredentials.REPORT);
                                if (dataObject.has("question_img")) {
                                    question_img = dataObject.getString("question_img");
                                } else {
                                    question_img = "No_Image";
                                }

                                fragmentUserMapLocationBinding.cardUserPost.setVisibility(View.VISIBLE);
                                Picasso.get().load(profile_pic).placeholder(R.drawable.ic_dummy).into(fragmentUserMapLocationBinding.ivMsgProfilePic);
                                if (display_simul.equals("Diabetes")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#0296FF"));
                                } else if (display_simul.equals("Cancer")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FFD519"));
                                } else if (display_simul.equals("Mental Health")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4CD964"));
                                } else if (display_simul.equals("Genetic Discordes")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
                                } else if (display_simul.equals("Age Associated Dieases")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4730B8"));
                                } else if (display_simul.equals("Heart Diases")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FB496D"));
                                } else if (display_simul.equals("MND")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#056CFE"));
                                } else if (display_simul.equals("Asthma")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4CD964"));
                                } else if (display_simul.equals("Trauma")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
                                } else if (display_simul.equals("Tumors")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FFD519"));
                                } else if (display_simul.equals("Obesity")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4730B8"));
                                } else if (display_simul.equals("Sexually Transmitted Diseases")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
                                } else {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
                                }
                                fragmentUserMapLocationBinding.txtPostUserName.setText(name);
                                fragmentUserMapLocationBinding.txtMsgTime.setText(time_duration);
                                fragmentUserMapLocationBinding.txtPost.setText(question);
                                fragmentUserMapLocationBinding.txtTotalComments.setText(number_of_comment);
                                fragmentUserMapLocationBinding.txtLike.setText(number_of_likes);

                                if (post_symptoms.equals("Diabetes")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#0296FF"));
                                } else if (post_symptoms.equals("Cancer")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FFD519"));
                                } else if (post_symptoms.equals("Mental Health")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4CD964"));
                                } else if (post_symptoms.equals("Genetic Discordes")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#B00296FF"));
                                } else if (post_symptoms.equals("Age Associated Dieases")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4730B8"));
                                } else if (post_symptoms.equals("Heart Diases")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FB496D"));
                                } else if (post_symptoms.equals("MND")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#056CFE"));
                                } else if (post_symptoms.equals("Asthma")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4CD964"));
                                } else if (post_symptoms.equals("Trauma")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#3C81B5"));
                                } else if (post_symptoms.equals("Tumors")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FFD519"));
                                } else if (post_symptoms.equals("Obesity")) {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4730B8"));
                                } else if (post_symptoms.equals("Sexually Transmitted Diseases")) {
                                    fragmentUserMapLocationBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
                                } else {
                                    fragmentUserMapLocationBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#BFFB496D"));
                                }

                                fragmentUserMapLocationBinding.txtHelthIsuusName.setText(post_symptoms);

                                if (post_fav.equals("1")) {
                                    Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                                    fragmentUserMapLocationBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                                } else {
                                    Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                                    fragmentUserMapLocationBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                                }

                            }
                        } else {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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
                    params.put(ServerCredentials.ID,my_id);
                    params.put(ServerCredentials.USER_ID, user_id);
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

    private void serverCallForLikePostQuestion(final String post_id, final String status) {

        if (CommonMethods.checkInternetConnection(context)) {

            fragmentUserMapLocationBinding.pbMap.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_LIKE_POST_QUE;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentUserMapLocationBinding.pbMap.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            //serverCallForGetUserLastPost(post_user_id, post_type);

                        } else {
                           /* if (status.equals("0"))
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();*/

                        }

                    } catch (JSONException e) {
                        fragmentUserMapLocationBinding.pbMap.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fragmentUserMapLocationBinding.pbMap.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, my_id);
                    params.put(ServerCredentials.POST_ID, post_id);
                    params.put(ServerCredentials.STATUS, status);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }

    }

    private void getGpsData() {

        locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }

            // get location
            getLocation();
        }

    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                        getData();
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                        getData();
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                    getData();
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
       /* fieldLatitude.setText("Latitude : "+Double.toString(loc.getLatitude()));
        fieldLongitude.setText("Longitude : "+Double.toString(loc.getLongitude()));
        fieldTime.setText("Time : "+ DateFormat.getTimeInstance().format(loc.getTime()));*/

        MyLat = loc.getLatitude();
        MyLong = loc.getLongitude();
    }

    private void getData() {

        try {

            Geocoder geo = new Geocoder(getContext().getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(MyLat, MyLong, 1);
            if (addresses.isEmpty()) {
                //fieldAddressLine.setText("Waiting for Location");
            } else {
                if (addresses.size() > 0) {
                    city = addresses.get(0).getLocality();
                    //Toast.makeText(context, city, Toast.LENGTH_SHORT).show();
                  /*  fieldCity.setText("City : "+addresses.get(0).getLocality());
                    fieldPostalCode.setText("PostalCode : "+addresses.get(0).getPostalCode());
                    fieldCountry.setText("Country : "+addresses.get(0).getCountryName());
                    fieldAddressLine.setText("Address : "+addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                   */ //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                getGpsData();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}
