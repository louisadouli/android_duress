package com.simul.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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
import com.simul.databinding.ActivityEditDeleteCommentBinding;
import com.simul.utils.CommonMethods;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditDeleteCommentActivity extends AppCompatActivity {

    ActivityEditDeleteCommentBinding activityEditDeleteCommentBinding;

    Context context;

    String comment_id , comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditDeleteCommentBinding = DataBindingUtil.setContentView(this,R.layout.activity_edit_delete_comment);

        idMapping();

        getIntentData();

        setClickListener();

        activityEditDeleteCommentBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;
    }

    private void getIntentData() {

        comment = getIntent().getStringExtra(ServerCredentials.COMMENT);
        comment_id = getIntent().getStringExtra(ServerCredentials.COMMENT_ID);

        activityEditDeleteCommentBinding.edtComment.setText(comment);
    }

    private void setClickListener() {

        activityEditDeleteCommentBinding.txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = activityEditDeleteCommentBinding.edtComment.getText().toString();

                if(comment.trim().length() == 0){
                    Toast.makeText(context, "Please write comment...", Toast.LENGTH_SHORT).show();
                }else {
                    serverCallForUpadateComment(comment);
                }
            }
        });

        activityEditDeleteCommentBinding.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                severCallForDeleteComment();
            }
        });

        activityEditDeleteCommentBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


    }


    private void serverCallForUpadateComment(final String comment) {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_UPDATE_COMMENT;

            activityEditDeleteCommentBinding.pbUpdateComment.setVisibility(View.VISIBLE);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    activityEditDeleteCommentBinding.pbUpdateComment.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        //Toast.makeText(context, ""+success, Toast.LENGTH_SHORT).show();

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            /*Intent i = new Intent(context,CommentActivity.class);
                            startActivity(i);
                            finish();*/
                            onBackPressed();

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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.COMMENT_ID, comment_id);
                    params.put(ServerCredentials.COMMENT, comment);
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


    private void severCallForDeleteComment() {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_DELETE_COMMENT;

            activityEditDeleteCommentBinding.pbUpdateComment.setVisibility(View.VISIBLE);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    activityEditDeleteCommentBinding.pbUpdateComment.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        //Toast.makeText(context, ""+success, Toast.LENGTH_SHORT).show();

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            /*Intent i = new Intent(context,CommentActivity.class);
                            startActivity(i);
                            finish();*/
                            onBackPressed();


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
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.COMMENT_ID, comment_id);
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
