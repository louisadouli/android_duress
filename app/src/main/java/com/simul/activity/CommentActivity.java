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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simul.R;
import com.simul.adapter.CommentAdapter;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivityCommentBinding;
import com.simul.model.CommentModel;
import com.simul.utils.CommentLikeListener;
import com.simul.utils.CommentReportListener;
import com.simul.utils.CommonMethods;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    ActivityCommentBinding activityCommentBinding;

    Context context;

    CommentAdapter commentAdapter;

    boolean checkReport;

    private SharedPreferences preferences;

    private ArrayList<CommentModel> commentModelArrayList;

    public static CommentLikeListener commentLikeListener;

    public static CommentReportListener commentReportListener;

    boolean queLiked, queDislike;

    boolean checkCommentReport;

    String comment_type = "Best Comment";

    String report, post_user_id, post_id, user_id, post_like, profile_pic, name, time_duration, question, number_of_likes, number_of_comment, number_of_view, question_img, display_simul, post_Unfav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        activityCommentBinding = DataBindingUtil.setContentView(this, R.layout.activity_comment);

        idMapping();

        getDataFromIntentAndPref();

        setUpCommentList();

        setClickListener();

        setImageBoder();

        activityCommentBinding.setActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        serverCallForCommentList();
    }

    private void idMapping() {

        context = this;

        commentModelArrayList = new ArrayList<>();

        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        //activityCommentBinding.edtRplyMsg.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    private void getDataFromIntentAndPref() {

        post_user_id = getIntent().getStringExtra("POST_USER_ID");
        post_id = getIntent().getStringExtra(ServerCredentials.POST_ID);
        profile_pic = getIntent().getStringExtra(ServerCredentials.PROFILE_PIC);
        name = getIntent().getStringExtra(ServerCredentials.NAME);
        post_like = getIntent().getStringExtra(ServerCredentials.POST_LIKE);
        time_duration = getIntent().getStringExtra(ServerCredentials.TIME_DURATION);
        question_img = getIntent().getStringExtra("Question_img");
        report = getIntent().getStringExtra(ServerCredentials.REPORT);
        question = getIntent().getStringExtra(ServerCredentials.QUESTION);
        number_of_likes = getIntent().getStringExtra(ServerCredentials.NUM_OF_LIKES);
        number_of_comment = getIntent().getStringExtra(ServerCredentials.NUM_OF_COMMENT);
        number_of_view = getIntent().getStringExtra(ServerCredentials.NUM_OF_VIEW);
        user_id = preferences.getString(ServerCredentials.USER_ID, null);
        display_simul = getIntent().getStringExtra(ServerCredentials.DISPLAY_SIMUL);
        post_Unfav = getIntent().getStringExtra("post_Unfav");

        if (post_user_id.equals(user_id)) {
            activityCommentBinding.ivComentInfo.setVisibility(View.GONE);
            activityCommentBinding.ivComentForword.setVisibility(View.GONE);
            activityCommentBinding.txtEdit.setVisibility(View.VISIBLE);
        } else {
            activityCommentBinding.ivComentInfo.setVisibility(View.VISIBLE);
            activityCommentBinding.ivComentForword.setVisibility(View.VISIBLE);
            activityCommentBinding.txtEdit.setVisibility(View.GONE);
        }

        if (report.equals("1")) {
            checkReport = true;
        } else {
            checkReport = false;
        }

        if (question_img.equals("No_Image")) {
            activityCommentBinding.ivQuestionImg.setVisibility(View.GONE);
        } else if (question_img.equals("")) {
            activityCommentBinding.ivQuestionImg.setVisibility(View.GONE);
        } else {
            activityCommentBinding.ivQuestionImg.setVisibility(View.VISIBLE);
            Picasso.get().load(question_img).into(activityCommentBinding.ivQuestionImg);
        }

        Picasso.get().load(profile_pic).into(activityCommentBinding.ivCommentProfile);
        activityCommentBinding.txtComantUserName.setText(name);
        activityCommentBinding.txtTime.setText(time_duration);
        activityCommentBinding.txtQuestion.setText(question);
        activityCommentBinding.txtLike.setText(number_of_likes);
        activityCommentBinding.txtTotalComments.setText(number_of_comment);
        activityCommentBinding.txtTotalView.setText(number_of_view);

        if (post_Unfav.equals("1")) {
            Drawable img = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
            activityCommentBinding.txtDisLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            queLiked = false;
            queDislike = true;
        } else {
            if (post_like.equals("1")) {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                activityCommentBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                queLiked = true;
                queDislike = true;
            } else if (post_like.equals("0")) {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                activityCommentBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                queLiked = false;
                queDislike = false;
            }
        }

    }

    private void setUpCommentList() {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        activityCommentBinding.recylerAllComment.setLayoutManager(mLayoutManager);
        activityCommentBinding.recylerAllComment.setAdapter(commentAdapter);
    }

    private void setClickListener() {

        activityCommentBinding.ivRplySend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String reply_message = activityCommentBinding.edtRplyMsg.getText().toString();
                if (reply_message.trim().length() == 0) {
                    Toast.makeText(context, "Please write comment...", Toast.LENGTH_SHORT).show();
                } else {
                    serverCallForAddComment(reply_message);
                }
            }
        });

        activityCommentBinding.linReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activityCommentBinding.relReply.setVisibility(View.VISIBLE);
            }
        });

        activityCommentBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        activityCommentBinding.linBestComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment_type = "Best Comment";
                serverCallForCommentList();
            }
        });

        activityCommentBinding.linLatestComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comment_type = "Latest Comment";
                serverCallForCommentList();
            }
        });

        activityCommentBinding.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!queLiked) {
                    serverCallForLikePostQuestion(post_id, "1");
                    int count = Integer.valueOf(activityCommentBinding.txtLike.getText().toString());
                    count = count + 1;
                    if (count == 0) {
                        count = count + 1;
                    }
                    Drawable img1 = context.getResources().getDrawable(R.drawable.ic_cmt_post_thumb_down);
                    activityCommentBinding.txtDisLike.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);

                    Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                    activityCommentBinding.txtLike.setText("" + count);
                    activityCommentBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    queLiked = true;
                }
            }
        });

        activityCommentBinding.txtDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (queLiked) {
                    serverCallForLikePostQuestion(post_id, "0");
                    serverCallForLikePostQuestion(post_id, "0");
                    int count = Integer.valueOf(activityCommentBinding.txtLike.getText().toString());
                    count = count - 1;
                    if (count == 0) {
                        count = count - 1;
                    }

                    Drawable img1 = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
                    activityCommentBinding.txtDisLike.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);

                    Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                    activityCommentBinding.txtLike.setText("" + count);
                    activityCommentBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    queLiked = false;
                } else {
                    if (!queDislike) {
                        serverCallForLikePostQuestion(post_id, "0");
                        int count = Integer.valueOf(activityCommentBinding.txtLike.getText().toString());
                        count = count - 1;
                        if (count == 0) {
                            count = count - 1;
                        }

                        Drawable img1 = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
                        activityCommentBinding.txtDisLike.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);

                        Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                        activityCommentBinding.txtLike.setText("" + count);
                        activityCommentBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                        queDislike = true;
                    }
                }

            }
        });

        commentLikeListener = new CommentLikeListener() {
            @Override
            public void likeComment(String comment_id, String status) {
                serverCallForLikeComment(comment_id, status);
            }
        };

        commentReportListener = new CommentReportListener() {
            @Override
            public void reportComment(final String comment_id) {

                if (!checkCommentReport) {
                    final Dialog dialog = new Dialog(context);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    dialog.setContentView(R.layout.dialog_comment_report);

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
                            // Toast.makeText(context, "Server Call here!!!", Toast.LENGTH_SHORT).show();
                            serverCallForCommentReport(comment_id);
                            dialog.dismiss();

                        }
                    });

                    txt_No.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {

                }


            }
        };

        activityCommentBinding.ivComentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkReport) {
                    if (report.equals("0")) {
                        final Dialog dialog = new Dialog(context);

                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        dialog.setContentView(R.layout.dialog_question_report);

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
                                //Toast.makeText(context, "Server Call here!!!", Toast.LENGTH_SHORT).show();
                                serverCallForQuestionReport();
                                dialog.dismiss();
                                checkReport = true;

                            }
                        });

                        txt_No.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    } else {
                        Toast.makeText(context, "You are already report this post.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "You are already report this post.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        activityCommentBinding.ivComentForword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri bmpUri = getLocalBitmapUri(activityCommentBinding.ivQuestionImg);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Question");
                String shareMessage = activityCommentBinding.txtQuestion.getText().toString();
                //shareMessage = shareMessage + "\n\n" + activityCommentBinding.txtLike.getText().toString() + " Likes, " + activityCommentBinding.txtTotalComments.getText().toString() + " Comments" + "\n\n" + "https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu";
                shareMessage = "Post Message : " + shareMessage + "\n\n" + activityCommentBinding.txtLike.getText().toString() + " Likes, " + activityCommentBinding.txtTotalComments.getText().toString() + " Comments" + "\n\nPost by : " + activityCommentBinding.txtComantUserName.getText().toString();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                if (question_img.equals("No_Image")) {

                } else if (question_img.equals("")) {

                } else {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                    shareIntent.setType("image/*");
                }
                startActivity(Intent.createChooser(shareIntent, "Share Question..."));

            }
        });

        activityCommentBinding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EditDeletePostActivity.class);
                i.putExtra(ServerCredentials.POST_ID, post_id);
                i.putExtra(ServerCredentials.QUESTION, question);
                i.putExtra(ServerCredentials.QUESTION_IMG, question_img);
                startActivity(i);
            }
        });
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable) {
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
            File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
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

    private void serverCallForAddComment(final String reply_message) {

        if (CommonMethods.checkInternetConnection(context)) {

            activityCommentBinding.pbComment.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_ADD_COMMENT;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        activityCommentBinding.pbComment.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            serverCallForCommentList();
                            activityCommentBinding.edtRplyMsg.setText("");
                            activityCommentBinding.relReply.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        activityCommentBinding.pbComment.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityCommentBinding.pbComment.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);//user_id
                    params.put(ServerCredentials.POST_ID, post_id);
                    params.put(ServerCredentials.COMMENT, reply_message);
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

    private void serverCallForCommentReport(final String comment_id) {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_REPORT_COMMENT;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);


                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        activityCommentBinding.pbComment.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityCommentBinding.pbComment.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);//user_id
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

    private void serverCallForQuestionReport() {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_REPORT_POST;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);


                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        activityCommentBinding.pbComment.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityCommentBinding.pbComment.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);//user_id
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

    private void serverCallForLikeComment(final String comment_id, final String status) {

        if (CommonMethods.checkInternetConnection(context)) {

            activityCommentBinding.pbComment.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_LIKE_COMMENT;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        activityCommentBinding.pbComment.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            serverCallForCommentList();
                        } else {
                            if (status.equals("1")) {
                                Toast.makeText(context, "You are already liked this comment.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "You are already Unlike this comment.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        activityCommentBinding.pbComment.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityCommentBinding.pbComment.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);//user_id
                    params.put(ServerCredentials.COMMENT_ID, comment_id);
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

    private void serverCallForLikePostQuestion(final String post_id, final String status) {

        if (CommonMethods.checkInternetConnection(context)) {

            activityCommentBinding.pbComment.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_LIKE_POST_QUE;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        activityCommentBinding.pbComment.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            //serverCallForQuestionList(post_type);
                        } else {
                            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        activityCommentBinding.pbComment.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityCommentBinding.pbComment.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);
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

    private void serverCallForCommentList() {

        if (CommonMethods.checkInternetConnection(context)) {

            commentModelArrayList.clear();

            activityCommentBinding.pbComment.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_COMMENT_LIST;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        activityCommentBinding.pbComment.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            activityCommentBinding.linCommentNotFound.setVisibility(View.GONE);

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                CommentModel commentModel = new CommentModel();

                                String comment_id = dataObject.getString(ServerCredentials.COMMENT_ID);
                                String comment = dataObject.getString(ServerCredentials.COMMENT);
                                String number_of_likes = dataObject.getString(ServerCredentials.NUM_OF_LIKES);
                                String time_duration = dataObject.getString(ServerCredentials.TIME_DURATION);
                                String profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                String name = dataObject.getString(ServerCredentials.NAME);
                                String report = dataObject.getString(ServerCredentials.REPORT);
                                String user_id = dataObject.getString(ServerCredentials.USER_ID);

                                String comment_DisLike = null;
                                if (dataObject.has("comment_DisLike")) {
                                    comment_DisLike = "1";
                                } else {
                                    comment_DisLike = "0";
                                }

                                String commentlike = null;
                                if (dataObject.has("comment_like")) {
                                    commentlike = "1";
                                } else {
                                    commentlike = "0";
                                }
                                String display_simul = dataObject.getString(ServerCredentials.DISPLAY_SIMUL);

                                commentModel.setUser_id(user_id);
                                commentModel.setComment_id(comment_id);
                                commentModel.setComment(comment);
                                commentModel.setNumber_of_likes(number_of_likes);
                                commentModel.setTime_duration(time_duration);
                                commentModel.setProfile_pic(profile_pic);
                                commentModel.setName(name);
                                commentModel.setComment_like(commentlike);
                                commentModel.setReport(report);
                                commentModel.setDisplay_simul(display_simul);
                                commentModel.setComment_DisLike(comment_DisLike);

                                commentModelArrayList.add(commentModel);

                                activityCommentBinding.txtTotalComments.setText("" + commentModelArrayList.size());

                            }

                        } else {
                            commentModelArrayList.clear();
                            activityCommentBinding.pbComment.setVisibility(View.GONE);
                            activityCommentBinding.linCommentNotFound.setVisibility(View.VISIBLE);
                        }
                        commentAdapter = new CommentAdapter(context, commentModelArrayList, user_id , activityCommentBinding.txtQuestion.getText().toString());
                        activityCommentBinding.recylerAllComment.setLayoutManager(new LinearLayoutManager(context));
                        activityCommentBinding.recylerAllComment.setAdapter(commentAdapter);

                    } catch (JSONException e) {
                        activityCommentBinding.pbComment.setVisibility(View.GONE);
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
                    params.put(ServerCredentials.USER_ID, user_id);//user_id
                    params.put(ServerCredentials.COMMENT_TYPE, comment_type);
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

    private void setImageBoder() {

        if (display_simul.equals("Diabetes")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            activityCommentBinding.ivCommentProfile.setBorderColor(Color.parseColor("#BFFB496D"));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
