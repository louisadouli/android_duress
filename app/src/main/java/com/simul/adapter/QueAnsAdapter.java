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
import com.simul.databinding.AdapterQueAnsListBinding;
import com.simul.fragment.ProfileFragment;
import com.simul.model.QueAnsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class QueAnsAdapter extends RecyclerView.Adapter<QueAnsAdapter.MyViewHolder> {

    private AdapterQueAnsListBinding adapterQueAnsListBinding;
    private Context context;
    private ArrayList<QueAnsModel> queAnsModelList;

    public QueAnsAdapter(Context context, ArrayList<QueAnsModel> queAnsModelList) {
        this.context = context;
        this.queAnsModelList = queAnsModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterQueAnsListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_que_ans_list, parent, false);
        return new QueAnsAdapter.MyViewHolder(adapterQueAnsListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final QueAnsModel queAnsModel = queAnsModelList.get(position);

        adapterQueAnsListBinding.txtName.setText(queAnsModel.getName());
        adapterQueAnsListBinding.txtTime.setText(queAnsModel.getTime_duration());
        adapterQueAnsListBinding.txtFullMsg.setText(queAnsModel.getQuestion());
        adapterQueAnsListBinding.txtLike.setText(queAnsModel.getNumber_of_likes());
        Picasso.get().load(queAnsModel.getProfile_pic()).into(adapterQueAnsListBinding.ivQueProfilePic);

        if (queAnsModel.getPost_UnFav().equals("1")) {
            Drawable img = context.getResources().getDrawable(R.drawable.ic_thumbdown_fill_orange);
            adapterQueAnsListBinding.txtDislike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        } else {
            if (queAnsModel.getStatus().equals("1")) {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_already_fav);
                adapterQueAnsListBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            } else {
                Drawable img = context.getResources().getDrawable(R.drawable.ic_new_one_thumbs_up);
                adapterQueAnsListBinding.txtLike.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
            }
        }


        String display_simul = queAnsModel.getDisplay_simul();
        String check = "";

        if (display_simul.equals("Diabetes")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            adapterQueAnsListBinding.ivQueProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        }

       /* adapterQueAnsListBinding.ivQueProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewOthersProfileActivity.class);
                context.startActivity(i);
            }
        });*/

        adapterQueAnsListBinding.txtLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment.postLikeListener.likePost(queAnsModel.getPost_id(), "1");
            }
        });


        adapterQueAnsListBinding.txtDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oo = queAnsModel.getPost_id();
                ProfileFragment.postLikeListener.likePost(queAnsModel.getPost_id(), "0");
            }
        });


        adapterQueAnsListBinding.txtFullMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment.showCommentListener.showComment(queAnsModel.getUser_id(),
                        queAnsModel.getPost_id(), queAnsModel.getProfile_pic(), queAnsModel.getName(), queAnsModel.getTime_duration(),
                        queAnsModel.getQuestion(), queAnsModel.getNumber_of_likes(), queAnsModel.getNumber_of_comment(), queAnsModel.getNumber_of_view(),
                        queAnsModel.getStatus(), queAnsModel.getQuestion_img(), queAnsModel.getReport(), queAnsModel.getDisplay_simul(),queAnsModel.getPost_UnFav());
            }
        });
    }

    @Override
    public int getItemCount() {
        return queAnsModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
