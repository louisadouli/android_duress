package com.simul.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivitySignUpBinding;
import com.simul.utils.CommonMethods;
import com.snapchat.kit.sdk.SnapLogin;
import com.snapchat.kit.sdk.core.controller.LoginStateController;
import com.snapchat.kit.sdk.login.models.MeData;
import com.snapchat.kit.sdk.login.models.UserDataResponse;
import com.snapchat.kit.sdk.login.networking.FetchUserDataCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    ActivitySignUpBinding activitySignUpBinding;

    Context context;
    private String carer = "0";
    private Uri imageUri;
    private FirebaseUser fuser;
    private boolean selection, bitmoji_selection = false;
    Bitmap bitmap;
    byte[] byteArray;
    String bitmapUrl;
    String query = "{me{bitmoji{avatar}}}";
    boolean installed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        idMapping();

        checkSnapChetInstallOrNot();

        setClickListener();

        changeButtonBg();

        activitySignUpBinding.setActivity(this);
    }

    private void changeButtonBg() {

        activitySignUpBinding.edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(activitySignUpBinding.edtUserName.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtEmailAddress.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtPassword.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtAboutYourself.getText().toString())
                ) {
                    activitySignUpBinding.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.white));
                } else {
                    activitySignUpBinding.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });

        activitySignUpBinding.edtEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(activitySignUpBinding.edtUserName.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtEmailAddress.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtPassword.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtAboutYourself.getText().toString())
                ) {
                    activitySignUpBinding.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.white));
                } else {
                    activitySignUpBinding.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });

        activitySignUpBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(activitySignUpBinding.edtUserName.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtEmailAddress.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtPassword.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtAboutYourself.getText().toString())
                ) {
                    activitySignUpBinding.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.white));
                } else {
                    activitySignUpBinding.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });

        activitySignUpBinding.edtAboutYourself.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activitySignUpBinding.txtAboutSelfCount.setText(String.valueOf(charSequence.length()) + "/500");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(activitySignUpBinding.edtUserName.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtEmailAddress.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtPassword.getText().toString())
                        && !TextUtils.isEmpty(activitySignUpBinding.edtAboutYourself.getText().toString())
                ) {
                    activitySignUpBinding.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.white));
                } else {
                    activitySignUpBinding.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });
    }

    private void idMapping() {

        context = this;
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

    private void setClickListener() {

        activitySignUpBinding.txtConnectBitmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (installed) {

                    activitySignUpBinding.pbSnapImage.setVisibility(View.VISIBLE);

                    SnapLogin.getAuthTokenManager(context).startTokenGrant();

                    SnapLogin.getLoginStateController(context).addOnLoginStateChangedListener(mLoginStateChangedListener);


                } else {
                    Toast.makeText(context, "You have no Snapchat App !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activitySignUpBinding.ivAvtarOne.setOnClickListener(this);
        activitySignUpBinding.ivAvtarTwo.setOnClickListener(this);
        activitySignUpBinding.ivAvtarThree.setOnClickListener(this);
        activitySignUpBinding.ivAvtarFour.setOnClickListener(this);
        activitySignUpBinding.txtContinue.setOnClickListener(this);

        activitySignUpBinding.switchCarer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    carer = "1";
                } else {
                    carer = "0";
                }
            }
        });

        activitySignUpBinding.edtUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String checkName = textView.getText().toString();
                    serverCallForCheckUserName(checkName);
                }
                return false;
            }
        });

        activitySignUpBinding.edtUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String checkName = activitySignUpBinding.edtUserName.getText().toString();
                    if (checkName.trim().length() > 0) {
                        serverCallForCheckUserName(checkName);
                    }
                }
            }
        });

        activitySignUpBinding.edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String checkName = editable.toString();
                if (checkName.trim().length() > 0) {
                    serverCallForCheckUserName(checkName);
                }
            }
        });

        activitySignUpBinding.edtEmailAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    String checkemail = textView.getText().toString();
                    if (checkemail.trim().length() > 0){
                        serverCallForCheckEmail(checkemail);
                    }

                }
                return false;
            }
        });

        activitySignUpBinding.edtEmailAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String checkemail = activitySignUpBinding.edtEmailAddress.getText().toString();
                    if (checkemail.trim().length() > 0) {
                        serverCallForCheckEmail(checkemail);
                    }
                }
            }
        });

        activitySignUpBinding.edtEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String emailId = editable.toString();
                if (emailId.trim().length() > 0) {
                    serverCallForCheckEmail(emailId);
                }
            }
        });

        activitySignUpBinding.edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String enteredText = s.toString();

                if (!isValidPassword(enteredText.trim())) {
                    Drawable icon = getResources().getDrawable(R.drawable.ic_error_username_24dp);

                    if (icon != null) {
                        icon.setBounds(0, 0,
                                icon.getIntrinsicWidth(),
                                icon.getIntrinsicHeight());
                    }

                    activitySignUpBinding.edtPassword.setError("Your password must be at least 8 character, contain one uppercase letter and one number and one symbol.", icon);

                }
            }
        });

    }

    private void serverCallForCheckEmail(final String checkemail) {
        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_CHECK_EMAIL;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Drawable icon = getResources().getDrawable(R.drawable.ic_error_username_24dp);

                            if (icon != null) {
                                icon.setBounds(0, 0,
                                        icon.getIntrinsicWidth(),
                                        icon.getIntrinsicHeight());
                            }

                            activitySignUpBinding.edtEmailAddress.setError("This Email Address Already Exist.", icon);
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.EMAIL, checkemail);
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

    private void serverCallForCheckUserName(final String checkName) {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_CHECK_USER_NAME;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Drawable icon = getResources().getDrawable(R.drawable.ic_error_username_24dp);

                            if (icon != null) {
                                icon.setBounds(0, 0,
                                        icon.getIntrinsicWidth(),
                                        icon.getIntrinsicHeight());
                            }

                            activitySignUpBinding.edtUserName.setError("This Username Already Exist.", icon);
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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.NAME, checkName);
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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_avtarOne:
                activitySignUpBinding.ivAvtarOne.setBorderColor(getResources().getColor(R.color.orange));
                activitySignUpBinding.ivAvtarOne.setBorderWidth(2);
                activitySignUpBinding.ivAvtarOne.setSelected(true);
                activitySignUpBinding.ivAvtarTwo.setSelected(false);
                activitySignUpBinding.ivAvtarThree.setSelected(false);
                activitySignUpBinding.ivAvtarFour.setSelected(false);
                selection = true;
                activitySignUpBinding.ivAvtarTwo.setBorderColor(getResources().getColor(R.color.white));
                activitySignUpBinding.ivAvtarThree.setBorderColor(getResources().getColor(R.color.white));
                activitySignUpBinding.ivAvtarFour.setBorderColor(getResources().getColor(R.color.white));

                break;

            case R.id.iv_avtarTwo:
                activitySignUpBinding.ivAvtarTwo.setBorderColor(getResources().getColor(R.color.orange));
                activitySignUpBinding.ivAvtarTwo.setBorderWidth(2);
                activitySignUpBinding.ivAvtarTwo.setSelected(true);
                activitySignUpBinding.ivAvtarOne.setSelected(false);
                activitySignUpBinding.ivAvtarThree.setSelected(false);
                activitySignUpBinding.ivAvtarFour.setSelected(false);
                selection = true;
                activitySignUpBinding.ivAvtarOne.setBorderColor(getResources().getColor(R.color.white));
                activitySignUpBinding.ivAvtarThree.setBorderColor(getResources().getColor(R.color.white));
                activitySignUpBinding.ivAvtarFour.setBorderColor(getResources().getColor(R.color.white));

                break;

            case R.id.iv_avtarThree:
                activitySignUpBinding.ivAvtarThree.setBorderColor(getResources().getColor(R.color.orange));
                activitySignUpBinding.ivAvtarThree.setBorderWidth(2);
                activitySignUpBinding.ivAvtarThree.setSelected(true);
                activitySignUpBinding.ivAvtarOne.setSelected(false);
                activitySignUpBinding.ivAvtarTwo.setSelected(false);
                activitySignUpBinding.ivAvtarFour.setSelected(false);
                selection = true;
                activitySignUpBinding.ivAvtarOne.setBorderColor(getResources().getColor(R.color.white));
                activitySignUpBinding.ivAvtarTwo.setBorderColor(getResources().getColor(R.color.white));
                activitySignUpBinding.ivAvtarFour.setBorderColor(getResources().getColor(R.color.white));

                break;

            case R.id.iv_avtarFour:
                activitySignUpBinding.ivAvtarFour.setBorderColor(getResources().getColor(R.color.orange));
                activitySignUpBinding.ivAvtarFour.setBorderWidth(2);
                activitySignUpBinding.ivAvtarFour.setSelected(true);
                activitySignUpBinding.ivAvtarOne.setSelected(false);
                activitySignUpBinding.ivAvtarTwo.setSelected(false);
                activitySignUpBinding.ivAvtarThree.setSelected(false);
                selection = true;
                activitySignUpBinding.ivAvtarOne.setBorderColor(getResources().getColor(R.color.white));
                activitySignUpBinding.ivAvtarTwo.setBorderColor(getResources().getColor(R.color.white));
                activitySignUpBinding.ivAvtarThree.setBorderColor(getResources().getColor(R.color.white));

                break;

            case R.id.txt_continue:
                activitySignUpBinding.txtContinue.setBackground(getResources().getDrawable(R.drawable.button_orange_bg));
                activitySignUpBinding.txtContinue.setTextColor(getResources().getColor(R.color.white));

                checkFeildValidation();

                break;
        }

    }

    private void checkFeildValidation() {

        String username = activitySignUpBinding.edtUserName.getText().toString();
        String email = activitySignUpBinding.edtEmailAddress.getText().toString();
        String password = activitySignUpBinding.edtPassword.getText().toString();
        String about_your_self = activitySignUpBinding.edtAboutYourself.getText().toString();

        if (username.trim().length() == 0) {
            Toast.makeText(context, R.string.error_empty_username, Toast.LENGTH_SHORT).show();
        } else if (username.contains("@")) {
            Toast.makeText(context, "Username in not use @ sign.", Toast.LENGTH_SHORT).show();
        } else if (email.trim().length() == 0) {
            Toast.makeText(context, R.string.error_empty_email, Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(context, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
        } else if (password.trim().length() == 0) {
            Toast.makeText(context, R.string.error_empty_password, Toast.LENGTH_SHORT).show();
        } else if (!isValidPassword(password.trim())) {
            Toast.makeText(context, R.string.error_invalid_password, Toast.LENGTH_SHORT).show();
        } else if (about_your_self.trim().length() == 0) {
            Toast.makeText(context, R.string.error_empty_about_yourself, Toast.LENGTH_SHORT).show();
        } else if (about_your_self.length() > 500) {
            Toast.makeText(context, R.string.error_invalid_about_yourself, Toast.LENGTH_SHORT).show();
        } else if (!selection) {
            Toast.makeText(context, "Please Select Avtar !!!", Toast.LENGTH_SHORT).show();
        } else {
            //serverCallForSignUp(username, email, password, about_your_self);
            //register(username, email, password);
            if (!bitmoji_selection) {
                imageSelection();
            }


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byteArray = stream.toByteArray();


            Intent i = new Intent(context, SignUpSelectSimulsActivity.class);
            i.putExtra(ServerCredentials.NAME, username);
            i.putExtra(ServerCredentials.EMAIL_ID, email);
            i.putExtra(ServerCredentials.PASSWORD, password);
            i.putExtra(ServerCredentials.ABOUT_SELF, about_your_self);
            i.putExtra(ServerCredentials.CARER, carer);
            i.putExtra("imageUri", imageUri.toString());
            i.putExtra(ServerCredentials.PROFILE_PIC, byteArray);
            startActivity(i);

            finish();

        }

    }

    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private void imageSelection() {
        if (activitySignUpBinding.ivAvtarOne.isSelected()) {
            imageUri = Uri.parse("android.resource://" + context.getPackageName() + "/drawable/ic_avtar_uncheck_one");
            bitmap = ((BitmapDrawable) activitySignUpBinding.ivAvtarOne.getDrawable()).getBitmap();
            //uploadImage();
        } else if (activitySignUpBinding.ivAvtarTwo.isSelected()) {
            imageUri = Uri.parse("android.resource://" + context.getPackageName() + "/drawable/ic_avtar_uncheck_two");
            bitmap = ((BitmapDrawable) activitySignUpBinding.ivAvtarTwo.getDrawable()).getBitmap();
            //uploadImage();
        } else if (activitySignUpBinding.ivAvtarThree.isSelected()) {
            imageUri = Uri.parse("android.resource://" + context.getPackageName() + "/drawable/ic_avtar_uncheck_three");
            bitmap = ((BitmapDrawable) activitySignUpBinding.ivAvtarThree.getDrawable()).getBitmap();
            //uploadImage();
        } else if (activitySignUpBinding.ivAvtarFour.isSelected()) {
            imageUri = Uri.parse("android.resource://" + context.getPackageName() + "/drawable/ic_avtar_uncheck_four");
            bitmap = ((BitmapDrawable) activitySignUpBinding.ivAvtarFour.getDrawable()).getBitmap();
            //uploadImage();
        } else {
            //obj.execute();
        }
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

                        }

                        @Override
                        public void onFailure(boolean b, int i) {

                            activitySignUpBinding.pbSnapImage.setVisibility(View.GONE);
                            /*Toast.makeText(context, "isNetworkError : " + b, Toast.LENGTH_SHORT).show();
                            Toast.makeText(context, "statusCode : " + i, Toast.LENGTH_SHORT).show();
*/
                            Toast.makeText(context, "Please try again !!!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // Here you could update UI to show login success
                }

                @Override
                public void onLoginFailed() {
                    activitySignUpBinding.pbSnapImage.setVisibility(View.GONE);
                    // Here you could update UI to show login failure
                    Toast.makeText(context, "onLoginFailed", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onLogout() {
                    activitySignUpBinding.pbSnapImage.setVisibility(View.GONE);
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

            bitmap = bmp;
            String oo = "";
            activitySignUpBinding.ivBitmojiAvtar.setImageBitmap(bitmap);
            activitySignUpBinding.pbSnapImage.setVisibility(View.GONE);
            //Toast.makeText(context, "Get Your Bitmoji Successfully.", Toast.LENGTH_SHORT).show();


        }
    };


    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //imageUri = data.getData();

        if(uploadTask != null && uploadTask.isInProgress()){
            Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show();
        }else {
            uploadImage();
        }
    }*/
}
