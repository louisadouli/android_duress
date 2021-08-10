package com.simul.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.simul.R;
import com.simul.databinding.ActivityMainBinding;
import com.simul.fragment.ChatFragment;
import com.simul.fragment.HomeFragment;
import com.simul.fragment.NotificationFragment;
import com.simul.fragment.ProfileFragment;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding activityMainBinding;

    Context context;

    FirebaseAuth auth;

    String token, currentUserId;

    FragmentManager fragmentManager;

    public static int selection = 0;

    DatabaseReference reference;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        activityMainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);

        idMapping();

        bottomNavigationItemSelection();

        loadFragment(new HomeFragment());

        activityMainBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;

        fragmentManager = getSupportFragmentManager();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

    }

    private void bottomNavigationItemSelection() {

        activityMainBinding.navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment = fragmentManager.findFragmentById(menuItem.getItemId());

                switch (menuItem.getItemId()){
                    case R.id.menu_home:
                        if (fragment == null) {
                            fragment = new HomeFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        }
                        loadFragment(fragment);
                        selection = 0;
                        return true;

                    case R.id.menu_chat:
                        if (fragment == null) {
                            fragment = new ChatFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        }
                        loadFragment(fragment);
                        selection = 1;
                        return true;

                    case R.id.menu_notification:
                        if (fragment == null) {
                            fragment = new NotificationFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        }
                        loadFragment(fragment);
                        selection = 2;
                        return true;

                    case R.id.menu_profile:
                        if (fragment == null) {
                            fragment = new ProfileFragment();
                            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                        }
                        loadFragment(fragment);
                        selection = 3;
                        return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MenuItem homeItem = activityMainBinding.navigationView.getMenu().getItem(0);
        if (selection == 0) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            super.onBackPressed();
            finish();
        } else if (selection == 1 || selection == 2 || selection == 3) {
            activityMainBinding.navigationView.setSelectedItemId(homeItem.getItemId());
            loadFragment(new HomeFragment());
        } else {

        }
    }

    private void status(String status){
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("status",status);
        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
