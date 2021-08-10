package com.simul.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivityViewOthersProfileBinding;
import com.simul.model.UserModel;
import com.simul.utils.CommonMethods;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOthersProfileActivity extends AppCompatActivity {

    ActivityViewOthersProfileBinding activityViewOthersProfileBinding;

    Context context;

    String view_user_id, my_id;

    String userId, display_simul, chatUserId = null , report;

    SharedPreferences preferences;

    List<String> symptoms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityViewOthersProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_others_profile);

        idMapping();

        getFirebaseId();

        setClickListener();

        serverCallForViewProfile();

        serverCallForGetInsight();

        activityViewOthersProfileBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;

        view_user_id = getIntent().getStringExtra(ServerCredentials.VIEW_USER_ID);

        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        my_id = preferences.getString(ServerCredentials.USER_ID, null);

        if (my_id.equals(view_user_id)) {
            activityViewOthersProfileBinding.relMessages.setVisibility(View.GONE);
        }
    }

    private void getFirebaseId() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);

                    if (user.getUserId() != null) {
                        if (user.getUserId().equals(view_user_id)) {
                            // Toast.makeText(context, user.getId(), Toast.LENGTH_SHORT).show();
                            chatUserId = user.getUserId();
                            userId = user.getId();
                            display_simul = user.getDisplay_simul();

                        } else {
                            //Toast.makeText(context, "Not Found", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void serverCallForViewProfile() {

        if (CommonMethods.checkInternetConnection(context)) {

            activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_VIEW_PROFILE;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObject = jsonArray.getJSONObject(i);
                                String profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                Picasso.get().load(profile_pic).placeholder(getResources().getDrawable(R.drawable.ic_dummy)).into(activityViewOthersProfileBinding.ivProfilePic);

                                String name = dataObject.getString(ServerCredentials.NAME);
                                String about_self = dataObject.getString(ServerCredentials.ABOUT_SELF);
                                String carer = dataObject.getString(ServerCredentials.CARER);

                                String city = dataObject.getString(ServerCredentials.CITY);
                                String simul_symptoms = dataObject.getString(ServerCredentials.SIMUL_SYMPTOMS);
                                String display_simul = dataObject.getString(ServerCredentials.DISPLAY_SIMUL);
                                report = dataObject.getString(ServerCredentials.REPORT);

                                setImageBoder(display_simul);

                                activityViewOthersProfileBinding.txtUserName.setText(name);
                                activityViewOthersProfileBinding.txtAboutMe.setText(about_self);
                                activityViewOthersProfileBinding.txtCity.setText(city);

                                setUpSymptomsInView(simul_symptoms);

                            }
                        } else {

                        }
                    } catch (JSONException e) {
                        activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, view_user_id);
                    params.put(ServerCredentials.ID, my_id);
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

    private void setImageBoder(String display_simul) {

        if (display_simul.equals("Diabetes")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            activityViewOthersProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        }
    }

    private void setUpSymptomsInView(String simul_symptoms) {

        String str[] = simul_symptoms.split(",");

        symptoms = new ArrayList<String>();

        symptoms = Arrays.asList(str);

        float d = this.getResources().getDisplayMetrics().density;
        int marginStart = (int) (10 * d);
        int paddingStartEnd = (int) (30 * d);
        int paddingTopBotoom = (int) (8 * d);
        int marginFive = (int) (5 * d);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(marginFive, marginStart, 0, 0);

        for (int i = 0; i < symptoms.size(); i++) {

            final TextView textView = new TextView(context);
            textView.setText(symptoms.get(i));
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(14);

            if (symptoms.get(i).equals("Diabites")) {
                textView.setBackground(getResources().getDrawable(R.drawable.diabetes_bg));
            } else if (symptoms.get(i).equals("Heart Diases")) {
                textView.setBackground(getResources().getDrawable(R.drawable.heart_diases_bg));
            } else if (symptoms.get(i).equals("Mental Health")) {
                textView.setBackground(getResources().getDrawable(R.drawable.mental_helth_bg));
            } else if (symptoms.get(i).equals("Cancer")) {
                textView.setBackground(getResources().getDrawable(R.drawable.cancer_bg));
            } else {
                textView.setBackground(getResources().getDrawable(R.drawable.diabetes_bg));
            }
            textView.setLayoutParams(params);
            /*if (i > 0) {
                textView.setLayoutParams(params);
            }*/

            textView.setPadding(paddingStartEnd, paddingTopBotoom, paddingStartEnd, paddingTopBotoom);
            textView.setTextColor(getResources().getColor(R.color.white));

            textView.setId(i);

            activityViewOthersProfileBinding.layout.addView(textView);
        }
    }

    private void setClickListener() {

        activityViewOthersProfileBinding.relMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(context, ChatMessagesActivity.class);
                i.putExtra("userid", userId);
                i.putExtra(ServerCredentials.DISPLAY_SIMUL, display_simul);
                startActivity(i);

            }
        });

        activityViewOthersProfileBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        activityViewOthersProfileBinding.ivShareProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri bmpUri = getLocalBitmapUri(activityViewOthersProfileBinding.ivProfilePic);
                if (bmpUri != null) {
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Question");
                    String shareMessage = activityViewOthersProfileBinding.txtUserName.getText().toString();
                    shareMessage = shareMessage + "\n\n" + "Insights : "+activityViewOthersProfileBinding.txtTotalLike.getText().toString() + "\n\nLocation : " + activityViewOthersProfileBinding.txtCity.getText().toString() + "\n\nAbout Profile : "+activityViewOthersProfileBinding.txtAboutMe.getText().toString();
                    shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");
                    context.startActivity(Intent.createChooser(shareIntent, "Share Profile..."));
                }

            }
        });

        activityViewOthersProfileBinding.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (report.equals("0")) {
                    final Dialog dialog = new Dialog(context);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    dialog.setContentView(R.layout.dialog_report_user);

                    dialog.setTitle("Custom Dialog");

                    dialog.show();

                    Window window = dialog.getWindow();
                    //window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);

                    TextView txt_No = dialog.findViewById(R.id.txt_No);

                    TextView txt_yes = dialog.findViewById(R.id.txt_yes);

                    txt_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            /*if (chatUserId.equals(null)) {

                            } else {
                                serverCallForReportUser();
                            }*/
                            serverCallForReportUser();
                            dialog.dismiss();

                        }
                    });

                    txt_No.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }else {
                    Toast.makeText(context, "You are already report this user.", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            // Use methods on Context to access package-specific directories on external storage.
            // This way, you don't need to request external read/write permission.
            // See https://youtu.be/5xVh-7ywKpE?t=25m25s
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            // **Warning:** This will fail for API >= 24, use a FileProvider as shown below instead.
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void serverCallForReportUser() {

        if (CommonMethods.checkInternetConnection(context)) {

            activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_REPORT_USRR;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            report = "1";
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, my_id);//user_id
                    params.put("report_user_id", view_user_id);
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

    private void serverCallForGetInsight() {

        if (CommonMethods.checkInternetConnection(context)) {

            activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_GET_INSIGHT;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            String total_like = jsonObject.getString("total_like");
                            activityViewOthersProfileBinding.txtTotalLike.setVisibility(View.VISIBLE);
                            activityViewOthersProfileBinding.txtTotalLike.setText(total_like);
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            activityViewOthersProfileBinding.txtTotalLike.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityViewOthersProfileBinding.pbViewProfile.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, my_id);//user_id
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
