package com.simul.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simul.R;
import com.simul.activity.CommentActivity;
import com.simul.activity.EditDeleteCommentActivity;
import com.simul.activity.ViewOthersProfileActivity;
import com.simul.credentials.ServerCredentials;
import com.simul.databinding.AdapterCommentListBinding;
import com.simul.model.CommentModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private AdapterCommentListBinding adapterCommentListBinding;
    private Context context;
    private ArrayList<CommentModel> commentModelArrayList;
    private String userId;
    private String question;


    public CommentAdapter(Context context, ArrayList<CommentModel> commentModelArrayList, String user_id, String que) {
        this.context = context;
        this.commentModelArrayList = commentModelArrayList;
        userId = user_id;
        question = que;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterCommentListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_comment_list, parent, false);
        return new CommentAdapter.MyViewHolder(adapterCommentListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final CommentModel commentModel = commentModelArrayList.get(position);

        if (userId.equals(commentModel.getUser_id())) {
            adapterCommentListBinding.txtEdit.setVisibility(View.VISIBLE);
            adapterCommentListBinding.ivComentInfo.setVisibility(View.GONE);
            adapterCommentListBinding.ivComentForword.setVisibility(View.GONE);
        } else {
            adapterCommentListBinding.txtEdit.setVisibility(View.GONE);
            adapterCommentListBinding.ivComentInfo.setVisibility(View.VISIBLE);
            adapterCommentListBinding.ivComentForword.setVisibility(View.VISIBLE);
        }

        Picasso.get().load(commentModel.getProfile_pic()).into(adapterCommentListBinding.ivCommentProfilePic);

        adapterCommentListBinding.txtName.setText(commentModel.getName());

        adapterCommentListBinding.txtFullMsg.setText(commentModel.getComment());

        adapterCommentListBinding.txtTime.setText(commentModel.getTime_duration());

        String display_simul = commentModel.getDisplay_simul();

        if (display_simul.equals("Diabetes")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            adapterCommentListBinding.ivCommentProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        }

        if (commentModel.getComment_DisLike().equals("1")) {
            Drawable img = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
            adapterCommentListBinding.txtDislike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            if (commentModel.getComment_like().equals("1")) {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                adapterCommentListBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                adapterCommentListBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            }
        }

        adapterCommentListBinding.txtLike.setText(commentModel.getNumber_of_likes());

        adapterCommentListBinding.ivCommentProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewOthersProfileActivity.class);
                i.putExtra(ServerCredentials.VIEW_USER_ID, commentModel.getUser_id());
                context.startActivity(i);
            }
        });

        adapterCommentListBinding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EditDeleteCommentActivity.class);
                i.putExtra(ServerCredentials.COMMENT, commentModel.getComment());
                i.putExtra(ServerCredentials.COMMENT_ID, commentModel.getComment_id());
                context.startActivity(i);
            }
        });


        adapterCommentListBinding.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentActivity.commentLikeListener.likeComment(commentModel.getComment_id(), "1");
            }
        });


        adapterCommentListBinding.txtDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentActivity.commentLikeListener.likeComment(commentModel.getComment_id(), "0");
            }
        });


        adapterCommentListBinding.ivComentForword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Question");
                String shareMessage = commentModel.getComment();
                //shareMessage = shareMessage + "\n\n" + commentModel.getComment_like() + " Likes " + "\n\n" + "https://play.google.com/store/apps/details?id=nic.goi.aarogyasetu";
                shareMessage = "Post Message : "+question+"\n\nComment : " + shareMessage + "\n\n" + commentModel.getComment_like() + " Likes " + "\n\nComment by : " + commentModel.getName();
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareMessage);
                context.startActivity(Intent.createChooser(shareIntent, "Share Comment..."));

            }
        });


        adapterCommentListBinding.ivComentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentModel.getReport().equals("0")) {
                    Toast.makeText(context, "Yoy are already report this comment.", Toast.LENGTH_SHORT).show();
                } else {
                    CommentActivity.commentReportListener.reportComment(commentModel.getComment_id());
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
