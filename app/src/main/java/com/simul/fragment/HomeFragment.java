package com.simul.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.simul.activity.CommentActivity;
import com.simul.activity.StartConversationActivity;
import com.simul.activity.ViewOthersProfileActivity;
import com.simul.adapter.HomeMessagesAdapter;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.FragmentHomeBinding;
import com.simul.model.HomeMessagesListModel;
import com.simul.utils.CommonMethods;
import com.simul.utils.OpenDialogListner;
import com.simul.utils.PostLikeListener;
import com.simul.utils.ShowCommentListener;
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

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding fragmentHomeBinding;

    private Context context;

    private int mTotalDy;

    private SharedPreferences preferences;

    private String simul_symptoms, user_id;

    private String post_type = "Latest";

    public static ShowCommentListener showCommentListener;

    public static PostLikeListener postLikeListener;

    public static OpenDialogListner openDialogListner;

    private HomeMessagesAdapter homeMessagesAdapter;

    PhotoViewAttacher photoViewAttacher;

    private ArrayList<HomeMessagesListModel> homeMessagesList;

    List<String> symptoms, allSymtoms;

    private boolean report_check = false;
    private boolean isUserScrolling = false;
    private boolean isListGoingUp = true;
    boolean post_like = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        EmojiCompat.init(new BundledEmojiCompatConfig(getContext()));

        idMapping();

        setUpSymptomsInView();

        serverCallForQuestionList(post_type);

        setClickListener();

        setUpSearchFunctionality();

        return fragmentHomeBinding.getRoot();
    }

    private void idMapping() {

        context = getContext();

        homeMessagesList = new ArrayList<>();

        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        user_id = preferences.getString(ServerCredentials.USER_ID, null);

    }

    private void setUpSearchFunctionality() {

        fragmentHomeBinding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //after the change calling the method and passing the search input

            }
        });
    }

    /*void filter(String text){
        ArrayList<HomeMessagesListModel> temp = new ArrayList();
        for(HomeMessagesListModel d: homeMessagesList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        homeMessagesAdapter.updateList(temp);
    }*/

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<HomeMessagesListModel> filterdNames = new ArrayList<>();

        //looping through existing elements
        for (HomeMessagesListModel s : homeMessagesList) {
            //if the existing elements contains the search input
            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getQuestion().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s);
            }
        }

        //calling a method of the adapter class and passing the filtered list
        ///homeMessagesAdapter.filterList(filterdNames);

        homeMessagesAdapter = new HomeMessagesAdapter(context, filterdNames);
        fragmentHomeBinding.recylerHomeMessages.setLayoutManager(new LinearLayoutManager(context));
        fragmentHomeBinding.recylerHomeMessages.setAdapter(homeMessagesAdapter);

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
                    //Toast.makeText(context, ""+view.getId(), Toast.LENGTH_SHORT).show();
                    serverCallForQuestionList(allSymtoms.get(view.getId()));
                    /*symptoms.remove(symptoms.get(view.getId()));
                    symptoms.add(0,symptoms.get(view.getId()));*/

                    /*setUpSymptomsInView();*/
                }
            });


            fragmentHomeBinding.layout.addView(textView);
        }


        /*textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_type = textView.getText().toString();
                serverCallForQuestionList(post_type);
            }
        });*/

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



   /* private void prepareMessageData() {
        HomeMessagesListModel homeMessagesListModel = new HomeMessagesListModel(R.drawable.ic_home_profile_one, "Cancer");
        homeMessagesList.add(homeMessagesListModel);

        homeMessagesListModel = new HomeMessagesListModel(R.drawable.ic_home_profile_two, "Diabetes");
        homeMessagesList.add(homeMessagesListModel);

        homeMessagesListModel = new HomeMessagesListModel(R.drawable.ic_home_profile_three, "Aids");
        homeMessagesList.add(homeMessagesListModel);

        homeMessagesListModel = new HomeMessagesListModel(R.drawable.ic_home_profile_three, "Aids");
        homeMessagesList.add(homeMessagesListModel);
    }*/

    private void setClickListener() {

        fragmentHomeBinding.ivStartConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, StartConversationActivity.class);
                startActivity(i);
            }
        });

        fragmentHomeBinding.ivMapUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preferences.edit().putBoolean("Change", false).apply();
                Fragment fragment = new UserMapLocationFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        fragmentHomeBinding.txtLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_type = fragmentHomeBinding.txtLatest.getText().toString();
                serverCallForQuestionList(post_type);
            }
        });

        fragmentHomeBinding.txtTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                post_type = fragmentHomeBinding.txtTrending.getText().toString();
                serverCallForQuestionList(post_type);
            }
        });

        showCommentListener = new ShowCommentListener() {
            @Override
            public void showComment(String post_user_id, String post_id, String profile_pic, String name
                    , String time_duration, String question, String number_of_likes
                    , String number_of_comment, String number_of_view, String post_like, String question_img, String report, String display_simul, String post_Unfav) {

                serverCallForNumberOfView(post_id);

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
                i.putExtra("Question_img", question_img);
                i.putExtra(ServerCredentials.REPORT, report);
                i.putExtra(ServerCredentials.DISPLAY_SIMUL, display_simul);
                i.putExtra("post_Unfav",post_Unfav);
                startActivity(i);

            }
        };

        postLikeListener = new PostLikeListener() {
            @Override
            public void likePost(String post_id, String status) {
                serverCallForLikePostQuestion(post_id, status);
            }
        };

        openDialogListner = new OpenDialogListner() {
            @Override
            public void dialogDetails(final String post_user_id, final String post_id, final String name, final String time_duration, final String question_image, String symptoms, final String question, final String number_of_cooment, final String number_of_likes, final String profile_pic, final String getPost_like, final String report, final String display_symtoms, final String number_of_view ,final String post_Unlike) {

                if (getPost_like.equals("1")) {
                    post_like = true;
                } else {
                    post_like = false;
                }

                final Dialog dialog = new Dialog(context, R.style.full_screen_dialog);

                ColorDrawable dialogColor = new ColorDrawable(Color.TRANSPARENT);
                dialogColor.setAlpha(220);

                dialog.getWindow().setBackgroundDrawable(dialogColor);

                dialog.setContentView(R.layout.dialog_show_full_post);

                dialog.setTitle("Custom Dialog");

                dialog.show();

                Window window = dialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
                window.setGravity(Gravity.CENTER);

                RelativeLayout relMain = dialog.findViewById(R.id.rel_main);


                ImageView iv_dialog_back = dialog.findViewById(R.id.iv_dialog_back);
                ImageView iv_postForword = dialog.findViewById(R.id.iv_postForword);
                ImageView iv_postInfo = dialog.findViewById(R.id.iv_postInfo);
                CircleImageView iv_msgProfilePic = dialog.findViewById(R.id.iv_msgProfilePic);
                TextView txt_msgTime = dialog.findViewById(R.id.txt_msgTime);
                final TextView txt_postUserName = dialog.findViewById(R.id.txt_postUserName);
                TextView txt_helthIsuusName = dialog.findViewById(R.id.txt_helthIsuusName);
                final TextView txt_postMsg = dialog.findViewById(R.id.txt_postMsg);
                final TextView txt_totalComments = dialog.findViewById(R.id.txt_totalComments);
                final TextView txt_like = dialog.findViewById(R.id.txt_like);
                final TextView txt_disLike = dialog.findViewById(R.id.txt_disLike);
                final ImageView iv_questionImg = dialog.findViewById(R.id.iv_questionImg);

                Picasso.get().load(profile_pic).into(iv_msgProfilePic);
                txt_msgTime.setText(time_duration);
                txt_postUserName.setText(name);
                txt_helthIsuusName.setText(symptoms);
                txt_postMsg.setText(question);
                txt_totalComments.setText(number_of_cooment);
                txt_like.setText(number_of_likes);

                if (symptoms.equals("Diabetes")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#0296FF"));
                } else if (symptoms.equals("Cancer")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#FFD519"));
                } else if (symptoms.equals("Mental Health")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#4CD964"));
                } else if (symptoms.equals("Genetic Discordes")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#B00296FF"));
                } else if (symptoms.equals("Age Associated Dieases")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#4730B8"));
                } else if (symptoms.equals("Heart Diases")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#FB496D"));
                } else if (symptoms.equals("MND")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#056CFE"));
                } else if (symptoms.equals("Asthma")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#4CD964"));
                } else if (symptoms.equals("Trauma")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#3C81B5"));
                } else if (symptoms.equals("Tumors")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#FFD519"));
                } else if (symptoms.equals("Obesity")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#4730B8"));
                } else if (symptoms.equals("Sexually Transmitted Diseases")) {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#BFFB496D"));
                } else {
                    txt_helthIsuusName.setTextColor(Color.parseColor("#BFFB496D"));
                }

                if (display_symtoms.equals("Diabetes")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#0296FF"));
                } else if (display_symtoms.equals("Cancer")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#FFD519"));
                } else if (display_symtoms.equals("Mental Health")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#4CD964"));
                } else if (display_symtoms.equals("Genetic Discordes")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
                } else if (display_symtoms.equals("Age Associated Dieases")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#4730B8"));
                } else if (display_symtoms.equals("Heart Diases")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#FB496D"));
                } else if (display_symtoms.equals("MND")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#056CFE"));
                } else if (display_symtoms.equals("Asthma")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#4CD964"));
                } else if (display_symtoms.equals("Trauma")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
                } else if (display_symtoms.equals("Tumors")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#FFD519"));
                } else if (display_symtoms.equals("Obesity")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#4730B8"));
                } else if (display_symtoms.equals("Sexually Transmitted Diseases")) {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
                } else {
                    iv_msgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
                }

                if (getPost_like.equals("1")) {
                    Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                    txt_like.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                    Drawable img1 = context.getResources().getDrawable(R.drawable.ic_white_thumb_down);
                    txt_disLike.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);

                } else {
                    Drawable img = context.getResources().getDrawable(R.drawable.ic_white_thumb_up);
                    txt_like.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                    Drawable img1 = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
                    txt_disLike.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);

                }

                if (question_image.equals("No_Image")) {
                    iv_questionImg.setVisibility(View.GONE);
                } else {
                    iv_questionImg.setVisibility(View.VISIBLE);
                    Picasso.get().load(question_image).into(iv_questionImg);
                    photoViewAttacher = new PhotoViewAttacher(iv_questionImg);
                    photoViewAttacher.update();
                }

                if (report.equals("0")) {
                    report_check = false;
                } else {
                    report_check = true;
                }

                iv_msgProfilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, ViewOthersProfileActivity.class);
                        i.putExtra(ServerCredentials.VIEW_USER_ID, post_user_id);
                        context.startActivity(i);
                    }
                });

                txt_postMsg.setOnClickListener(new View.OnClickListener() {
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
                        i.putExtra(ServerCredentials.NUM_OF_COMMENT, number_of_cooment);
                        i.putExtra(ServerCredentials.NUM_OF_VIEW, number_of_view);
                        i.putExtra(ServerCredentials.POST_LIKE, getPost_like);
                        i.putExtra("Question_img", question_image);
                        i.putExtra(ServerCredentials.REPORT, report);
                        i.putExtra(ServerCredentials.DISPLAY_SIMUL, display_symtoms);
                        i.putExtra("post_Unfav", post_Unlike);
                        startActivity(i);
                    }
                });

                txt_totalComments.setOnClickListener(new View.OnClickListener() {
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
                        i.putExtra(ServerCredentials.NUM_OF_COMMENT, number_of_cooment);
                        i.putExtra(ServerCredentials.NUM_OF_VIEW, number_of_view);
                        i.putExtra(ServerCredentials.POST_LIKE, getPost_like);
                        i.putExtra("Question_img", question_image);
                        i.putExtra(ServerCredentials.REPORT, report);
                        i.putExtra(ServerCredentials.DISPLAY_SIMUL, display_symtoms);
                        i.putExtra("post_Unfav", post_Unlike);
                        startActivity(i);
                    }
                });


                iv_postInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!report_check) {
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
                                    serverCallForQuestionReport(user_id, post_id);
                                    dialog.dismiss();
                                    report_check = true;

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
                    }
                });

                iv_postForword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Uri bmpUri = getLocalBitmapUri(iv_questionImg);
                        if (bmpUri != null) {
                            // Construct a ShareIntent with link to image
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Question");
                            String shareMessage= txt_postMsg.getText().toString();
                            //shareMessage = "Post Message : "+shareMessage + "\n\n" + txt_like.getText().toString() +" Likes, " +txt_totalComments.getText().toString()+" Comments"+"\n\n" + "https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu";
                            shareMessage = "Post Message : "+shareMessage + "\n\n" + txt_like.getText().toString() +" Likes, " +txt_totalComments.getText().toString()+" Comments" + "\n\nPost by : "+txt_postUserName.getText().toString();
                            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
                            shareIntent.setType("image/*");
                            startActivity(Intent.createChooser(shareIntent, "Share Image"));
                        } else {
                            // ...sharing failed, handle error
                        }

                    }
                });

                txt_like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!post_like) {
                            Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                            txt_like.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                            Drawable img1 = context.getResources().getDrawable(R.drawable.ic_white_thumb_down);
                            txt_disLike.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);

                            int like = Integer.valueOf(txt_like.getText().toString());
                            int count = like + 1;
                            txt_like.setText("" + count);
                            post_like = true;
                        }
                        serverCallForLikePostQuestion(post_id, "1");
                    }
                });

                txt_disLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Drawable img = context.getResources().getDrawable(R.drawable.ic_white_thumb_up);
                        txt_like.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);

                        Drawable img1 = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
                        txt_disLike.setCompoundDrawablesWithIntrinsicBounds(img1, null, null, null);

                        if (post_like) {
                            int like = Integer.valueOf(txt_like.getText().toString());
                            int count = like - 1;
                            txt_like.setText("" + count);
                            post_like = false;

                            serverCallForLikePostQuestion(post_id, "0");
                        }

                    }
                });

                iv_dialog_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        };

        fragmentHomeBinding.recylerHomeMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isUserScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isUserScrolling) {
                    if (dy > 0.0) {
                        //Scroll list Down
                        fragmentHomeBinding.linWelcomeMsg.setVisibility(View.GONE);
                        fragmentHomeBinding.linWelcomeMsg.animate()
                                .translationY(fragmentHomeBinding.linWelcomeMsg.getHeight())
                                .alpha(0.0f)
                                .setDuration(300)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        fragmentHomeBinding.linWelcomeMsg.setVisibility(View.GONE);
                                    }
                                });
                    } else {
                        if (!recyclerView.canScrollVertically(-1)) {

                            //fragmentHomeBinding.linWelcomeMsg.setVisibility(View.VISIBLE);
                            //Toast.makeText(context, "Top", Toast.LENGTH_SHORT).show();
                            //This Your Top View do Something
                        }
                    }
                }
            }
        });
    }

    private void serverCallForQuestionReport(final String user_id, final String post_id) {

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


    private void serverCallForLikePostQuestion(final String post_id, final String status) {

        if (CommonMethods.checkInternetConnection(context)) {

            fragmentHomeBinding.pbHome.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_LIKE_POST_QUE;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentHomeBinding.pbHome.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            // serverCallForQuestionList(post_type);


                        } else {
                           /* if (status.equals("0"))
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();*/

                        }

                    } catch (JSONException e) {
                        fragmentHomeBinding.pbHome.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fragmentHomeBinding.pbHome.setVisibility(View.GONE);
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

    private void serverCallForNumberOfView(final String post_id) {

        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_NUMBER_OF_VIEW;

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

    private void serverCallForQuestionList(final String post_type) {

        if (CommonMethods.checkInternetConnection(context)) {

            fragmentHomeBinding.pbHome.setVisibility(View.VISIBLE);

            homeMessagesList.clear();

            String url = ServerUrls.SIMUL_POST_QUE_LIST;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentHomeBinding.pbHome.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {

                            fragmentHomeBinding.recylerHomeMessages.setVisibility(View.VISIBLE);
                            fragmentHomeBinding.linPostNotFound.setVisibility(View.GONE);

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                HomeMessagesListModel homeMessagesListModel = new HomeMessagesListModel();

                                String post_id = dataObject.getString(ServerCredentials.POST_ID);
                                String user_id = dataObject.getString(ServerCredentials.USER_ID);
                                String question = dataObject.getString(ServerCredentials.QUESTION);
                                String symptoms = dataObject.getString(ServerCredentials.SYMPTOMS);
                                String time_duration = dataObject.getString(ServerCredentials.TIME_DURATION);
                                String number_of_comment = dataObject.getString(ServerCredentials.NUM_OF_COMMENT);
                                String number_of_likes = dataObject.getString(ServerCredentials.NUM_OF_LIKES);
                                String number_of_view = dataObject.getString(ServerCredentials.NUM_OF_VIEW);
                                String profile_pic = dataObject.getString(ServerCredentials.PROFILE_PIC);
                                String name = dataObject.getString(ServerCredentials.NAME);

                                String post_like = null;
                                if (dataObject.has("post_fav")) {
                                    post_like = dataObject.getString(ServerCredentials.POST_FAV);
                                } else {
                                    post_like = "0";
                                }


                                String post_UnFav = null;

                                if (dataObject.has("post_UnFav")) {
                                    post_UnFav = "1";
                                } else {
                                    post_UnFav = "0";
                                }

                                String question_img = null;
                                if (dataObject.has("question_img")) {
                                    question_img = dataObject.getString("question_img");
                                } else {
                                    question_img = "No_Image";
                                }
                                String report = dataObject.getString(ServerCredentials.REPORT);
                                String display_simul = dataObject.getString(ServerCredentials.DISPLAY_SIMUL);

                                homeMessagesListModel.setPost_id(post_id);
                                homeMessagesListModel.setPost_user_id(user_id);
                                homeMessagesListModel.setQuestion(question);
                                homeMessagesListModel.setSymptoms(symptoms);
                                homeMessagesListModel.setTime_duration(time_duration);
                                homeMessagesListModel.setNumber_of_comment(number_of_comment);
                                homeMessagesListModel.setNumber_of_likes(number_of_likes);
                                homeMessagesListModel.setNumber_of_view(number_of_view);
                                homeMessagesListModel.setProfile_pic(profile_pic);
                                homeMessagesListModel.setName(name);
                                homeMessagesListModel.setPost_like(post_like);
                                homeMessagesListModel.setQuestion_img(question_img);
                                homeMessagesListModel.setReport(report);
                                homeMessagesListModel.setDisplay_simul(display_simul);
                                homeMessagesListModel.setPost_UnFav(post_UnFav);

                                homeMessagesList.add(homeMessagesListModel);

                            }

                            homeMessagesAdapter = new HomeMessagesAdapter(context, homeMessagesList);
                            fragmentHomeBinding.recylerHomeMessages.setLayoutManager(new LinearLayoutManager(context));
                            fragmentHomeBinding.recylerHomeMessages.setAdapter(homeMessagesAdapter);


                        } else {
                            fragmentHomeBinding.recylerHomeMessages.setVisibility(View.GONE);
                            fragmentHomeBinding.linPostNotFound.setVisibility(View.VISIBLE);
                          //  Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        fragmentHomeBinding.pbHome.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fragmentHomeBinding.pbHome.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);
                    params.put(ServerCredentials.POST_TYPE, post_type);//post_type
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
        // serverCallForQuestionList(post_type);
    }
}
