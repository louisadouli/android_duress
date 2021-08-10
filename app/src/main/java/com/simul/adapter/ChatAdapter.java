package com.simul.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simul.R;
import com.simul.activity.ChatMessagesActivity;
import com.simul.credentials.ServerCredentials;
import com.simul.databinding.AdapterChatListBinding;
import com.simul.model.ChatModel;
import com.simul.model.UserModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private AdapterChatListBinding adapterChatListBinding;

    private Context context;
    private List<UserModel> mUsers;
    private boolean ischat;
    private String theLastMessage, time , display_simul;
    private int unread = 1;

    public ChatAdapter(Context context, List<UserModel> mUsers, boolean ischat, int unread) {
        this.context = context;
        this.mUsers = mUsers;
        this.ischat = ischat;
        this.unread = unread;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterChatListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_chat_list, parent, false);
        return new ChatAdapter.MyViewHolder(adapterChatListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final UserModel user = mUsers.get(position);

        adapterChatListBinding.txtUserName.setText(user.getUsername());

        if (user.getImageURL().equals("default")) {
            adapterChatListBinding.ivUserProfilePic.setImageResource(R.drawable.ic_dummy);
        } else {
            Picasso.get().load(user.getImageURL()).placeholder(R.drawable.ic_dummy).into(adapterChatListBinding.ivUserProfilePic);
        }

        display_simul = user.getDisplay_simul();

        if (display_simul.equals("Diabetes")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            adapterChatListBinding.ivUserProfilePic.setBorderColor(Color.parseColor("#BFFB496D"));
        }

        int newPosition = holder.getAdapterPosition();
        if (ischat) {
            lastMessage(user.getId(), adapterChatListBinding.txtLastMsg,adapterChatListBinding.txtNumberOfMessages,adapterChatListBinding.txtTime,newPosition);
        } else {
            adapterChatListBinding.txtLastMsg.setVisibility(View.GONE);
        }



       /* if (ischat) {
            if (user.getStatus().equals("online")) {
                adapterChatListBinding.txtNumberOfMessages.setVisibility(View.VISIBLE);
            } else {
               // adapterChatListBinding.txtNumberOfMessages.setVisibility(View.GONE);
                adapterChatListBinding.txtNumberOfMessages.setVisibility(View.VISIBLE);
            }
        } else {
           // adapterChatListBinding.txtNumberOfMessages.setVisibility(View.GONE);
            adapterChatListBinding.txtNumberOfMessages.setVisibility(View.VISIBLE);
        }*/

       // adapterChatListBinding.txtNumberOfMessages.setText(String.valueOf(countList.get(position)));

      /* if(unread == 0){
           //Toast.makeText(context, "No Unread Msg", Toast.LENGTH_SHORT).show();
       }else {
           adapterChatListBinding.txtNumberOfMessages.setText(user.getLastMsgTime());
       }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ChatMessagesActivity.class);
                i.putExtra("userid", user.getId());
                i.putExtra(ServerCredentials.DISPLAY_SIMUL,user.getDisplay_simul());
                i.putExtra(ServerCredentials.ID,user.getUserId());
                context.startActivity(i);
            }
        });

        //adapterChatListBinding.txtTime.setText(user.getLastMsgTime());

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg, final TextView count_text, final TextView txt_time, final int newPosition) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);

                    if (firebaseUser != null) {
                        if (chat.getReceiver() == null) {
                            theLastMessage = "default";
                        } else if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            if(chat.getMessage().equals("null")){
                                theLastMessage = "default";
                            }else {
                                theLastMessage = chat.getMessage();
                                time = chat.getTime();
                            }

                        }
                    }
                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("");
                        txt_time.setText("");
                        break;

                    default:
                        if (theLastMessage.equals("android.resource://com.simul/drawable/ic_chat_hand")){
                            last_msg.setText("You Send Like...");
                        }else {
                            last_msg.setText(theLastMessage);
                        }
                        //last_msg.setText(theLastMessage);
                        txt_time.setText(time);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (firebaseUser != null) {
                        if (chat.getReceiver() != null){
                            if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)  && !chat.isIsseen() && !chat.getMessage().equals("null")) {
                                unread++;
                                String g = "";
                            }
                        }

                    }

                }
               // unread = unread/2;
                count_text.setText(String.valueOf(unread));
                if (unread == 0){
                    count_text.setVisibility(View.GONE);
                }else {
                    count_text.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
