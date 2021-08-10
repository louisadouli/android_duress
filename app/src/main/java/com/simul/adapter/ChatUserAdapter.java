package com.simul.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.simul.databinding.AdapterUserListItemBinding;
import com.simul.model.ChatModel;
import com.simul.model.UserModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder> {

    AdapterUserListItemBinding adapterChatListBinding;

    private Context mContext;
    private List<UserModel> mUsers;
    private boolean ischat;
    private String theLastMessage,display_simul;

    public ChatUserAdapter(Context mContext, List<UserModel> mUsers, boolean ischat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public ChatUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterChatListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_user_list_item, parent, false);
        return new ChatUserAdapter.ViewHolder(adapterChatListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserModel user = mUsers.get(position);

        adapterChatListBinding.rbChatUser.setText(user.getUsername());

        if (user.getImageURL().equals("default")) {
            adapterChatListBinding.ivProfileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            //Glide.with(mContext).load(user.getImageURL()).into(holder.profile_image);
            Picasso.get().load(user.getImageURL()).into(adapterChatListBinding.ivProfileImage);
        }

        display_simul = user.getDisplay_simul();

        if (display_simul.equals("Diabetes")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            adapterChatListBinding.ivProfileImage.setBorderColor(Color.parseColor("#BFFB496D"));
        }

        adapterChatListBinding.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ChatMessagesActivity.class);
                i.putExtra("userid", user.getId());
                i.putExtra(ServerCredentials.DISPLAY_SIMUL,user.getDisplay_simul());
                i.putExtra(ServerCredentials.ID,user.getUserId());
                mContext.startActivity(i);
            }
        });

        adapterChatListBinding.rbChatUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, ChatMessagesActivity.class);
                i.putExtra("userid", user.getId());
                i.putExtra(ServerCredentials.DISPLAY_SIMUL,user.getDisplay_simul());
                i.putExtra(ServerCredentials.ID,user.getUserId());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private void lastMessage(final String userid, final TextView last_msg) {
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);

                    if(firebaseUser != null){
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            theLastMessage = chat.getMessage();
                        }
                    }

                }

                switch (theLastMessage) {
                    case "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
