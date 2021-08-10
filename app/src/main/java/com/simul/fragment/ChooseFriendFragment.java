package com.simul.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.simul.R;
import com.simul.adapter.ChatUserAdapter;
import com.simul.databinding.FragmentChooseFriendBinding;
import com.simul.model.UserModel;

import java.util.ArrayList;
import java.util.List;

import static com.simul.activity.MainActivity.selection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseFriendFragment extends Fragment {

    private FragmentChooseFriendBinding fragmentChooseFriendBinding;

    private Context context;

    private ChatUserAdapter userAdapter;

    private List<UserModel> mUsers;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChooseFriendBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_choose_friend, container, false);

        idMapping();

        setClickListener();

        return fragmentChooseFriendBinding.getRoot();
    }

    private void idMapping() {

        context = getContext();

        selection = 4;

        fragmentChooseFriendBinding.recylerChooseFriend.setHasFixedSize(true);
        fragmentChooseFriendBinding.recylerChooseFriend.setLayoutManager(new LinearLayoutManager(context));

        mUsers = new ArrayList<>();

        readUsers();

    }

    private void readUsers() {

        fragmentChooseFriendBinding.pbChooseFrd.setVisibility(View.VISIBLE);
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);

                    assert user != null;
                    assert firebaseUser != null;

                    if(user.getId() != null){
                        if (!user.getId().equals(firebaseUser.getUid())) {
                            mUsers.add(user);
                        }
                    }


                }

                userAdapter = new ChatUserAdapter(context, mUsers, false);
                fragmentChooseFriendBinding.recylerChooseFriend.setAdapter(userAdapter);
                fragmentChooseFriendBinding.pbChooseFrd.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setClickListener() {

        fragmentChooseFriendBinding.edtSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s).endAt(s + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserModel user = snapshot.getValue(UserModel.class);

                    assert user != null;
                    assert fuser != null;
                    if (!user.getId().equals(fuser.getUid())) {
                        mUsers.add(user);
                    }
                }

                userAdapter = new ChatUserAdapter(context, mUsers, false);
                fragmentChooseFriendBinding.recylerChooseFriend.setAdapter(userAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
