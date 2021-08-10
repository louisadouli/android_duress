package com.simul.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivitySimulSupportBinding;
import com.simul.utils.CommonMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SimulSupportActivity extends AppCompatActivity {
    
    ActivitySimulSupportBinding activity_simul_support;
    
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity_simul_support = DataBindingUtil.setContentView(this,R.layout.activity_simul_support);
        
        idMapping();

        setClickListener();

        changeButtonBg();
        
        activity_simul_support.setActivity(this);
    }

    private void idMapping() {

        context = this;
    }

    private void setClickListener() {

        activity_simul_support.txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFeildValidation();
                Intent i = new Intent(context,SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void changeButtonBg() {

        activity_simul_support.edtEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(activity_simul_support.edtEmailAddress.getText().toString()) && !TextUtils.isEmpty(activity_simul_support.edtFeedbackMsg.getText().toString()) ) {
                    activity_simul_support.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    activity_simul_support.txtContinue.setTextColor(getResources().getColor(R.color.white));
                }else {
                    activity_simul_support.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    activity_simul_support.txtContinue.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });

        activity_simul_support.edtFeedbackMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(activity_simul_support.edtEmailAddress.getText().toString()) && !TextUtils.isEmpty(activity_simul_support.edtFeedbackMsg.getText().toString()) ) {
                    activity_simul_support.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    activity_simul_support.txtContinue.setTextColor(getResources().getColor(R.color.white));
                }else {
                    activity_simul_support.txtContinue.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    activity_simul_support.txtContinue.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });
    }

    private void checkFeildValidation() {

        String email = activity_simul_support.edtEmailAddress.getText().toString();
        String feedback_msg = activity_simul_support.edtFeedbackMsg.getText().toString();

        if(email.trim().length() == 0){
            Toast.makeText(context, R.string.error_empty_email, Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(context, R.string.error_invalid_email, Toast.LENGTH_SHORT).show();
        }else if(feedback_msg.trim().length() == 0){
            Toast.makeText(context, R.string.error_empty_feedback, Toast.LENGTH_SHORT).show();
        }else {
            serverCallForSimulSupoort(email,feedback_msg);
        }
    }

    private void serverCallForSimulSupoort(final String email, final String feedback_msg) {

        if(CommonMethods.checkInternetConnection(context)){

            String url = ServerUrls.SIMUL_SIMUL_SUPPORT;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if(success == 1){
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            onBackPressed();

                        }else {
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
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.EMAIL_ID, email);
                    params.put(ServerCredentials.MESSAGE, feedback_msg);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);

        }else {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
