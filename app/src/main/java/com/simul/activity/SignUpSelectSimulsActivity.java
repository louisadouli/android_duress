package com.simul.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.ActivitySignUpSelectSimulsBinding;
import com.simul.utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SignUpSelectSimulsActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    ActivitySignUpSelectSimulsBinding activitySignUpSelectSimulsBinding;

    Context context;

    FirebaseAuth auth;

    DatabaseReference reference;

    StorageReference storageReference;

    private Bitmap profileBitmap;

    Bitmap bmp;

    Uri imageUri;

    int display_simual_count = 0;

    String display_simul;

    SharedPreferences preferences;

    private FirebaseUser fuser;

    private StorageTask uploadTask;

    ArrayList<String> symptoms;

    String username, email, password, about_your_self, carer, city = "Not Found";

    boolean setCheck, setMultipleCheck = true;

    String userId, snapchatImgUrl;

    boolean imgSnap;

    Location loc;
    LocationManager locationManager;

    double MyLat;
    double MyLong;

    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpSelectSimulsBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_select_simuls);

        idMapping();

        getIntentData();

        getGpsData();

        setClickListener();

        activitySignUpSelectSimulsBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;

        symptoms = new ArrayList<>();

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference("uploads");



    }

    private void getIntentData() {

        username = getIntent().getStringExtra(ServerCredentials.NAME);
        email = getIntent().getStringExtra(ServerCredentials.EMAIL_ID);
        password = getIntent().getStringExtra(ServerCredentials.PASSWORD);
        about_your_self = getIntent().getStringExtra(ServerCredentials.ABOUT_SELF);
        carer = getIntent().getStringExtra(ServerCredentials.CARER);
        imageUri = Uri.parse(getIntent().getStringExtra("imageUri"));

        snapchatImgUrl = getIntent().getStringExtra("imageUri");

        if (snapchatImgUrl.startsWith("https")) {
            imgSnap = true;
        } else {
            imgSnap = false;
        }

        byte[] byteArray = getIntent().getByteArrayExtra(ServerCredentials.PROFILE_PIC);
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    private void setClickListener() {

        activitySignUpSelectSimulsBinding.txtDiabites.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtCancer.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtMentalHelth.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtGenericDiabites.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtAgeAssociated.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtST.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtHeartDiases.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtMnd.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtAsthma.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtTrauma.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtTumurs.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtObesity.setOnClickListener(this);

        activitySignUpSelectSimulsBinding.txtDiabitesOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtCancerOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtMentalHelthOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtGenericDiabitesOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtAgeAssociatedOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtHeartDiasesOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtMndOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtAsthmaOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtTraumaOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtTumursOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtObesityOne.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtDiabitesTwo.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtSexuallyTransmittedTwo.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtSexuallyTransmittedThree.setOnClickListener(this);
        activitySignUpSelectSimulsBinding.txtSexuallyTransmittedFour.setOnClickListener(this);


        activitySignUpSelectSimulsBinding.txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (symptoms.size() == 0) {
                    Toast.makeText(context, "Please Select Your Simul's", Toast.LENGTH_SHORT).show();
                } else {
                    String symptoms_string = convertToString(symptoms);
                    serverCallForSignUp(symptoms_string);
                }
            }
        });
    }

    private void serverCallForSignUp(final String symptoms_string) {
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, ServerUrls.USER_SIGNUP,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {

                            activitySignUpSelectSimulsBinding.setProcessing(true);

                            JSONObject jsonObject = new JSONObject(new String(response.data));

                            int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                            String message = jsonObject.getString(ServerCredentials.MESSAGE);

                            if (success == 1) {

                                preferences.edit().putString("MY_SIMUL", display_simul).apply();

                                register(username, email, password,display_simul);
                                uploadImage(message);

                            } else {

                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            }

                            // Toast.makeText(context.getApplicationContext(), updateuserObject.getString("error"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put(ServerCredentials.NAME, username);

                params.put(ServerCredentials.EMAIL_ID, email);

                params.put(ServerCredentials.PASSWORD, password);

                params.put(ServerCredentials.ABOUT_SELF, about_your_self);

                params.put(ServerCredentials.CARER, carer);

                params.put(ServerCredentials.CITY, city);

                params.put(ServerCredentials.SIMUL_SYMPTOMS, symptoms_string);

                params.put(ServerCredentials.DISPLAY_SIMUL,display_simul);

                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put(ServerCredentials.PROFILE_PIC, new DataPart(System.currentTimeMillis() +
                        ".jpg", getFileDataFromDrawable(bmp)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

    static String convertToString(ArrayList<String> numbers) {
        StringBuilder builder = new StringBuilder();
        // Append all Integers in StringBuilder to the StringBuilder.
        for (String number : numbers) {
            builder.append(number);
            builder.append(",");
        }
        // Remove last delimiter with setLength.
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

    private void register(final String username, String email, String password, final String display_simul) {//email
        auth.createUserWithEmailAndPassword(username + "@gmail.com", password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    assert firebaseUser != null;
                    userId = firebaseUser.getUid();

                    reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userId);
                    hashMap.put("username", username);
                    hashMap.put("imageURL", "default");
                    hashMap.put("status", "offline");
                    hashMap.put("search", username.toLowerCase());
                    hashMap.put("display_simul", display_simul);

                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(context, "Here", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(context, "You can't register with this email password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadImage(final String message) {
        /*final ProgressDialog pb = new ProgressDialog(context);
        pb.setMessage("Uploading");
        pb.show();*/

        if (imageUri != null) {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() +
                    "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        String mUri = null;
                        if (imgSnap) {
                            mUri = snapchatImgUrl;
                        } else {
                            Uri downloadUri = task.getResult();
                            mUri = downloadUri.toString();
                        }


                        FirebaseUser firebaseUser = auth.getCurrentUser();
                        assert firebaseUser != null;
                        String userId = firebaseUser.getUid();

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);//child(firebaseUser.getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);


                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(context,SigninActivity.class);
                       // i.putExtra("SNAP_IMAGE","snapchatImgUrl");
                        preferences.edit().putString("SNAP_IMAGE","snapchatImgUrl").apply();
                        startActivity(i);
                        finish();

                        activitySignUpSelectSimulsBinding.setProcessing(false);

                        conertIntoBitmap();

                    } else {

                        // Toast.makeText(context, "Failed !", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context,SigninActivity.class);
                       // i.putExtra("SNAP_IMAGE",snapchatImgUrl);
                        preferences.edit().putString("SNAP_IMAGE",snapchatImgUrl).apply();
                        startActivity(i);
                        finish();
                        activitySignUpSelectSimulsBinding.setProcessing(false);
                        //pb.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  //  Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    activitySignUpSelectSimulsBinding.setProcessing(false);
                    //pb.dismiss();
                }
            });
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void conertIntoBitmap() {

        try {
            if (imageUri != null) {
                profileBitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            }
        } catch (Exception e) {
            //Toast.makeText(context, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.txt_diabites:
                setCheck = activitySignUpSelectSimulsBinding.txtDiabites.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtDiabites.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtDiabites, setCheck);

                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.txt_cancer:
                setCheck = activitySignUpSelectSimulsBinding.txtCancer.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtCancer.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtCancer, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.txt_mentalHelth:
                setCheck = activitySignUpSelectSimulsBinding.txtMentalHelth.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtMentalHelth.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtMentalHelth, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_genericDiabites:
                setCheck = activitySignUpSelectSimulsBinding.txtGenericDiabites.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtGenericDiabites.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtGenericDiabites, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_ageAssociated:
                setCheck = activitySignUpSelectSimulsBinding.txtAgeAssociated.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtAgeAssociated.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtAgeAssociated, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_sT:
                setCheck = activitySignUpSelectSimulsBinding.txtST.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtST.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtST, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_heartDiases:
                setCheck = activitySignUpSelectSimulsBinding.txtHeartDiases.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtHeartDiases.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtHeartDiases, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_mnd:
                setCheck = activitySignUpSelectSimulsBinding.txtMnd.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtMnd.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtMnd, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_asthma:
                setCheck = activitySignUpSelectSimulsBinding.txtAsthma.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtAsthma.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtAsthma, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_trauma:
                setCheck = activitySignUpSelectSimulsBinding.txtTrauma.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtTrauma.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtTrauma, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_tumurs:
                setCheck = activitySignUpSelectSimulsBinding.txtTumurs.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtTumurs.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtTumurs, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_obesity:
                setCheck = activitySignUpSelectSimulsBinding.txtObesity.isSelected();
                if (display_simual_count < 1 || setCheck) {
                    display_simul = activitySignUpSelectSimulsBinding.txtObesity.getText().toString();
                    simulCheckUncheck(activitySignUpSelectSimulsBinding.txtObesity, setCheck);
                } else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                //simulCheckUncheck(activitySignUpSelectSimulsBinding.txtObesity, setCheck);
                break;

            case R.id.txt_diabitesOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtDiabitesOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtDiabitesOne, setMultipleCheck);
                break;

            case R.id.txt_cancerOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtCancerOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtCancerOne, setMultipleCheck);
                break;

            case R.id.txt_mentalHelthOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtMentalHelthOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtMentalHelthOne, setMultipleCheck);
                break;

            case R.id.txt_genericDiabitesOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtGenericDiabitesOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtGenericDiabitesOne, setMultipleCheck);
                break;

            case R.id.txt_ageAssociatedOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtAgeAssociatedOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtAgeAssociatedOne, setMultipleCheck);
                break;

            case R.id.txt_mndOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtMndOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtMndOne, setMultipleCheck);
                break;

            case R.id.txt_diabitesTwo:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtDiabitesTwo.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtDiabitesTwo, setMultipleCheck);
                break;

            case R.id.txt_heartDiasesOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtHeartDiasesOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtHeartDiasesOne, setMultipleCheck);
                break;

            case R.id.txt_asthmaOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtAsthmaOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtAsthmaOne, setMultipleCheck);
                break;

            case R.id.txt_traumaOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtTraumaOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtTraumaOne, setMultipleCheck);
                break;

            case R.id.txt_tumursOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtTumursOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtTumursOne, setMultipleCheck);
                break;

            case R.id.txt_obesityOne:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtObesityOne.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtObesityOne, setMultipleCheck);
                break;

            case R.id.txt_sexuallyTransmittedTwo:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtSexuallyTransmittedTwo.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtSexuallyTransmittedTwo, setMultipleCheck);
                break;

            case R.id.txt_sexuallyTransmittedThree:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtSexuallyTransmittedThree.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtSexuallyTransmittedThree, setMultipleCheck);
                break;

            case R.id.txt_sexuallyTransmittedFour:
                setMultipleCheck = activitySignUpSelectSimulsBinding.txtSexuallyTransmittedFour.isSelected();
                simulMultipleCheckUncheck(activitySignUpSelectSimulsBinding.txtSexuallyTransmittedFour, setMultipleCheck);
                break;

        }

    }

    private void simulMultipleCheckUncheck(TextView textViewMultiple, boolean setMultipleCheck) {
        if (setMultipleCheck) {

            textViewMultiple.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textViewMultiple.setSelected(false);
            symptoms.remove(textViewMultiple.getText().toString());

            if (symptoms.size() > 0) {
                activitySignUpSelectSimulsBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                activitySignUpSelectSimulsBinding.txtSignUp.setTextColor(getResources().getColor(R.color.white));

            } else {
                activitySignUpSelectSimulsBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                activitySignUpSelectSimulsBinding.txtSignUp.setTextColor(getResources().getColor(R.color.edt_hint));
            }
        } else {

            textViewMultiple.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_round_fwhite_fill_tick, 0);
            textViewMultiple.setSelected(true);
            symptoms.add(textViewMultiple.getText().toString());
            String l = "";

            if (symptoms.size() > 0) {
                activitySignUpSelectSimulsBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                activitySignUpSelectSimulsBinding.txtSignUp.setTextColor(getResources().getColor(R.color.white));

            } else {
                activitySignUpSelectSimulsBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                activitySignUpSelectSimulsBinding.txtSignUp.setTextColor(getResources().getColor(R.color.edt_hint));
            }
        }

    }

    public void simulCheckUncheck(TextView textView, boolean setCheck) {

        if (setCheck) {
            display_simual_count--;
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textView.setSelected(false);
            symptoms.remove(textView.getText().toString());

            if (symptoms.size() > 0) {
                activitySignUpSelectSimulsBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                activitySignUpSelectSimulsBinding.txtSignUp.setTextColor(getResources().getColor(R.color.white));

            } else {
                activitySignUpSelectSimulsBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                activitySignUpSelectSimulsBinding.txtSignUp.setTextColor(getResources().getColor(R.color.edt_hint));
            }
        } else {
            display_simual_count++;
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_round_fwhite_fill_tick, 0);
            textView.setSelected(true);
            symptoms.add(textView.getText().toString());
            String l = "";

            if (symptoms.size() > 0) {
                activitySignUpSelectSimulsBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                activitySignUpSelectSimulsBinding.txtSignUp.setTextColor(getResources().getColor(R.color.white));

            } else {
                activitySignUpSelectSimulsBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                activitySignUpSelectSimulsBinding.txtSignUp.setTextColor(getResources().getColor(R.color.edt_hint));
            }
        }


        //textView.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_round_fwhite_fill_tick, 0, 0, 0);
    }

    private void getGpsData() {

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }

            // get location
            getLocation();
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                getGpsData();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                        getData();
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                        getData();
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                    getData();
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        /*fieldLatitude.setText("Latitude : "+Double.toString(loc.getLatitude()));
        fieldLongitude.setText("Longitude : "+Double.toString(loc.getLongitude()));
        fieldTime.setText("Time : "+ DateFormat.getTimeInstance().format(loc.getTime()));*/

        MyLat = loc.getLatitude();
        MyLong = loc.getLongitude();
    }

    private void getData() {

        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(MyLat, MyLong, 1);
            if (addresses.isEmpty()) {
                //fieldAddressLine.setText("Waiting for Location");
            } else {
                if (addresses.size() > 0) {
                    city = addresses.get(0).getLocality();
                    //Toast.makeText(context, city, Toast.LENGTH_SHORT).show();
                    /*fieldCity.setText("City : "+addresses.get(0).getLocality());
                    fieldPostalCode.setText("PostalCode : "+addresses.get(0).getPostalCode());
                    fieldCountry.setText("Country : "+addresses.get(0).getCountryName());
                    fieldAddressLine.setText("Address : "+addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                   */ //Toast.makeText(getApplicationContext(), "Address:- " + addresses.get(0).getFeatureName() + addresses.get(0).getAdminArea() + addresses.get(0).getLocality(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "onLocationChanged");
        updateUI(location);

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }
}
