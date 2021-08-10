package com.simul.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityWelcomeBinding activityWelcomeBinding;

    Context context;

    FirebaseUser firebaseUser;

    SharedPreferences preferences;

    boolean hasLoggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityWelcomeBinding = DataBindingUtil.setContentView(this,R.layout.activity_welcome);

        idMapping();

        setClickListener();

        getFirebaseToken();

        activityWelcomeBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        hasLoggedIn = preferences.getBoolean("hasLoggedIn", false);

        //check if user is null
        /*if(firebaseUser != null)
        {
            Intent i = new Intent(context,MainActivity.class);
            startActivity(i);
            finish();
        }*/

        if(hasLoggedIn){
            Intent i = new Intent(context,MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void getFirebaseToken() {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                String token = instanceIdResult.getToken();

                //Toast.makeText(context, token, Toast.LENGTH_SHORT).show();

                Log.d("Token", "onSuccess: " + token);

                preferences.edit().putString(ServerCredentials.TOKEN,token).apply();
            }
        });

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
    }

    private void setClickListener() {

        activityWelcomeBinding.txtSignIn.setOnClickListener(this);
        activityWelcomeBinding.txtCreateAccount.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_signIn:
                Intent in = new Intent(context,SigninActivity.class);
                startActivity(in);
                break;

            case R.id.txt_createAccount:
                Intent i = new Intent(context,SignUpActivity.class);
                startActivity(i);
                break;
        }
    }
}
