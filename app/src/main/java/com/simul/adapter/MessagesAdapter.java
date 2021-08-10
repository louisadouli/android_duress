package com.simul.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.simul.R;
import com.simul.model.ChatModel;
import com.simul.model.MessagesModel;
import com.squareup.picasso.Picasso;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<ChatModel> mChat;
    private String imageurl;


    FirebaseUser fuser;

    public MessagesAdapter(Context mContext, List<ChatModel> mChat, String imageurl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new MessagesAdapter.MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new MessagesAdapter.MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
       /* MessagesModel messagesModel = mChat.get(position);
        holder.show_message.setText(messagesModel.getMessage());
        holder.txt_seen.setText(messagesModel.getTime());*/

        final ChatModel chat = mChat.get(position);

        if(chat.getMessage().equals("null")){
            holder.show_message.setVisibility(View.GONE);
            holder.txt_seen.setVisibility(View.GONE);
        }else {

            holder.show_message.setMaxLines(1);
            if(chat.getThumb().equals("0")){
                holder.show_message.setVisibility(View.VISIBLE);
                holder.show_thumb.setVisibility(View.GONE);
                holder.show_message.setText(chat.getMessage());
                holder.show_message.setMaxLines(Integer.MAX_VALUE);
            }else {
                //Picasso.get().load("android.resource://com.simul/drawable/ic_chat_hand").into(holder.show_thumb);
                holder.show_message.setVisibility(View.INVISIBLE);
                holder.show_thumb.setVisibility(View.VISIBLE);
                Picasso.get().load(chat.getMessage()).into(holder.show_thumb);
                holder.show_message.setMaxLines(1);
            }

        }


        if (position == mChat.size() - 1) {
            if (chat.isIsseen()) {
                holder.txt_seen.setText("Seen");
            } else {
                holder.txt_seen.setText("Delivered");
            }
        } else {
            if(chat.getTime().equals("")){
                holder.txt_seen.setText("");
            }else {
                holder.txt_seen.setText("Read "+chat.getTime());
            }
            //holder.txt_seen.setText("Seen");
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView show_message, txt_seen;
        public ImageView show_thumb;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            txt_seen = itemView.findViewById(R.id.txt_seen);
            show_thumb = itemView.findViewById(R.id.show_thumb);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mChat.get(position).getSender().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
