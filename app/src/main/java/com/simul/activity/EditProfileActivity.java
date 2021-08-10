package com.simul.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivityEditProfileBinding;
import com.simul.utils.CommonMethods;
import com.simul.utils.VolleyMultipartRequest;
import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.login.models.MeData;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding activityEditProfileBinding;

    Context context;

    FirebaseAuth auth;

    FirebaseUser fuser;

    boolean update_simul = false;

    SharedPreferences preferences;

    ArrayList<String> symptoms;

    DatabaseReference reference;

    String display_simul, currentUserId;

    private int RESULT_LOAD_IMAGE = 100;

    private Bitmap profileBitmap;

    boolean set = false;

    private DatabaseReference rootRef;

    StorageReference userProfileImageRef;

    boolean installed;

    String str[];

    String query = "{me{bitmoji{avatar}}}";

    String bitmapUrl;

    private Uri imageUri;

    private boolean selection, bitmoji_selection = false;

    String user_id, name, email, about_self, carer, city, simul_symptoms, profile_pic, total_like;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);

        idMapping();

        checkSnapChetInstallOrNot();

        setClickListener();

        setImageBoder();

        setUpPrefData();

        setupView();

        activityEditProfileBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        profile_pic = preferences.getString(ServerCredentials.PROFILE_PIC, null);

        Picasso.get().load(profile_pic).placeholder(R.drawable.ic_dummy).into(activityEditProfileBinding.ivProfilePic);

        userProfileImageRef = FirebaseStorage.getInstance().getReference("uploads");

        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    private void checkSnapChetInstallOrNot() {

        installed = appInstalledOrNot("com.snapchat.android");
        if (installed) {
            // Toast.makeText(context, "App is already installed on your phone", Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(context, "App is not currently installed on your phone", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    private void setImageBoder() {

        display_simul = preferences.getString(ServerCredentials.DISPLAY_SIMUL, null);
        String h = "";

        if (display_simul.equals("Diabetes")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            activityEditProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        }
    }

    private void setClickListener() {

        activityEditProfileBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        activityEditProfileBinding.txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAllFeild();
            }
        });

        activityEditProfileBinding.ivAddSymtoms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EditProfileSelectSimulActivity.class);
                startActivity(i);
                finish();
            }
        });

        activityEditProfileBinding.ivSelectImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                //editProfileImage();
                editProfileBitMoji();
            }
        });

        activityEditProfileBinding.txtLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(context);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setContentView(R.layout.dialog_logout);

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

                        FirebaseAuth.getInstance().signOut();
                        preferences.edit().putBoolean("hasLoggedIn", false).apply();
                        finishAffinity();
                        Intent i = new Intent(context, WelcomeActivity.class);
                        startActivity(i);

                    }
                });

                txt_No.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void setUpPrefData() {

        user_id = preferences.getString(ServerCredentials.USER_ID, null);
        name = preferences.getString(ServerCredentials.NAME, null);
        email = preferences.getString(ServerCredentials.EMAIL_ID, null);
        about_self = preferences.getString(ServerCredentials.ABOUT_SELF, null);
        carer = preferences.getString(ServerCredentials.CARER, null);
        city = preferences.getString(ServerCredentials.CITY, null);
        simul_symptoms = preferences.getString(ServerCredentials.SIMUL_SYMPTOMS, null);
        profile_pic = preferences.getString(ServerCredentials.PROFILE_PIC, null);
        display_simul = preferences.getString(ServerCredentials.DISPLAY_SIMUL, null);
        total_like = getIntent().getStringExtra("INSIGHT");

        activityEditProfileBinding.txtTotalInsight.setText(total_like);
        Picasso.get().load(profile_pic).placeholder(getResources().getDrawable(R.drawable.ic_main_profile)).into(activityEditProfileBinding.ivProfilePic);
        activityEditProfileBinding.txtUsernameHeading.setText(name);
        activityEditProfileBinding.txtUserName.setText(name);
        activityEditProfileBinding.txtEmailAddress.setText(email);
        activityEditProfileBinding.edtAboutMe.setText(about_self);
        activityEditProfileBinding.edtLocation.setText(city);

        str = simul_symptoms.split(",");

        symptoms = new ArrayList<>(Arrays.asList(str));
    }

    private void setupView() {

        update_simul = preferences.getBoolean("UPADATE_SIMUL", false);

        if (update_simul) {

            preferences.edit().putBoolean("UPADATE_SIMUL", false).apply();

            simul_symptoms = getIntent().getStringExtra("symptoms_string");

            display_simul = getIntent().getStringExtra(ServerCredentials.DISPLAY_SIMUL);

            str = simul_symptoms.split(",");

            symptoms = new ArrayList<>(Arrays.asList(str));

            activityEditProfileBinding.layout.removeAllViews();

            setUpSymptomsInView();
        } else {
            setUpSymptomsInView();
        }
    }


    private void setUpSymptomsInView() {

        /* simul_symptoms = preferences.getString(ServerCredentials.SIMUL_SYMPTOMS, null);*/

        // String str[] = simul_symptoms.split(",");

        /*symptoms = new ArrayList<String>();*/

        //symptoms = new ArrayList<>(Arrays.asList(str));

        /* symptoms = (ArrayList) Arrays.asList(str);*/


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
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_transh_16dp), null);
            textView.setCompoundDrawablePadding(marginStart);

            if (symptoms.get(i).equals("Diabetes")) {
                textView.setBackground(getResources().getDrawable(R.drawable.diabetes_bg));
            } else if (symptoms.get(i).equals("Cancer")) {
                textView.setBackground(getResources().getDrawable(R.drawable.cancer_bg));
            } else if (symptoms.get(i).equals("Mental Health")) {
                textView.setBackground(getResources().getDrawable(R.drawable.mental_helth_bg));
            } else if (symptoms.get(i).equals("Genetic Discordes")) {
                textView.setBackground(getResources().getDrawable(R.drawable.genetic_discordes_bg));
            } else if (symptoms.get(i).equals("Age Associated Dieases")) {
                textView.setBackground(getResources().getDrawable(R.drawable.age_associated_dieases_bg));
            } else if (symptoms.get(i).equals("Heart Diases")) {
                textView.setBackground(getResources().getDrawable(R.drawable.heart_diases_bg));
            } else if (symptoms.get(i).equals("MND")) {
                textView.setBackground(getResources().getDrawable(R.drawable.mnd_bg));
            } else if (symptoms.get(i).equals("Asthma")) {
                textView.setBackground(getResources().getDrawable(R.drawable.asthma_bg));
            } else if (symptoms.get(i).equals("Trauma")) {
                textView.setBackground(getResources().getDrawable(R.drawable.trauma_bg));
            } else if (symptoms.get(i).equals("Tumors")) {
                textView.setBackground(getResources().getDrawable(R.drawable.tumors_bg));
            } else if (symptoms.get(i).equals("Obesity")) {
                textView.setBackground(getResources().getDrawable(R.drawable.obesity_bg));
            } else if (symptoms.get(i).equals("Sexually Transmitted Diseases")) {
                textView.setBackground(getResources().getDrawable(R.drawable.sexually_transmitted_bg));
            } else {
                textView.setBackground(getResources().getDrawable(R.drawable.sexually_transmitted_bg));
            }

            textView.setLayoutParams(params);

            /*if (i > 0) {
                textView.setLayoutParams(params);
            }*/

            textView.setPadding(paddingStartEnd, paddingTopBotoom, marginStart, paddingTopBotoom);
            textView.setTextColor(getResources().getColor(R.color.white));

            textView.setId(i);

           /* textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context, ""+view.getId(), Toast.LENGTH_SHORT).show();
                    //serverCallForQuestionList(symptoms.get(view.getId()));
                }
            });*/

            textView.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_LEFT = 0;
                    final int DRAWABLE_TOP = 1;
                    final int DRAWABLE_RIGHT = 2;
                    final int DRAWABLE_BOTTOM = 3;

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (event.getRawX() >= (textView.getRight() - textView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            // your action here

                            /*String rword = symptoms.get(v.getId());

                            simul_symptoms = simul_symptoms.replace(rword + ",","");
*/
                            activityEditProfileBinding.layout.removeAllViews();
                            symptoms.remove(v.getId());
                            setUpSymptomsInView();
                            simul_symptoms = convertToString(symptoms);
                            //Toast.makeText(context, "" + simul_symptoms, Toast.LENGTH_SHORT).show();

                            return true;
                        }
                    }
                    return false;
                }
            });

            activityEditProfileBinding.layout.addView(textView);
        }


        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_type = textView.getText().toString();
                serverCallForQuestionList(post_type);
            }
        });*/


    }

    static String convertToString(ArrayList<String> numbers) {
        StringBuilder builder = new StringBuilder();
        // Append all Integers in StringBuilder to the StringBuilder.
        for (String number : numbers) {
            builder.append(number);
            builder.append(",");
        }
        // Remove last delimiter with setLength.
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    private void checkAllFeild() {

        String city = activityEditProfileBinding.edtLocation.getText().toString();
        String about_me = activityEditProfileBinding.edtAboutMe.getText().toString();
        List<Address> addresses = null;

        if (Geocoder.isPresent()) {
            try {
                String location = city;
                Geocoder gc = new Geocoder(this);
                addresses = gc.getFromLocationName(location, 5); // get the found Address Objects
                List<LatLng> ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for (Address a : addresses) {
                    if (a.hasLatitude() && a.hasLongitude()) {
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
            } catch (IOException e) {
                // handle the exception
            }
        }

        if (city.trim().length() == 0) {
            Toast.makeText(context, R.string.error_empty_city, Toast.LENGTH_SHORT).show();
        } else if (about_me.trim().length() == 0) {
            Toast.makeText(context, R.string.error_empty_about_yourself, Toast.LENGTH_SHORT).show();
        } else if (symptoms.size() == 0) {
            Toast.makeText(context, R.string.error_empty_simul, Toast.LENGTH_SHORT).show();
        } else if (addresses.size() == 0){
            Toast.makeText(context, "Please enter valid location.", Toast.LENGTH_SHORT).show();
            // serverCallForUpdateProfile(city, about_me);

        }else {
            serverCallForUpdateProfile(city, about_me);
        }
    }


    private void serverCallForUpdateProfile(final String city, final String about_me) {
        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_UPDATE_PROFILE;

            activityEditProfileBinding.pbUpdate.setVisibility(View.VISIBLE);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                    activityEditProfileBinding.pbUpdate.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(new String(response.data));

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            JSONArray dataArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < dataArray.length(); i++) {

                                JSONObject userDataObject = dataArray.getJSONObject(i);

                                String user_id = userDataObject.getString(ServerCredentials.USER_ID);
                                String name = userDataObject.getString(ServerCredentials.NAME);
                                String email = userDataObject.getString(ServerCredentials.EMAIL_ID);
                                String about_self = userDataObject.getString(ServerCredentials.ABOUT_SELF);
                                String carer = userDataObject.getString(ServerCredentials.CARER);
                                String profile_pic = userDataObject.getString(ServerCredentials.PROFILE_PIC);
                                String city = userDataObject.getString(ServerCredentials.CITY);
                                String simul_symptoms = userDataObject.getString(ServerCredentials.SIMUL_SYMPTOMS);
                                String display_simul = userDataObject.getString(ServerCredentials.DISPLAY_SIMUL);

                                preferences.edit().putString(ServerCredentials.USER_ID, user_id).apply();
                                preferences.edit().putString(ServerCredentials.NAME, name).apply();
                                preferences.edit().putString(ServerCredentials.EMAIL_ID, email).apply();
                                preferences.edit().putString(ServerCredentials.ABOUT_SELF, about_self).apply();
                                preferences.edit().putString(ServerCredentials.CARER, carer).apply();
                                preferences.edit().putString(ServerCredentials.CITY, city).apply();
                                preferences.edit().putString(ServerCredentials.PROFILE_PIC, profile_pic).apply();
                                preferences.edit().putString(ServerCredentials.SIMUL_SYMPTOMS, simul_symptoms).apply();
                                preferences.edit().putString(ServerCredentials.DISPLAY_SIMUL, display_simul).apply();

                            }
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            onBackPressed();
                            upadateFirebaseData();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);
                    params.put(ServerCredentials.NAME, name);
                    params.put(ServerCredentials.EMAIL_ID, email);
                    params.put(ServerCredentials.ABOUT_SELF, about_me);
                    params.put(ServerCredentials.CITY, city);
                    params.put(ServerCredentials.SIMUL_SYMPTOMS, simul_symptoms);
                    params.put(ServerCredentials.DISPLAY_SIMUL, display_simul);
                    //params.put(ServerCredentials.PROFILE_PIC, profile_pic);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {

                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView

                    if (profileBitmap != null) {

                        params.put(ServerCredentials.PROFILE_PIC, new DataPart(System.currentTimeMillis() + ".jpg", getFileDataFromDrawable(profileBitmap)));
                    }

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void upadateFirebaseData() {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("display_simul", display_simul);
        if (set) {
            hashMap.put("imageURL", bitmapUrl);
        }
        reference.updateChildren(hashMap);
    }

    private void editProfileBitMoji() {

        if (installed) {

            activityEditProfileBinding.pbUpdate.setVisibility(View.VISIBLE);

            SnapLogin.getAuthTokenManager(context).startTokenGrant();

            SnapLogin.getLoginStateController(context).addOnLoginStateChangedListener(mLoginStateChangedListener);


        } else {
            Toast.makeText(context, "You have no Snapchat App !!!", Toast.LENGTH_SHORT).show();
        }
    }

  /*  @RequiresApi(api = Build.VERSION_CODES.M)
    private void editProfileImage() {
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    2000);
        } else {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, 100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            profileBitmap = null;

            if (data.getExtras() != null) {

                Bundle extras = data.getExtras();

                Bitmap bitmap = extras.getParcelable("data");

                profileBitmap = bitmap;

                activityEditProfileBinding.ivProfilePic.setImageBitmap(profileBitmap);

            } else if (data.getData() != null) {

                Uri uri = data.getData();

                Bitmap bitmap = null;

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

                } catch (IOException e) {

                    e.printStackTrace();
                }

                if (CommonMethods.exifCheck(bitmap, "file://" + uri.getPath()) != null) {

                    bitmap = CommonMethods.exifCheck(bitmap, "file://" + uri.getPath());
                }

                if (bitmap != null) {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

                    profileBitmap = bitmap;

                    activityEditProfileBinding.ivProfilePic.setImageBitmap(profileBitmap);

                }

            } else if (data.getClipData() != null) {

                Uri uri = data.getClipData().getItemAt(0).getUri();

                Bitmap bitmap = null;

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

                } catch (IOException e) {

                    e.printStackTrace();
                }

                if (bitmap != null) {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

                    profileBitmap = bitmap;

                    activityEditProfileBinding.ivProfilePic.setImageBitmap(profileBitmap);
//
//                    byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
//
//                    str_encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);


//                    Picasso.get().load(uri).transform(new CircleTransformPicasso()).into(iv_profile);

                    // iv_profilePic.setImageBitmap(CommonMethods.getCroppedBitmap(bitmap));
                }

            } else {

                Toast.makeText(context, "Something went wrong. Try different gallery app if problem persists.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "For check", Toast.LENGTH_SHORT).show();
        }

        if (resultCode == RESULT_OK) {

            Uri resultUri = data.getData();

            StorageReference filePath = userProfileImageRef.child(currentUserId + ".jpg");

            filePath.putFile(resultUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();

                            firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    rootRef.child("Users").child(currentUserId).child("imageURL")
                                            .setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        *//*Toast.makeText(SettingsActivity.this, "Image saved in database successfuly", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();*//*

                                                    } else {
                                                        String message = task.getException().toString();
                                                        Toast.makeText(context, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                        *//*loadingBar.dismiss();*//*

                                                    }

                                                }
                                            });

                                }
                            });

                        }
                    });
        }
    }*/

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    final LoginStateController.OnLoginStateChangedListener mLoginStateChangedListener =
            new LoginStateController.OnLoginStateChangedListener() {
                @Override
                public void onLoginSucceeded() {
                    // Toast.makeText(context, "onLoginSucceeded", Toast.LENGTH_SHORT).show();
                    SnapLogin.fetchUserData(context, query, null, new FetchUserDataCallback() {
                        @Override
                        public void onSuccess(@Nullable UserDataResponse userDataResponse) {
                            if (userDataResponse == null || userDataResponse.getData() == null) {
                                return;
                            }

                            MeData meData = userDataResponse.getData().getMe();
                            if (meData == null) {
                                return;
                            }
                            bitmapUrl = meData.getBitmojiData().getAvatar();
                            imageUri = Uri.parse(meData.getBitmojiData().getAvatar());
                            bitmoji_selection = true;
                            selection = true;
                            obj.execute();

                            if (bitmapUrl.startsWith("https")) {
                                set = true;
                            } else {
                                set = false;
                            }

                        }

                        @Override
                        public void onFailure(boolean b, int i) {

                            activityEditProfileBinding.pbUpdate.setVisibility(View.GONE);
                            Toast.makeText(context, "isNetworkError : " + b, Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "statusCode : " + i, Toast.LENGTH_SHORT).show();

                            //Toast.makeText(context, "Please try again !!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // Here you could update UI to show login success
                }

                @Override
                public void onLoginFailed() {
                    activityEditProfileBinding.pbUpdate.setVisibility(View.GONE);
                    // Here you could update UI to show login failure
                    Toast.makeText(context, "onLoginFailed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLogout() {
                    activityEditProfileBinding.pbUpdate.setVisibility(View.GONE);
                    Toast.makeText(context, "onLogout", Toast.LENGTH_SHORT).show();
                    // Here you could update UI to reflect logged out state
                }
            };

    public class MyAsync extends AsyncTask<Void, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {

            try {
                URL url = new URL(bitmapUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    MyAsync obj = new MyAsync() {

        @Override
        protected void onPostExecute(Bitmap bmp) {
            super.onPostExecute(bmp);

            profileBitmap = bmp;
            // Bitmap last = getRoundedCornerBitmap(profileBitmap);
            String oo = "";
            activityEditProfileBinding.ivProfilePic.setImageBitmap(profileBitmap);
            activityEditProfileBinding.pbUpdate.setVisibility(View.GONE);
            //Toast.makeText(context, "Get Your Bitmoji Successfully.", Toast.LENGTH_SHORT).show();


        }
    };

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
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
        p.setColor(Color.parseColor("#FB496D"));
        p.setStrokeWidth(6);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        return output;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
