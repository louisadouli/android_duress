package com.simul.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.simul.R;
import com.simul.adapter.MessagesAdapter;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivityChatMessagesBinding;
import com.simul.model.ChatModel;
import com.simul.model.MessagesModel;
import com.simul.model.UserModel;
import com.simul.utils.CommonMethods;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMessagesActivity extends AppCompatActivity {

    ActivityChatMessagesBinding activityChatMessagesBinding;

    Context context;

    MessagesAdapter messagesAdapter;

    private Uri thumbImageUri;

    Bitmap bitmap;

    SharedPreferences preferences;

    private ArrayList<MessagesModel> mChat;

    Intent intent;

    boolean report = false;
    boolean notify = false;
    String chatUserId, userid, display_simul, myUserId;
    FirebaseUser fuser;
    DatabaseReference reference, refs, chatlistRef;
    List<ChatModel> mchat;
    ValueEventListener seenListener;
    ValueEventListener reedListener;
    private boolean readMethod = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityChatMessagesBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat_messages);

        idMapping();

        setUpChatMessages();

        setImageBoder();

        setClickListener();

        activityChatMessagesBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;

        mChat = new ArrayList<>();

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

       /* DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Chats");
        Query lastQuery = dbRef.orderByKey().limitToLast(1);
        lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot data : dataSnapshot.getChildren())
                {
                    Date d = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                    String currentDateTimeString = sdf.format(d);
                    //if you call methods on dataSnapshot it gives you the required values
                    //String s = data.getValue(); // then it has the value "somekey4"
                    String key = data.getKey(); // then it has the value "4:"
                    reference.child(key);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("time", currentDateTimeString);
                    reference.updateChildren(hashMap);
                    //as per your given snapshot of firebase database data
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }});*/
    }

    private void setUpChatMessages() {

        activityChatMessagesBinding.recyclerMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        activityChatMessagesBinding.recyclerMessages.setLayoutManager(linearLayoutManager);

        intent = getIntent();

        userid = intent.getStringExtra("userid");
        display_simul = intent.getStringExtra(ServerCredentials.DISPLAY_SIMUL);
        chatUserId = intent.getStringExtra(ServerCredentials.ID);
        myUserId = preferences.getString(ServerCredentials.USER_ID, null);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        assert userid != null;
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                assert user != null;
                activityChatMessagesBinding.txtUsername.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    activityChatMessagesBinding.ivProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    // Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
                    Picasso.get().load(user.getImageURL()).placeholder(R.drawable.ic_dummy).into(activityChatMessagesBinding.ivProfileImage);
                }

                readMesagges(fuser.getUid(), userid, user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (readMethod) {
            seenMessage(userid);
        } else {

        }


    }

    private void setImageBoder() {

        if (display_simul.equals(null)) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#BFFB496D"));
        } else if (display_simul.equals("Diabetes")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#0296FF"));
        } else if (display_simul.equals("Cancer")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Mental Health")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Genetic Discordes")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#B00296FF"));
        } else if (display_simul.equals("Age Associated Dieases")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Heart Diases")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#FB496D"));
        } else if (display_simul.equals("MND")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#056CFE"));
        } else if (display_simul.equals("Asthma")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#4CD964"));
        } else if (display_simul.equals("Trauma")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#3C81B5"));
        } else if (display_simul.equals("Tumors")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#FFD519"));
        } else if (display_simul.equals("Obesity")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#4730B8"));
        } else if (display_simul.equals("Sexually Transmitted Diseases")) {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#BFFB496D"));
        } else {
            activityChatMessagesBinding.ivProfileImage.setBorderColor(Color.parseColor("#BFFB496D"));
        }
    }

    private void setClickListener() {

        activityChatMessagesBinding.txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String msg = activityChatMessagesBinding.edtTypeMessage.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), userid, msg);
                } else {
                    Toast.makeText(context, "You can't send Empty Message !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        activityChatMessagesBinding.ivHand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                thumbImageUri = Uri.parse("android.resource://" + context.getPackageName() + "/drawable/ic_chat_hand");
                bitmap = ((BitmapDrawable) activityChatMessagesBinding.ivHand.getDrawable()).getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                //Toast.makeText(context, ""+thumbImageUri, Toast.LENGTH_SHORT).show();


                sendThumbMessage(fuser.getUid(), userid, thumbImageUri);
            }
        });

        activityChatMessagesBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        activityChatMessagesBinding.ivDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(context);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.setContentView(R.layout.dialog_delete_chat);

                dialog.setTitle("Custom Dialog");

                dialog.show();

                Window window = dialog.getWindow();
                //window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);

                TextView txt_No = dialog.findViewById(R.id.txt_No);

                TextView txt_yes = dialog.findViewById(R.id.txt_yes);

                txt_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        /*FirebaseDatabase.getInstance().getReference("Chats").child("-M2hGX1l8HC4uBhzx03u").removeValue();
                        Toast.makeText(ChatMessagesActivity.this, "Ok", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();*/

                        //mchat = new ArrayList<>();

                        deleteFromChatList();

                        readMethod = false;

                        reference = FirebaseDatabase.getInstance().getReference("Chats");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ChatModel chat = snapshot.getValue(ChatModel.class);

                                    if (chat.getSender().equals(fuser.getUid()) && chat.getReceiver().equals(userid)) {
                                        if (chat.getId() != null) {
                                            // reference.child(chat.getId()).removeValue();
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("message", "null");
                                            snapshot.getRef().updateChildren(hashMap);
                                        }
                                    }

                                    if (chat.getSender().equals(userid) && chat.getReceiver().equals(fuser.getUid())) {
                                        if (chat.getId() != null) {
                                            //reference.child(chat.getId()).removeValue();
                                            HashMap<String, Object> hashMap = new HashMap<>();
                                            hashMap.put("message", "null");
                                            snapshot.getRef().updateChildren(hashMap);
                                        }
                                    }

                                    dialog.dismiss();

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ChatMessagesActivity.this, "" + databaseError, Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                });

                txt_No.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });

        activityChatMessagesBinding.edtTypeMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    activityChatMessagesBinding.txtSend.setVisibility(View.VISIBLE);
                    activityChatMessagesBinding.ivHand.setVisibility(View.GONE);
                } else {
                    activityChatMessagesBinding.ivHand.setVisibility(View.VISIBLE);
                    activityChatMessagesBinding.txtSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        activityChatMessagesBinding.ivChatUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!report) {
                    final Dialog dialog = new Dialog(context);

                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    dialog.setContentView(R.layout.dialog_report_user);

                    dialog.setTitle("Custom Dialog");

                    dialog.show();

                    Window window = dialog.getWindow();
                    //window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    window.setGravity(Gravity.CENTER);

                    TextView txt_No = dialog.findViewById(R.id.txt_No);

                    TextView txt_yes = dialog.findViewById(R.id.txt_yes);

                    txt_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (chatUserId.equals(null)) {

                            } else {

                                serverCallForReportUser();

                            }
                            dialog.dismiss();

                        }
                    });

                    txt_No.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(ChatMessagesActivity.this, "You are already report this user.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void serverCallForReportUser() {

        if (CommonMethods.checkInternetConnection(context)) {

            activityChatMessagesBinding.pbChatMsg.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_REPORT_USRR;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        activityChatMessagesBinding.pbChatMsg.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            report = true;
                        } else {
                            Toast.makeText(ChatMessagesActivity.this, "You are already report this user.", Toast.LENGTH_SHORT).show();
                            report = true;
                        }

                    } catch (JSONException e) {
                        activityChatMessagesBinding.pbChatMsg.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    activityChatMessagesBinding.pbChatMsg.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, myUserId);//user_id
                    params.put("report_user_id", chatUserId);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFromChatList() {

        chatlistRef = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid()).child(userid);

        chatlistRef.removeValue();
    }

    private void sendThumbMessage(String sender, final String receiver, Uri thumbImageUri) {

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        String currentDateTimeString = sdf.format(d);

        theLastMessage(currentDateTimeString);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", thumbImageUri.toString());
        hashMap.put("isseen", false);
        hashMap.put("time", currentDateTimeString);
        hashMap.put("thumb", "1");

        reference.child("Chats").push().setValue(hashMap);

        String p = reference.getDatabase().getReference().getKey();
        String pss = "";

        ///////////////////////
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        refs = FirebaseDatabase.getInstance().getReference("Chats");

        refs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int c = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    refs = FirebaseDatabase.getInstance().getReference("Chats").child(snapshot.getKey());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", snapshot.getKey());
                    refs.updateChildren(hashMap);

                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    /*if (!chat.isIsseen()) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals("PcQUhi29cGcXx4akrRjvhrBBtH42") ||
                                chat.getReceiver().equals("PcQUhi29cGcXx4akrRjvhrBBtH42") && chat.getSender().equals(firebaseUser.getUid())) {

                            c++;
                            int value = c;
                            *//*hashMap.put("count", String.valueOf(c));
                            reference.updateChildren(hashMap);*//*
                            //theLastMessage = chat.getMessage();


                        }
                    }

                    if (c == 0) {
                        hashMap.put("count", "0");
                       // reference.updateChildren(hashMap);
                    }*/

                  /*  if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals("PcQUhi29cGcXx4akrRjvhrBBtH42")){
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        String use = userModel.getId();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(chat.getReceiver());
                        hashMap.put("unread", String.valueOf(c));
                        reference.updateChildren(hashMap);
                    }*/


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //////////////////////////

        activityChatMessagesBinding.edtTypeMessage.setText("");

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = thumbImageUri.toString();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (notify) {
                    // sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMesagges(final String myid, final String userid, final String imageurl) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);

                    if (chat.getReceiver() == null) {

                    } else if (chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)) {
                        mchat.add(chat);
                    }

                    messagesAdapter = new MessagesAdapter(context, mchat, imageurl);
                    activityChatMessagesBinding.recyclerMessages.setAdapter(messagesAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message) {

        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        String currentDateTimeString = sdf.format(d);

        theLastMessage(currentDateTimeString);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("time", "");
        hashMap.put("thumb", "0");

        reference.child("Chats").push().setValue(hashMap);

        String p = reference.getDatabase().getReference().getKey();
        String pss = "";

        ///////////////////////
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        refs = FirebaseDatabase.getInstance().getReference("Chats");

        refs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int c = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    refs = FirebaseDatabase.getInstance().getReference("Chats").child(snapshot.getKey());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", snapshot.getKey());
                    refs.updateChildren(hashMap);

                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    /*if (!chat.isIsseen()) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals("PcQUhi29cGcXx4akrRjvhrBBtH42") ||
                                chat.getReceiver().equals("PcQUhi29cGcXx4akrRjvhrBBtH42") && chat.getSender().equals(firebaseUser.getUid())) {

                            c++;
                            int value = c;
                            *//*hashMap.put("count", String.valueOf(c));
                            reference.updateChildren(hashMap);*//*
                            //theLastMessage = chat.getMessage();


                        }
                    }

                    if (c == 0) {
                        hashMap.put("count", "0");
                       // reference.updateChildren(hashMap);
                    }*/

                  /*  if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals("PcQUhi29cGcXx4akrRjvhrBBtH42")){
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        String use = userModel.getId();
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(chat.getReceiver());
                        hashMap.put("unread", String.valueOf(c));
                        reference.updateChildren(hashMap);
                    }*/


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //////////////////////////

        activityChatMessagesBinding.edtTypeMessage.setText("");

        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
                .child(fuser.getUid())
                .child(userid);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                UserModel user = dataSnapshot.getValue(UserModel.class);
                if (notify) {
                    // sendNotification(receiver, user.getUsername(), msg);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void seenMessage(final String userid) {

        reference = FirebaseDatabase.getInstance().getReference("Chats");

        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatModel chat = snapshot.getValue(ChatModel.class);
                    if (chat.getReceiver() == null) {
                    } else if (readMethod && chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }

                    if (chat.getId() == null) {

                    } else {
                        if (chat.getReceiver() == null) {

                        } else if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
                            if (chat.getTime().equals("")) {
                                Date d = new Date();
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                                String currentDateTimeString = sdf.format(d);
                                String p = chat.getId();
                                String check = "";


                                reference = FirebaseDatabase.getInstance().getReference("Chats").child(chat.getId());

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("time", currentDateTimeString);
                                reference.updateChildren(hashMap);


                            }
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }

    private void theLastMessage(String currentDateTimeString) {

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("lastMsgTime", currentDateTimeString);
        reference.updateChildren(hashMap);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
