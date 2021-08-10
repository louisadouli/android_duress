package com.simul.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simul.R;
import com.simul.databinding.AdapterAnswerListBinding;
import com.simul.fragment.ProfileFragment;
import com.simul.model.AnswerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.MyViewHolder> {

    AdapterAnswerListBinding adapterAnswerListBinding;
    private Context context;
    private ArrayList<AnswerModel> answerModelList;

    public AnswerAdapter(Context context, ArrayList<AnswerModel> answerModelList) {
        this.context = context;
        this.answerModelList = answerModelList;
    }

    @NonNull
    @Override
    public AnswerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterAnswerListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_answer_list, parent, false);
        return new AnswerAdapter.MyViewHolder(adapterAnswerListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull AnswerAdapter.MyViewHolder holder, int position) {

        final AnswerModel answerModel = answerModelList.get(position);

        adapterAnswerListBinding.txtName.setText(answerModel.getName());
        adapterAnswerListBinding.txtTime.setText(answerModel.getTime_duration());
        adapterAnswerListBinding.txtFullMsg.setText(answerModel.getComment());
        adapterAnswerListBinding.txtLike.setText(answerModel.getNumber_of_likes());
        Picasso.get().load(answerModel.getProfile_pic()).into(adapterAnswerListBinding.ivAnsProfilePic);

        if (answerModel.getComment_DisLike().equals("1")) {
            Drawable img = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
            adapterAnswerListBinding.txtDislike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            if (answerModel.getComment_status().equals("1")) {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                adapterAnswerListBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                adapterAnswerListBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            }
        }

        String display_simul = answerModel.getDisplay_simul();

        if (display_simul.equals("Diabetes")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            adapterAnswerListBinding.ivAnsProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        }

        adapterAnswerListBinding.txtFullMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment.showCommentListener.showComment(answerModel.getPost_user_id(),
                        answerModel.getPost_id(), answerModel.getProfile_pic(),
                        answerModel.getName(), answerModel.getTime_duration(),
                        answerModel.getQuestion(), answerModel.getQuestion_number_of_likes(),
                        answerModel.getQuestion_number_of_comment(), answerModel.getQuestion_number_of_view()
                        , answerModel.getPost_status(), answerModel.getQuestion_image(), answerModel.getPost_report(), answerModel.getDisplay_simul(), answerModel.getPost_UnLike());
            }
        });


        adapterAnswerListBinding.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment.commentLikeListener.likeComment(answerModel.getComment_id(), "1");
            }
        });


        adapterAnswerListBinding.txtDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment.commentLikeListener.likeComment(answerModel.getComment_id(), "0");
            }
        });


    }

    @Override
    public int getItemCount() {
        return answerModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
