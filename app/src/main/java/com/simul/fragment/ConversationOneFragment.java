package com.simul.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.databinding.FragmentConversationOneBinding;
import com.simul.utils.CommonMethods;
import com.simul.utils.EmojiExcludeFilter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationOneFragment extends Fragment {

    FragmentConversationOneBinding fragmentConversationOneBinding;

    private Context context;

    private int RESULT_LOAD_IMAGE = 100;

    private Bitmap profileBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentConversationOneBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_conversation_one, container, false);
      //  EmojiCompat.init(new BundledEmojiCompatConfig(getContext()));
        idMapping();

        setClickListener();

        changeButtonBg();

        return fragmentConversationOneBinding.getRoot();
    }

    private void idMapping() {

        context = getContext();

      //  fragmentConversationOneBinding.edtQuestion.setFilters(new InputFilter[]{new EmojiExcludeFilter()});
    }

    private void changeButtonBg() {

        fragmentConversationOneBinding.edtQuestion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(fragmentConversationOneBinding.edtQuestion.getText().toString()) && !TextUtils.isEmpty(fragmentConversationOneBinding.ivImageName.getText().toString())) {
                    fragmentConversationOneBinding.txtNext.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    fragmentConversationOneBinding.txtNext.setTextColor(getResources().getColor(R.color.white));
                } else {
                    fragmentConversationOneBinding.txtNext.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    fragmentConversationOneBinding.txtNext.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });

        fragmentConversationOneBinding.ivImageName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(fragmentConversationOneBinding.edtQuestion.getText().toString()) && !TextUtils.isEmpty(fragmentConversationOneBinding.ivImageName.getText().toString())) {
                    fragmentConversationOneBinding.txtNext.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    fragmentConversationOneBinding.txtNext.setTextColor(getResources().getColor(R.color.white));
                } else {
                    fragmentConversationOneBinding.txtNext.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                    fragmentConversationOneBinding.txtNext.setTextColor(getResources().getColor(R.color.edt_hint));
                }
            }
        });
    }

    private void checkFeildValidation() {

        String question = fragmentConversationOneBinding.edtQuestion.getText().toString();

        if(question.trim().length() == 0){
            Toast.makeText(context, "Please type some conversation message", Toast.LENGTH_SHORT).show();
        }else {
            Bundle bundle = new Bundle();
            bundle.putString(ServerCredentials.QUESTION, question);
            bundle.putParcelable("BitmapImage",profileBitmap);
            Fragment fragment = new ConversationTwoFragment();
            fragment.setArguments(bundle);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_convesationContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void setClickListener() {

        fragmentConversationOneBinding.txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFeildValidation();
            }
        });

        fragmentConversationOneBinding.linUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                } else{
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 2000 :
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, 100);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(context, "These permissions are mandatory for the application. Please allow access.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try{
                final Uri imageUri = data.getData();
                final InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profileBitmap = selectedImage;

                fragmentConversationOneBinding.ivImageName.setText("Tap here to change attached file");
                fragmentConversationOneBinding.ivAttachedImg.setImageBitmap(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        /*if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            profileBitmap = null;

            if (data.getExtras() != null) {

                Bundle extras = data.getExtras();

                Bitmap bitmap = extras.getParcelable("data");

                profileBitmap = bitmap;

                fragmentConversationOneBinding.ivAttachedImg.setImageBitmap(profileBitmap);

               // iv_profile.setImageBitmap(profileBitmap);

            } else if (data.getData() != null) {

                Uri uri = data.getData();

                File f = new File(""+uri);


                fragmentConversationOneBinding.ivImageName.setText("Tap here to change attached file");

                Bitmap bitmap = null;

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

                } catch (IOException e) {

                    e.printStackTrace();
                }

                if (CommonMethods.exifCheck(bitmap, "file://" + uri.getPath()) != null) {

                    bitmap = CommonMethods.exifCheck(bitmap, "file://" + uri.getPath());
                }

                if (bitmap != null) {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

                    profileBitmap = bitmap;

                    fragmentConversationOneBinding.ivAttachedImg.setImageBitmap(profileBitmap);

                 //   iv_profile.setImageBitmap(profileBitmap);

                }

            } else if (data.getClipData() != null) {

                Uri uri = data.getClipData().getItemAt(0).getUri();

                Bitmap bitmap = null;

                try {

                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

                } catch (IOException e) {

                    e.printStackTrace();
                }

                if (bitmap != null) {

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);

                    profileBitmap = bitmap;

                    fragmentConversationOneBinding.ivAttachedImg.setImageBitmap(profileBitmap);

                  //  iv_profile.setImageBitmap(profileBitmap);
//
//                    byte[] byteArrayImage = byteArrayOutputStream.toByteArray();
//
//                    str_encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);


//                    Picasso.get().load(uri).transform(new CircleTransformPicasso()).into(iv_profile);

                    // iv_profilePic.setImageBitmap(CommonMethods.getCroppedBitmap(bitmap));
                }

            } else {

                Toast.makeText(context, "Something went wrong. Try different gallery app if problem persists.", Toast.LENGTH_LONG).show();
            }
        }else {
           // Toast.makeText(context, "For check", Toast.LENGTH_SHORT).show();
        }*/
    }
}
