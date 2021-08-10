package com.simul.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivityEditDeletePostBinding;
import com.simul.utils.CommonMethods;
import com.simul.utils.EmojiExcludeFilter;
import com.simul.utils.VolleyMultipartRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditDeletePostActivity extends AppCompatActivity {

    ActivityEditDeletePostBinding activityEditDeletePostBinding;

    Context context;

    Bitmap postBitmap;

    String post_id, question, question_image;

    private int RESULT_LOAD_IMAGE = 100;

    private Bitmap profileBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditDeletePostBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_delete_post);

        idMapping();

        getIntentData();

        setClickListener();

        activityEditDeletePostBinding.setActivity(this);

    }

    private void idMapping() {

        context = this;

        activityEditDeletePostBinding.edtQuestion.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    private void getIntentData() {

        post_id = getIntent().getStringExtra(ServerCredentials.POST_ID);
        question = getIntent().getStringExtra(ServerCredentials.QUESTION);
        question_image = getIntent().getStringExtra(ServerCredentials.QUESTION_IMG);

        if (question_image.equals("No_Image")) {
            postBitmap = null;
        } else {
            //Picasso.get().load(question).into(activityEditDeletePostBinding.ivAttachedImg);
            Picasso.get().load(question_image).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    postBitmap = bitmap;
                    activityEditDeletePostBinding.ivAttachedImg.setImageBitmap(bitmap);
                    activityEditDeletePostBinding.ivImageName.setText("Tap here to change attached file");
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
        }

        activityEditDeletePostBinding.edtQuestion.setText(question);

    }


    private void setClickListener() {

        activityEditDeletePostBinding.linUploadImage.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
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
        });

        activityEditDeletePostBinding.txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = activityEditDeletePostBinding.edtQuestion.getText().toString();

                if(question.trim().length() == 0){
                    Toast.makeText(context, "Please type some conversation message", Toast.LENGTH_SHORT).show();
                }else {
                    serverCallForUpdatePost(question);
                }
            }
        });

        activityEditDeletePostBinding.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverCallForDeltePost();
            }
        });

        activityEditDeletePostBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void serverCallForUpdatePost(final String question) {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_UPDATE_POST;

            activityEditDeletePostBinding.pbUpdatePost.setVisibility(View.VISIBLE);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                    activityEditDeletePostBinding.pbUpdatePost.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(new String(response.data));

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(context,MainActivity.class);
                            startActivity(i);
                            finish();

                        } else {
                            // fragmentConversationTwoBinding.pbPostQuestion.setVisibility(View.GONE);
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
                    params.put(ServerCredentials.POST_ID, post_id);
                    params.put(ServerCredentials.QUESTION, question);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {

                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView

                    if (postBitmap != null) {

                       // preferences.edit().putBoolean("isProfilePicUpadated", false).apply();

                        params.put("question_img", new DataPart(System.currentTimeMillis() + ".jpg", getFileDataFromDrawable(postBitmap)));
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

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    private void serverCallForDeltePost() {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_DELETE_POST;

            activityEditDeletePostBinding.pbUpdatePost.setVisibility(View.VISIBLE);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    activityEditDeletePostBinding.pbUpdatePost.setVisibility(View.GONE);

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        //Toast.makeText(context, ""+success, Toast.LENGTH_SHORT).show();

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(context,MainActivity.class);
                            startActivity(i);
                            finish();

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
                    params.put(ServerCredentials.POST_ID, post_id);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2000: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context, "These permissions are mandatory for the application. Please allow access.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                postBitmap = selectedImage;

                activityEditDeletePostBinding.ivImageName.setText("Tap here to change attached file");
                activityEditDeletePostBinding.ivAttachedImg.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            postBitmap = null;

            if (data.getExtras() != null) {

                Bundle extras = data.getExtras();

                Bitmap bitmap = extras.getParcelable("data");

                postBitmap = bitmap;

                activityEditDeletePostBinding.ivAttachedImg.setImageBitmap(postBitmap);

                // iv_profile.setImageBitmap(profileBitmap);

            } else if (data.getData() != null) {

                Uri uri = data.getData();

                File f = new File("" + uri);


                activityEditDeletePostBinding.ivImageName.setText("Tap here to change attached file");

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

                    postBitmap = bitmap;

                    activityEditDeletePostBinding.ivAttachedImg.setImageBitmap(postBitmap);

                    //   iv_profile.setImageBitmap(profileBitmap);

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

                    postBitmap = bitmap;

                    activityEditDeletePostBinding.ivAttachedImg.setImageBitmap(postBitmap);

                    //  iv_profile.setImageBitmap(profileBitmap);
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
            // Toast.makeText(context, "For check", Toast.LENGTH_SHORT).show();
        }*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
