package com.simul.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simul.R;
import com.simul.activity.CommentActivity;
import com.simul.activity.EditProfileActivity;
import com.simul.adapter.AnswerAdapter;
import com.simul.adapter.QueAnsAdapter;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.FragmentProfileBinding;
import com.simul.model.AnswerModel;
import com.simul.model.QueAnsModel;
import com.simul.utils.CommentLikeListener;
import com.simul.utils.CommonMethods;
import com.simul.utils.PostLikeListener;
import com.simul.utils.ShowCommentListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding fragmentProfileBinding;
    private Context context;
    private QueAnsAdapter queAnsAdapter;
    private AnswerAdapter answerAdapter;
    private SharedPreferences preferences;
    private ArrayList<QueAnsModel> queAnsModelList;
    private ArrayList<AnswerModel> answerModelList;
    public static ShowCommentListener showCommentListener;
    public static PostLikeListener postLikeListener;
    public static CommentLikeListener commentLikeListener;
    List<String> symptoms;
    String total_like;
    private String user_id, name, email, about_self, carer, city, simul_symptoms, profile_pic, display_simul;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        idMapping();

        setClickListener();

        setUpPrefData();

        serverCallForGetInsight();

        serverCallForQuestionList();

        return fragmentProfileBinding.getRoot();
    }

    private void idMapping() {

        context = getContext();

        queAnsModelList = new ArrayList<>();

        answerModelList = new ArrayList<>();

        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        profile_pic = preferences.getString(ServerCredentials.PROFILE_PIC, null);

        Picasso.get().load(profile_pic).placeholder(R.drawable.ic_dummy).into(fragmentProfileBinding.ivProfilePic);
    }

    private void setImageBoder() {

        display_simul = preferences.getString(ServerCredentials.DISPLAY_SIMUL, null);

        if (display_simul.equals("Diabetes")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            fragmentProfileBinding.ivProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        }
    }

    private void setClickListener() {

        fragmentProfileBinding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EditProfileActivity.class);
                i.putExtra("INSIGHT", total_like);
                startActivity(i);
            }
        });

        fragmentProfileBinding.txtQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverCallForQuestionList();
            }
        });

        fragmentProfileBinding.txtAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverCallForAnswerList();
            }
        });

        showCommentListener = new ShowCommentListener() {
            @Override
            public void showComment(String post_user_id, String post_id, String profile_pic, String name, String time_duration, String question, String number_of_likes, String number_of_comment, String number_of_view, String post_like, String question_image, String report, String display_simul, String post_Unlike) {
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
                i.putExtra(ServerCredentials.POST_LIKE, post_like);
                i.putExtra("Question_img", question_image);
                i.putExtra(ServerCredentials.REPORT, report);
                i.putExtra(ServerCredentials.DISPLAY_SIMUL, display_simul);
                i.putExtra("post_Unfav", post_Unlike);
                startActivity(i);
            }
        };

        postLikeListener = new PostLikeListener() {
            @Override
            public void likePost(String post_id, String status) {
                serverCallForLikePostQuestion(post_id, status);
            }
        };

        commentLikeListener = new CommentLikeListener() {
            @Override
            public void likeComment(String comment_id, String status) {
                serverCallForLikeComment(comment_id, status);
            }
        };
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

        Picasso.get().load(profile_pic).placeholder(getResources().getDrawable(R.drawable.ic_main_profile)).into(fragmentProfileBinding.ivProfilePic);
        fragmentProfileBinding.txtUsernameHeading.setText(name);
        fragmentProfileBinding.txtCity.setText(city);
        fragmentProfileBinding.txtAboutMe.setText(about_self);

        if (carer.equals("1")) {
            fragmentProfileBinding.cardIamCarer.setVisibility(View.VISIBLE);
            fragmentProfileBinding.txtAreYouC.setVisibility(View.VISIBLE);
        } else {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    2.0f
            );
            fragmentProfileBinding.cardIamCarer.setVisibility(View.GONE);
            fragmentProfileBinding.txtAreYouC.setVisibility(View.GONE);
            fragmentProfileBinding.cardLoaction.setLayoutParams(param);
        }

        setUpSymptomsInView();
    }

    private void setUpSymptomsInView() {

        fragmentProfileBinding.layout.removeAllViews();

        simul_symptoms = preferences.getString(ServerCredentials.SIMUL_SYMPTOMS, null);

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

           /* if(i > 0){
                textView.setLayoutParams(params);
            }*/

            textView.setPadding(paddingStartEnd, paddingTopBotoom, paddingStartEnd, paddingTopBotoom);
            textView.setTextColor(getResources().getColor(R.color.white));

            textView.setId(i);

            fragmentProfileBinding.layout.addView(textView);
        }


        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_type = textView.getText().toString();
                serverCallForQuestionList(post_type);
            }
        });*/

    }

    private void serverCallForGetInsight() {

        if (CommonMethods.checkInternetConnection(context)) {

            fragmentProfileBinding.pbProfile.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_GET_INSIGHT;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentProfileBinding.pbProfile.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            total_like = jsonObject.getString("total_like");
                            fragmentProfileBinding.txtTotalInsight.setVisibility(View.VISIBLE);
                            fragmentProfileBinding.txtTotalInsight.setText(total_like);
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        } else {
                            fragmentProfileBinding.txtTotalInsight.setVisibility(View.INVISIBLE);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        fragmentProfileBinding.pbProfile.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fragmentProfileBinding.pbProfile.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);//user_id
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

            fragmentProfileBinding.pbProfile.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_LIKE_COMMENT;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentProfileBinding.pbProfile.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            serverCallForAnswerList();
                            serverCallForGetInsight();
                        } else {
                            if (status.equals("1")) {
                                Toast.makeText(context, "You are already liked this answer.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "You are already Unlike this answer.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        fragmentProfileBinding.pbProfile.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fragmentProfileBinding.pbProfile.setVisibility(View.GONE);
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

    private void serverCallForAnswerList() {
        if (CommonMethods.checkInternetConnection(context)) {

            answerModelList.clear();

            fragmentProfileBinding.pbProfile.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_ANS_LIST;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentProfileBinding.pbProfile.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            fragmentProfileBinding.linQueAnsTab.setVisibility(View.VISIBLE);

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                AnswerModel answerModel = new AnswerModel();

                                String comment_id = dataObject.getString(ServerCredentials.COMMENT_ID);
                                String user_id = dataObject.getString(ServerCredentials.USER_ID);
                                String comment = dataObject.getString(ServerCredentials.COMMENT);
                                String number_of_likes = dataObject.getString(ServerCredentials.NUM_OF_LIKES);
                                String time_duration = dataObject.getString(ServerCredentials.TIME_DURATION);
                                String post_id = dataObject.getString(ServerCredentials.POST_ID);
                                String question = dataObject.getString(ServerCredentials.QUESTION);
                                String question_number_of_likes = dataObject.getString("question_number_of_likes ");
                                String question_number_of_comment = dataObject.getString("question_number_of_comment ");
                                String question_number_of_view = dataObject.getString("question_number_of_view ");

                                String question_img = null;
                                if (dataObject.has("question_image ")) {
                                    question_img = dataObject.getString("question_image ");
                                } else {
                                    question_img = "No_Image";
                                }
                                String symptoms = dataObject.getString("symptoms ");
                                String post_user_id = dataObject.getString("post_user_id ");
                                String post_user_profile_pic = dataObject.getString("post_user_profile_pic");
                                String post_user_name = dataObject.getString("post_user_name");
                                String post_time_duration = dataObject.getString("post_time_duration");
                                //String post_like = dataObject.getString("post_like");
                                String post_report = dataObject.getString("post_report");
                                String display_simul = preferences.getString(ServerCredentials.DISPLAY_SIMUL, null);

                                String post_like = null;
                                if (dataObject.has("post_like")) {
                                    post_like = dataObject.getString("post_like");
                                } else {
                                    post_like = "0";
                                }

                                String comment_like = null;
                                if (dataObject.has("comment_like")) {
                                    comment_like = dataObject.getString("comment_like");
                                } else {
                                    comment_like = "0";
                                }

                                String post_UnLike = null;
                                if (dataObject.has("post_UnLike")) {
                                    post_UnLike = "1";
                                } else {
                                    post_UnLike = "0";
                                }

                                String comment_DisLike = null;
                                if (dataObject.has("comment_DisLike")) {
                                    comment_DisLike = "1";
                                } else {
                                    comment_DisLike = "0";
                                }

                                answerModel.setReport("1");
                                answerModel.setName(name);
                                answerModel.setProfile_pic(profile_pic);
                                answerModel.setPost_id(post_id);
                                answerModel.setComment_id(comment_id);
                                answerModel.setUser_id(user_id);
                                answerModel.setComment(comment);
                                answerModel.setNumber_of_likes(number_of_likes);
                                answerModel.setTime_duration(time_duration);
                                answerModel.setQuestion(question);
                                answerModel.setQuestion_number_of_likes(question_number_of_likes);
                                answerModel.setQuestion_number_of_comment(question_number_of_comment);
                                answerModel.setQuestion_number_of_view(question_number_of_view);
                                answerModel.setQuestion_image(question_img);
                                answerModel.setSymptoms(symptoms);
                                answerModel.setPost_user_id(post_user_id);
                                answerModel.setPost_user_profile_pic(post_user_profile_pic);
                                answerModel.setPost_user_name(post_user_name);
                                answerModel.setPost_time_duration(post_time_duration);
                                answerModel.setComment_status(comment_like);
                                answerModel.setPost_status(post_like);
                                answerModel.setPost_report(post_report);
                                answerModel.setDisplay_simul(display_simul);
                                answerModel.setComment_DisLike(comment_DisLike);
                                answerModel.setPost_UnLike(post_UnLike);

                                answerModelList.add(answerModel);

                            }

                            answerAdapter = new AnswerAdapter(context, answerModelList);
                            fragmentProfileBinding.recylerProfileQuestionAns.setLayoutManager(new LinearLayoutManager(context));
                            fragmentProfileBinding.recylerProfileQuestionAns.setAdapter(answerAdapter);

                        } else {
                            //fragmentProfileBinding.linQueAnsTab.setVisibility(View.GONE);
                            Toast.makeText(context, "You have give No any Answer yet !!!", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }


    private void serverCallForQuestionList() {

        if (CommonMethods.checkInternetConnection(context)) {

            fragmentProfileBinding.pbProfile.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_QUE_LIST;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentProfileBinding.pbProfile.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            queAnsModelList.clear();

                            fragmentProfileBinding.linQueAnsTab.setVisibility(View.VISIBLE);

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                QueAnsModel queAnsModel = new QueAnsModel();

                                String question = dataObject.getString(ServerCredentials.QUESTION);
                                String time_duration = dataObject.getString(ServerCredentials.TIME_DURATION);
                                String number_of_likes = dataObject.getString(ServerCredentials.NUM_OF_LIKES);
                                String question_id = dataObject.getString(ServerCredentials.QUESTION_ID);
                                String profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                String name = dataObject.getString(ServerCredentials.NAME);
                                String post_id = dataObject.getString(ServerCredentials.POST_ID);
                                String user_id = dataObject.getString(ServerCredentials.USER_ID);
                                String symptoms = dataObject.getString(ServerCredentials.SYMPTOMS);
                                String number_of_comment = dataObject.getString(ServerCredentials.NUM_OF_COMMENT);
                                String number_of_view = dataObject.getString(ServerCredentials.NUM_OF_VIEW);

                                String question_img = null;
                                if (dataObject.has("question_img")) {
                                    question_img = dataObject.getString("question_img");
                                } else {
                                    question_img = "No_Image";
                                }

                                String post_fav = null;
                                if (dataObject.has("post_fav")) {
                                    post_fav = dataObject.getString(ServerCredentials.POST_FAV);
                                } else {
                                    post_fav = "0";
                                }

                                String post_UnFav = null;

                                if (dataObject.has("post_UnFav")) {
                                    post_UnFav = "1";
                                } else {
                                    post_UnFav = "0";
                                }

                                String report = dataObject.getString(ServerCredentials.REPORT);
                                String display_simul = preferences.getString(ServerCredentials.DISPLAY_SIMUL, null);

                                queAnsModel.setReport(report);
                                queAnsModel.setPost_id(post_id);
                                queAnsModel.setUser_id(user_id);
                                queAnsModel.setSymptoms(symptoms);
                                queAnsModel.setNumber_of_comment(number_of_comment);
                                queAnsModel.setNumber_of_view(number_of_view);
                                queAnsModel.setStatus(post_fav);
                                queAnsModel.setQuestion_img(question_img);

                                queAnsModel.setName(name);
                                queAnsModel.setNumber_of_likes(number_of_likes);
                                queAnsModel.setProfile_pic(profile_pic);
                                queAnsModel.setQuestion(question);
                                queAnsModel.setTime_duration(time_duration);
                                queAnsModel.setQuestion_id(question_id);
                                queAnsModel.setDisplay_simul(display_simul);
                                queAnsModel.setPost_UnFav(post_UnFav);

                                queAnsModelList.add(queAnsModel);

                            }

                            queAnsAdapter = new QueAnsAdapter(context, queAnsModelList);
                            fragmentProfileBinding.recylerProfileQuestionAns.setLayoutManager(new LinearLayoutManager(context));
                            fragmentProfileBinding.recylerProfileQuestionAns.setAdapter(queAnsAdapter);

                        } else {
                            fragmentProfileBinding.linQueAnsTab.setVisibility(View.GONE);
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void serverCallForLikePostQuestion(final String post_id, final String status) {

        if (CommonMethods.checkInternetConnection(context)) {

            fragmentProfileBinding.pbProfile.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_LIKE_POST_QUE;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentProfileBinding.pbProfile.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            serverCallForQuestionList();
                            serverCallForGetInsight();
                        } else {
                            if (status.equals("1")) {
                                Toast.makeText(context, "You are already liked this question.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "You are already Unlike this question.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        fragmentProfileBinding.pbProfile.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fragmentProfileBinding.pbProfile.setVisibility(View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        setUpPrefData();
        setImageBoder();
    }
}
