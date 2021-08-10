package com.simul.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivitySigninBinding;
import com.simul.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySigninBinding activitySigninBinding;

    Context context;

    FirebaseAuth auth;

    String token, currentUserId;

    DatabaseReference reference;

    FirebaseUser fuser;

    SharedPreferences preferences;

    String snapImage;

    boolean set = false;

    String TAG = "SigninActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySigninBinding = DataBindingUtil.setContentView(this, R.layout.activity_signin);

        idMapping();

        //getFirebaseToken();

        setClickListener();

        changeButtonBg();

        activitySigninBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;

        auth = FirebaseAuth.getInstance();

        //token = FirebaseInstanceId.getInstance().getToken();

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        snapImage = preferences.getString("SNAP_IMAGE","null");

        token = preferences.getString(ServerCredentials.TOKEN,null);

        if (snapImage.startsWith("https")) {
            set = true;
        } else {
            set = false;
        }



       /* activitySigninBinding.edtUserName.setImeActionLabel("", EditorInfo.IME_ACTION_NEXT);

        activitySigninBinding.edtUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_NEXT){
                    if( activitySigninBinding.edtUserName.getText().toString().trim().equalsIgnoreCase(""))
                        activitySigninBinding.edtUserName.setError("Please enter some thing!!!");
                    else
                        Toast.makeText(getApplicationContext(),"Notnull",Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });*/
    }

    /*private void getFirebaseToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                token = instanceIdResult.getToken();

                Log.d("Token", "onSuccess: " + token);
            }
        });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }*/

    private void setClickListener() {

        activitySigninBinding.cardSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //activitySigninBinding.cardSignIn.setCardBackgroundColor(getResources().getColor(R.color.orange));
                //activitySigninBinding.linSignIn.setBackgroundColor(getResources().getColor(R.color.orange));
                //activitySigninBinding.txtSignIn.setBackgroundColor(getResources().getColor(R.color.orange));
                //activitySigninBinding.txtSignIn.setTextColor(getResources().getColor(R.color.white));


                checkFelidValidation();

            }
        });

        activitySigninBinding.txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        activitySigninBinding.txtResetPassword.setOnClickListener(this);
        activitySigninBinding.txtContactSimul.setOnClickListener(this);
    }

    private void changeButtonBg() {

        activitySigninBinding.edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(activitySigninBinding.edtUserName.getText().toString()) && !TextUtils.isEmpty(activitySigninBinding.edtPassword.getText().toString())) {
                    activitySigninBinding.cardSignIn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    activitySigninBinding.cardSignIn.setTextColor(getResources().getColor(R.color.white));
                } else {
                    activitySigninBinding.cardSignIn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    activitySigninBinding.cardSignIn.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });

        activitySigninBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(activitySigninBinding.edtUserName.getText().toString()) && !TextUtils.isEmpty(activitySigninBinding.edtPassword.getText().toString())) {
                    activitySigninBinding.cardSignIn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    activitySigninBinding.cardSignIn.setTextColor(getResources().getColor(R.color.white));
                } else {
                    activitySigninBinding.cardSignIn.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    activitySigninBinding.cardSignIn.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_resetPassword:
                Intent in = new Intent(context, ForgotPasswordActivity.class);
                startActivity(in);
                break;

            case R.id.txt_contactSimul:
                Intent i = new Intent(context, SimulSupportActivity.class);
                startActivity(i);
                break;
        }
    }

    private void checkFelidValidation() {

        String username = activitySigninBinding.edtUserName.getText().toString();
        String password = activitySigninBinding.edtPassword.getText().toString();

        if (username.trim().length() == 0) {
            Toast.makeText(context, R.string.error_empty_username, Toast.LENGTH_SHORT).show();
        } else if (password.trim().length() == 0) {
            Toast.makeText(context, R.string.error_empty_password, Toast.LENGTH_SHORT).show();
        } else if (password.length() < 8) {
            Toast.makeText(context, R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
        } else {
            serverCallForSigiIn(username, password);

        }
    }

    private void serverCallForSigiIn(final String username, final String password) {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.USER_LOG_IN;

            activitySigninBinding.setProcessing(true);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        Log.d(TAG, String.valueOf(success));

                        //Toast.makeText(context, ""+success, Toast.LENGTH_SHORT).show();

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            Log.d(TAG, "Enter in success 1");

                            //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                            JSONArray dataArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < dataArray.length(); i++) {

                                JSONObject userDataObject = dataArray.getJSONObject(i);

                                String user_id = userDataObject.getString(ServerCredentials.USER_ID);
                                Log.d(TAG, "Come For fb signup");

                                signIn(username, password, user_id);
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
                        } else {
                            activitySigninBinding.setProcessing(false);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        activitySigninBinding.setProcessing(false);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activitySigninBinding.setProcessing(false);
                    Log.d(TAG, "error.getMessage()");
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.NAME, username);
                    params.put(ServerCredentials.PASSWORD, password);
                    params.put(ServerCredentials.TOKEN, token);//token
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

    private void signIn(String username, String password, final String user_id) {

        Log.d(TAG, "Fb signin method in");

        auth.signInWithEmailAndPassword(username + "@gmail.com", password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            activitySigninBinding.setProcessing(false);
                            //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                            addUserIdIntoFireBase(user_id);
                            Log.d(TAG, "After Add User Id");
                            Intent i = new Intent(context, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();
                            preferences.edit().putBoolean("hasLoggedIn", true).apply();

                        } else {
                            Log.d(TAG, "Authentication Failed");
                            activitySigninBinding.setProcessing(false);
                            Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addUserIdIntoFireBase(String user_id) {

        Log.d(TAG, "Add User Id");

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", user_id);
        if (set) {
            hashMap.put("imageURL", snapImage);
        }
        reference.updateChildren(hashMap);

        preferences.edit().putString("SNAP_IMAGE","snapchatImgUrl").apply();
        Log.d(TAG, "Add User Id Complete");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
