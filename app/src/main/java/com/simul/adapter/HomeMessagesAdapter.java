package com.simul.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simul.R;
import com.simul.activity.ViewOthersProfileActivity;
import com.simul.credentials.ServerCredentials;
import com.simul.databinding.AdapterHomeMsgListBinding;
import com.simul.fragment.HomeFragment;
import com.simul.model.HomeMessagesListModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomeMessagesAdapter extends RecyclerView.Adapter<HomeMessagesAdapter.MyViewHolder> {

    private Context context;
    private AdapterHomeMsgListBinding adapterHomeMsgListBinding;
    private ArrayList<HomeMessagesListModel> homeMessagesList;
    ArrayList<String> checkLike, checkDislike;

    public HomeMessagesAdapter(Context context, ArrayList<HomeMessagesListModel> homeMessagesList) {
        this.context = context;
        this.homeMessagesList = homeMessagesList;
        checkLike = new ArrayList<>();
        checkDislike = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterHomeMsgListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_home_msg_list, parent, false);
        return new MyViewHolder(adapterHomeMsgListBinding.getRoot());
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final HomeMessagesListModel homeMessagesListModel = homeMessagesList.get(position);

        Picasso.get().load(homeMessagesListModel.getProfile_pic()).into(adapterHomeMsgListBinding.ivMsgProfilePic);

        adapterHomeMsgListBinding.txtPostUserName.setText(homeMessagesListModel.getName());

        adapterHomeMsgListBinding.txtMsgTime.setText(homeMessagesListModel.getTime_duration());

        String post_img = homeMessagesListModel.getQuestion_img();

        if (post_img.equals("No_Image")) {
            adapterHomeMsgListBinding.ivQuestionImg.setVisibility(View.GONE);
        } else {
            adapterHomeMsgListBinding.ivQuestionImg.setVisibility(View.VISIBLE);
            Picasso.get().load(post_img).into(adapterHomeMsgListBinding.ivQuestionImg);
        }

        String helthIsuusName = homeMessagesListModel.getSymptoms();

        if (helthIsuusName.equals("Diabetes")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setText(homeMessagesListModel.getSymptoms());
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#0296FF"));
        } else if (helthIsuusName.equals("Cancer")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FFD519"));
        } else if (helthIsuusName.equals("Mental Health")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4CD964"));
        } else if (helthIsuusName.equals("Genetic Discordes")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#B00296FF"));
        } else if (helthIsuusName.equals("Age Associated Dieases")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4730B8"));
        } else if (helthIsuusName.equals("Heart Diases")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FB496D"));
        } else if (helthIsuusName.equals("MND")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#056CFE"));
        } else if (helthIsuusName.equals("Asthma")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4CD964"));
        } else if (helthIsuusName.equals("Trauma")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#3C81B5"));
        } else if (helthIsuusName.equals("Tumors")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#FFD519"));
        } else if (helthIsuusName.equals("Obesity")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#4730B8"));
        } else if (helthIsuusName.equals("Sexually Transmitted Diseases")) {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#BFFB496D"));
        } else {
            adapterHomeMsgListBinding.txtHelthIsuusName.setTextColor(Color.parseColor("#BFFB496D"));
        }

        String display_simul = homeMessagesListModel.getDisplay_simul();

        if (display_simul.equals("Diabetes")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            adapterHomeMsgListBinding.ivMsgProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        }

        adapterHomeMsgListBinding.txtHelthIsuusName.setText(homeMessagesListModel.getSymptoms());

        adapterHomeMsgListBinding.txtPostMsg.setText(homeMessagesListModel.getQuestion());

        adapterHomeMsgListBinding.txtTotalComments.setText(homeMessagesListModel.getNumber_of_comment());

        holder.txtLike.setText(homeMessagesListModel.getNumber_of_likes());

        if (homeMessagesListModel.getPost_UnFav().equals("1")) {
            Drawable img = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
            holder.txtDisLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            if (homeMessagesListModel.getPost_like().equals("1")) {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            }
        }

        adapterHomeMsgListBinding.txtTotalComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HomeFragment.showCommentListener.showComment(homeMessagesListModel.getPost_user_id(), homeMessagesListModel.getPost_id(),
                        homeMessagesListModel.getProfile_pic(), homeMessagesListModel.getName(),
                        homeMessagesListModel.getTime_duration(), homeMessagesListModel.getQuestion(),
                        homeMessagesListModel.getNumber_of_likes(), homeMessagesListModel.getNumber_of_comment(),
                        homeMessagesListModel.getNumber_of_view(), homeMessagesListModel.getPost_like(), homeMessagesListModel.getQuestion_img(), homeMessagesListModel.getReport()
                        , homeMessagesListModel.getDisplay_simul(),homeMessagesListModel.getPost_UnFav());

            }
        });

        adapterHomeMsgListBinding.ivQuestionImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment.openDialogListner.dialogDetails(homeMessagesListModel.getPost_user_id(), homeMessagesListModel.getPost_id(), homeMessagesListModel.getName()
                        , homeMessagesListModel.getTime_duration(), homeMessagesListModel.getQuestion_img()
                        , homeMessagesListModel.getSymptoms(), homeMessagesListModel.getQuestion(),
                        homeMessagesListModel.getNumber_of_comment(),
                        homeMessagesListModel.getNumber_of_likes(),
                        homeMessagesListModel.getProfile_pic(), homeMessagesListModel.getPost_like()
                        , homeMessagesListModel.getReport(), homeMessagesListModel.getDisplay_simul(), homeMessagesListModel.getNumber_of_view()
                ,homeMessagesListModel.getPost_UnFav());
            }
        });

        adapterHomeMsgListBinding.txtPostMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeFragment.showCommentListener.showComment(homeMessagesListModel.getPost_user_id(), homeMessagesListModel.getPost_id(),
                        homeMessagesListModel.getProfile_pic(), homeMessagesListModel.getName(),
                        homeMessagesListModel.getTime_duration(), homeMessagesListModel.getQuestion(),
                        homeMessagesListModel.getNumber_of_likes(), homeMessagesListModel.getNumber_of_comment(),
                        homeMessagesListModel.getNumber_of_view(), homeMessagesListModel.getPost_like(), homeMessagesListModel.getQuestion_img(), homeMessagesListModel.getReport()
                        , homeMessagesListModel.getDisplay_simul(),homeMessagesListModel.getPost_UnFav());

            }
        });

        if (homeMessagesListModel.getPost_like().equals("1")) {
            checkLike.add(homeMessagesListModel.getPost_id());
        }


        holder.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkLike.contains(homeMessagesListModel.getPost_id())) {
                    checkDislike.remove(homeMessagesListModel.getPost_id());
                    checkLike.add(homeMessagesListModel.getPost_id());
                    int total_like = Integer.valueOf(holder.txtLike.getText().toString());
                    total_like = total_like + 1;
                    if (total_like == 0)
                    {
                        total_like = total_like + 1;
                    }
                    HomeFragment.postLikeListener.likePost(homeMessagesListModel.getPost_id(), "1");
                    Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                    Drawable img2 = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_down);
                    holder.txtDisLike.setCompoundDrawablesWithIntrinsicBounds(img2, null, null, null);
                    holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    holder.txtLike.setText("" + total_like);
                }
            }
        });


        adapterHomeMsgListBinding.txtDisLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkLike.contains(homeMessagesListModel.getPost_id())) {
                    checkLike.remove(homeMessagesListModel.getPost_id());
                    checkDislike.add(homeMessagesListModel.getPost_id());
                    int total_like = Integer.valueOf(holder.txtLike.getText().toString());
                    total_like = total_like - 1;
                    if (total_like == 0)
                    {
                        total_like = total_like - 1;
                    }
                    HomeFragment.postLikeListener.likePost(homeMessagesListModel.getPost_id(), "0");
                    Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                    Drawable img2 = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
                    holder.txtDisLike.setCompoundDrawablesWithIntrinsicBounds(img2, null, null, null);
                    holder.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
                    holder.txtLike.setText("" + total_like);
                } else {
                    if (!checkDislike.contains(homeMessagesListModel.getPost_id())) {
                        checkDislike.add(homeMessagesListModel.getPost_id());
                        Drawable img2 = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
                        holder.txtDisLike.setCompoundDrawablesWithIntrinsicBounds(img2, null, null, null);
                        int total_like = Integer.valueOf(holder.txtLike.getText().toString());
                        total_like = total_like - 1;
                        if (total_like == 0)
                        {
                            total_like = total_like - 1;
                        }
                        holder.txtLike.setText("" + total_like);
                        HomeFragment.postLikeListener.likePost(homeMessagesListModel.getPost_id(), "0");
                    }
                }
            }
        });


        adapterHomeMsgListBinding.ivMsgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewOthersProfileActivity.class);
                i.putExtra(ServerCredentials.VIEW_USER_ID, homeMessagesListModel.getPost_user_id());
                context.startActivity(i);
            }
        });


        holder.setIsRecyclable(false);

    }

    @Override
    public int getItemCount() {
        return homeMessagesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtLike, txtDisLike;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLike = itemView.findViewById(R.id.txt_like);
            txtDisLike = itemView.findViewById(R.id.txt_disLike);
        }
    }
}
