package com.simul.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.databinding.ActivityEditProfileSelectSimulBinding;

import java.util.ArrayList;

public class EditProfileSelectSimulActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityEditProfileSelectSimulBinding activityEditProfileSelectSimulBinding;

    Context context;

    ArrayList<String> symptoms;

    SharedPreferences preferences;

    String display_simul;

    int display_simual_count = 0;

    boolean setCheck,setMultipleCheck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditProfileSelectSimulBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile_select_simul);

        idMapping();

        setClickListener();

        activityEditProfileSelectSimulBinding.setActivity(this);
    }

    private void idMapping() {

        context = this;

        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);

        symptoms = new ArrayList<>();
    }

    private void setClickListener() {

        activityEditProfileSelectSimulBinding.txtDiabites.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtCancer.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtMentalHelth.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtGenericDiabites.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtAgeAssociated.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtST.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtHeartDiases.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtMnd.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtAsthma.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtTrauma.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtTumurs.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtObesity.setOnClickListener(this);

        activityEditProfileSelectSimulBinding.txtDiabitesOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtCancerOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtMentalHelthOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtGenericDiabitesOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtAgeAssociatedOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtHeartDiasesOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtMndOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtAsthmaOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtTraumaOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtTumursOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtObesityOne.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtDiabitesTwo.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtSexuallyTransmittedTwo.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtSexuallyTransmittedThree.setOnClickListener(this);
        activityEditProfileSelectSimulBinding.txtSexuallyTransmittedFour.setOnClickListener(this);


        activityEditProfileSelectSimulBinding.txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (symptoms.size() == 0) {
                    Toast.makeText(context, "Please Select Your Simul's", Toast.LENGTH_SHORT).show();
                } else {
                    preferences.edit().putBoolean("UPADATE_SIMUL",true).apply();
                    String symptoms_string = convertToString(symptoms);

                    Intent i = new Intent(context,EditProfileActivity.class);
                    i.putExtra(ServerCredentials.DISPLAY_SIMUL,display_simul);
                    i.putExtra("symptoms_string",symptoms_string);
                    startActivity(i);
                    finish();
                    //Toast.makeText(context, ""+symptoms_string, Toast.LENGTH_SHORT).show();
                    //serverCallForSignUp(symptoms_string);
                }
            }
        });
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.txt_diabites:
                setCheck = activityEditProfileSelectSimulBinding.txtDiabites.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtDiabites.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtDiabites, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.txt_cancer:
                setCheck = activityEditProfileSelectSimulBinding.txtCancer.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtCancer.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtCancer, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.txt_mentalHelth:
                setCheck = activityEditProfileSelectSimulBinding.txtMentalHelth.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtMentalHelth.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtMentalHelth, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_genericDiabites:
                setCheck = activityEditProfileSelectSimulBinding.txtGenericDiabites.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtGenericDiabites.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtGenericDiabites, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_ageAssociated:
                setCheck = activityEditProfileSelectSimulBinding.txtAgeAssociated.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtAgeAssociated.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtAgeAssociated, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_sT:
                setCheck = activityEditProfileSelectSimulBinding.txtST.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtST.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtST, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_heartDiases:
                setCheck = activityEditProfileSelectSimulBinding.txtHeartDiases.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtHeartDiases.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtHeartDiases, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_mnd:
                setCheck = activityEditProfileSelectSimulBinding.txtMnd.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtMnd.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtMnd, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_asthma:
                setCheck = activityEditProfileSelectSimulBinding.txtAsthma.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtAsthma.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtAsthma, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_trauma:
                setCheck = activityEditProfileSelectSimulBinding.txtTrauma.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtTrauma.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtTrauma, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_tumurs:
                setCheck = activityEditProfileSelectSimulBinding.txtTumurs.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtTumurs.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtTumurs, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.txt_obesity:
                setCheck = activityEditProfileSelectSimulBinding.txtObesity.isSelected();
                if(display_simual_count < 1 || setCheck){
                    display_simul = activityEditProfileSelectSimulBinding.txtObesity.getText().toString();
                    simulCheckUncheck(activityEditProfileSelectSimulBinding.txtObesity, setCheck);
                }else {
                    Toast.makeText(context, R.string.error_display_simul_only_one, Toast.LENGTH_SHORT).show();
                }
                //simulCheckUncheck(activitySignUpSelectSimulsBinding.txtObesity, setCheck);
                break;

            case R.id.txt_diabitesOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtDiabitesOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtDiabitesOne, setMultipleCheck);
                break;

            case R.id.txt_cancerOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtCancerOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtCancerOne, setMultipleCheck);
                break;

            case R.id.txt_mentalHelthOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtMentalHelthOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtMentalHelthOne, setMultipleCheck);
                break;

            case R.id.txt_genericDiabitesOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtGenericDiabitesOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtGenericDiabitesOne, setMultipleCheck);
                break;

            case R.id.txt_ageAssociatedOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtAgeAssociatedOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtAgeAssociatedOne, setMultipleCheck);
                break;

            case R.id.txt_mndOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtMndOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtMndOne, setMultipleCheck);
                break;

            case R.id.txt_diabitesTwo:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtDiabitesTwo.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtDiabitesTwo, setMultipleCheck);
                break;

            case R.id.txt_heartDiasesOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtHeartDiasesOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtHeartDiasesOne, setMultipleCheck);
                break;

            case R.id.txt_asthmaOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtAsthmaOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtAsthmaOne, setMultipleCheck);
                break;

            case R.id.txt_traumaOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtTraumaOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtTraumaOne, setMultipleCheck);
                break;

            case R.id.txt_tumursOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtTumursOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtTumursOne, setMultipleCheck);
                break;

            case R.id.txt_obesityOne:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtObesityOne.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtObesityOne, setMultipleCheck);
                break;

            case R.id.txt_sexuallyTransmittedTwo:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtSexuallyTransmittedTwo.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtSexuallyTransmittedTwo, setMultipleCheck);
                break;

            case R.id.txt_sexuallyTransmittedThree:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtSexuallyTransmittedThree.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtSexuallyTransmittedThree, setMultipleCheck);
                break;

            case R.id.txt_sexuallyTransmittedFour:
                setMultipleCheck = activityEditProfileSelectSimulBinding.txtSexuallyTransmittedFour.isSelected();
                simulMultipleCheckUncheck(activityEditProfileSelectSimulBinding.txtSexuallyTransmittedFour, setMultipleCheck);
                break;

        }

    }

    private void simulMultipleCheckUncheck(TextView textViewMultiple, boolean setMultipleCheck) {
        if (setMultipleCheck) {

            textViewMultiple.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            textViewMultiple.setSelected(false);
            symptoms.remove(textViewMultiple.getText().toString());

            if (symptoms.size() > 0) {
                activityEditProfileSelectSimulBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                activityEditProfileSelectSimulBinding.txtSignUp.setTextColor(getResources().getColor(R.color.white));

            } else {
                activityEditProfileSelectSimulBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                activityEditProfileSelectSimulBinding.txtSignUp.setTextColor(getResources().getColor(R.color.edt_hint));
            }
        } else {

            textViewMultiple.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_round_fwhite_fill_tick, 0);
            textViewMultiple.setSelected(true);
            symptoms.add(textViewMultiple.getText().toString());
            String l = "";

            if (symptoms.size() > 0) {
                activityEditProfileSelectSimulBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                activityEditProfileSelectSimulBinding.txtSignUp.setTextColor(getResources().getColor(R.color.white));

            } else {
                activityEditProfileSelectSimulBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                activityEditProfileSelectSimulBinding.txtSignUp.setTextColor(getResources().getColor(R.color.edt_hint));
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
                activityEditProfileSelectSimulBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                activityEditProfileSelectSimulBinding.txtSignUp.setTextColor(getResources().getColor(R.color.white));

            } else {
                activityEditProfileSelectSimulBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                activityEditProfileSelectSimulBinding.txtSignUp.setTextColor(getResources().getColor(R.color.edt_hint));
            }
        } else {
            display_simual_count++;
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_round_fwhite_fill_tick, 0);
            textView.setSelected(true);
            symptoms.add(textView.getText().toString());
            String l = "";

            if (symptoms.size() > 0) {
                activityEditProfileSelectSimulBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                activityEditProfileSelectSimulBinding.txtSignUp.setTextColor(getResources().getColor(R.color.white));

            } else {
                activityEditProfileSelectSimulBinding.txtSignUp.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_bg));
                activityEditProfileSelectSimulBinding.txtSignUp.setTextColor(getResources().getColor(R.color.edt_hint));
            }
        }


        //textView.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_round_fwhite_fill_tick, 0, 0, 0);
    }
}
