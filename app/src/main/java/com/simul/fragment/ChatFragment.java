package com.simul.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simul.R;
import com.simul.adapter.ChatAdapter;
import com.simul.adapter.ChatUserAdapter;
import com.simul.databinding.FragmentChatBinding;
import com.simul.model.ChatListModel;
import com.simul.model.ChatModel;
import com.simul.model.UserModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    FragmentChatBinding fragmentChatBinding;

    private Context context;

    private ChatAdapter chatAdapter;

    private ChatUserAdapter userAdapter;
    private List<UserModel> mUsers;

    private FirebaseUser fuser;
    private DatabaseReference reference, ref;

    private List<ChatListModel> usersList;
    private int unread = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChatBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);

        idMapping();

        setClickListener();


        //setUpUnReadMsg();

        return fragmentChatBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpChatList();
    }

    private void setUpUnReadMsg() {

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    String res = chat.getReceiver();
                    String sen = fuser.getUid();

                    if (chat.getReceiver().equals(fuser.getUid()) && !chat.isIsseen()) {
                        unread++;
                        String g = "";
                    }
                    String i = "";
                }

                String j = "";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void idMapping() {

        context = getContext();
    }

    private void setUpChatList() {


        fragmentChatBinding.recylerChat.setHasFixedSize(true);
        fragmentChatBinding.recylerChat.setLayoutManager(new LinearLayoutManager(context));

        fragmentChatBinding.pbChatList.setVisibility(View.VISIBLE);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatListModel chatlist = snapshot.getValue(ChatListModel.class);
                    usersList.add(chatlist);
                }


                if (usersList.size() == 0) {
                    //Toast.makeText(context, "No Chat Found.", Toast.LENGTH_SHORT).show();
                    fragmentChatBinding.linChatNotFound.setVisibility(View.VISIBLE);
                } else {
                    fragmentChatBinding.linChatNotFound.setVisibility(View.GONE);
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //updateToken(FirebaseInstanceId.getInstance().getToken());
    }

    private void chatList() {

        fragmentChatBinding.pbChatList.setVisibility(View.VISIBLE);

        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);
                    for (ChatListModel chatlist : usersList) {
                        if(user.getId() != null){
                            if (user.getId().equals(chatlist.getId())) {
                                mUsers.add(user);
                            }
                        }

                    }
                }

                chatAdapter = new ChatAdapter(context, mUsers, true, unread);
                fragmentChatBinding.recylerChat.setAdapter(chatAdapter);

                fragmentChatBinding.pbChatList.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setClickListener() {

        fragmentChatBinding.ivStartConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new ChooseFriendFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
