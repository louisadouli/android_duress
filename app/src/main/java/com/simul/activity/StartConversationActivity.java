package com.simul.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.simul.R;
import com.simul.databinding.ActivityStartConversationBinding;
import com.simul.fragment.ConversationOneFragment;

public class StartConversationActivity extends AppCompatActivity {

    ActivityStartConversationBinding activityStartConversationBinding;

    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);
        activityStartConversationBinding = DataBindingUtil.setContentView(this,R.layout.activity_start_conversation);

        idMapping();

        setClickListener();

        loadFirstStep();

        activityStartConversationBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;
    }

    private void setClickListener() {

        activityStartConversationBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadFirstStep() {

        Fragment fragment = new ConversationOneFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_convesationContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
