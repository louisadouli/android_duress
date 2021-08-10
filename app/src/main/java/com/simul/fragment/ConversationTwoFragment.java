package com.simul.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.simul.R;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.FragmentConversationTwoBinding;
import com.simul.utils.CommonMethods;
import com.simul.utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationTwoFragment extends Fragment {

    private FragmentConversationTwoBinding fragmentConversationTwoBinding;

    private Context context;

    private String question, symptoms, user_id;

    private boolean category_show = true;

    private boolean anyCheck = false;

    private SharedPreferences preferences;

    private Bitmap postBitmap;

    private String simul_symptoms;

    List<String> symptomsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fragmentConversationTwoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversation_two, container, false);
        EmojiCompat.init(new BundledEmojiCompatConfig(getContext()));
        idMapping();

        setClickListener();

        setUpSymptomsInView();

        return fragmentConversationTwoBinding.getRoot();
    }

    private void idMapping() {

        context = getContext();

        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        user_id = preferences.getString(ServerCredentials.USER_ID, null);

        question = getArguments().getString(ServerCredentials.QUESTION);

        postBitmap = getArguments().getParcelable("BitmapImage");

        String o = "";
    }

    private void setClickListener() {

        fragmentConversationTwoBinding.relPickCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (category_show) {
                    fragmentConversationTwoBinding.linCategory.setVisibility(View.GONE);
                    fragmentConversationTwoBinding.ivUpDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_orange_24dp));
                    category_show = false;

                } else {
                    fragmentConversationTwoBinding.linCategory.setVisibility(View.VISIBLE);
                    fragmentConversationTwoBinding.ivUpDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_up_24dp));
                    category_show = true;

                }
            }
        });

       /* fragmentConversationTwoBinding.relDiabitesTypeOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentConversationTwoBinding.ivDiabitesTypeOne.setImageResource(R.drawable.ic_check_circle_sky_blue_24dp);
                fragmentConversationTwoBinding.ivDepresion.setImageResource(R.drawable.ic_radio_button_unchecked_green_24dp);
                symptoms = fragmentConversationTwoBinding.txtSymptomsOne.getText().toString();
                anyCheck = true;
            }
        });*/

      /*  fragmentConversationTwoBinding.relDepresion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragmentConversationTwoBinding.ivDepresion.setImageResource(R.drawable.ic_check_circle_green_24dp);
                fragmentConversationTwoBinding.ivDiabitesTypeOne.setImageResource(R.drawable.ic_radio_button_unchecked_sky_blue_24dp);
                symptoms = fragmentConversationTwoBinding.txtSymptomsTwo.getText().toString();
                anyCheck = true;
            }
        });*/

        fragmentConversationTwoBinding.txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (anyCheck) {
                    setUpDialog();
                } else {
                    Toast.makeText(context, "Please select post related simul.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void setUpSymptomsInView() {

        simul_symptoms = preferences.getString(ServerCredentials.SIMUL_SYMPTOMS, null);

        String str[] = simul_symptoms.split(",");

        symptomsList = new ArrayList<String>();

        symptomsList = Arrays.asList(str);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.weight = 1.0f;
        params.gravity = Gravity.RIGHT;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        float d = this.getResources().getDisplayMetrics().density;
        int padding = (int) (10 * d);

        final RadioButton[] rb = new RadioButton[5];
        final RadioGroup rg = new RadioGroup(context);//create the RadioGroup
        rg.setOrientation(RadioGroup.VERTICAL);//or RadioGroup.VERTICAL

        for (int i = 0; i < symptomsList.size(); i++) {

            final String tag = getColoredSpanned("S/", "#F47521");
            final String symtums = getColoredSpanned(symptomsList.get(i), "#B00296FF");

            rb[i] = new RadioButton(context);
            rb[i].setButtonDrawable(getResources().getDrawable(android.R.color.transparent));
            rb[i].setGravity(Gravity.LEFT);
            rb[i].setTextSize(18);
            rb[i].setLayoutParams(layoutParams);
            rb[i].setPadding(padding, padding, padding, padding);

            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_radio_button_unchecked_light_tometo_red_24dp);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            if (symptomsList.get(i).equals("Diabetes")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_sky_blue_24dp), null);
            } else if (symptomsList.get(i).equals("Cancer")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_yellow_24dp), null);
            } else if (symptomsList.get(i).equals("Mental Health")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_green_24dp), null);
            } else if (symptomsList.get(i).equals("Genetic Discordes")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_sky_blue_24dp), null);
            } else if (symptomsList.get(i).equals("Age Associated Dieases")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_blue_24dp), null);
            } else if (symptomsList.get(i).equals("Heart Diases")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_light_tometo_red_24dp), null);
            } else if (symptomsList.get(i).equals("MND")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_mnd_24dp), null);
            } else if (symptomsList.get(i).equals("Asthma")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_asthma_24dp), null);
            } else if (symptomsList.get(i).equals("Trauma")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_trauma_24dp), null);
            } else if (symptomsList.get(i).equals("Tumors")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_tumors_24dp), null);
            } else if (symptomsList.get(i).equals("Obesity")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_obesity_24dp), null);
            } else if (symptomsList.get(i).equals("Sexually Transmitted Diseases")) {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_light_tometo_red_24dp), null);
            } else {
                rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_light_tometo_red_24dp), null);
            }

            rb[i].setText(Html.fromHtml(tag + "" + symtums));
            rb[i].setTypeface(null, Typeface.BOLD);

            if (symptomsList.get(i).equals("Diabetes")) {
                rb[i].setTextColor(getResources().getColor(R.color.light_sky_blue));
            } else if (symptomsList.get(i).equals("Cancer")) {
                rb[i].setTextColor(getResources().getColor(R.color.yellow));
            } else if (symptomsList.get(i).equals("Mental Health")) {
                rb[i].setTextColor(getResources().getColor(R.color.green));
            } else if (symptomsList.get(i).equals("Genetic Discordes")) {
                rb[i].setTextColor(getResources().getColor(R.color.light_sky_blue));
            } else if (symptomsList.get(i).equals("Age Associated Dieases")) {
                rb[i].setTextColor(getResources().getColor(R.color.blue));
            } else if (symptomsList.get(i).equals("Heart Diases")) {
                rb[i].setTextColor(getResources().getColor(R.color.light_tometo_red));
            } else if (symptomsList.get(i).equals("MND")) {
                rb[i].setTextColor(Color.parseColor("#056CFE"));
            } else if (symptomsList.get(i).equals("Asthma")) {
                rb[i].setTextColor(Color.parseColor("#4CD964"));
            } else if (symptomsList.get(i).equals("Trauma")) {
                rb[i].setTextColor(Color.parseColor("#3C81B5"));
            } else if (symptomsList.get(i).equals("Tumors")) {
                rb[i].setTextColor(Color.parseColor("#FFD519"));
            } else if (symptomsList.get(i).equals("Obesity")) {
                rb[i].setTextColor(Color.parseColor("#4730B8"));
            } else if (symptomsList.get(i).equals("Sexually Transmitted Diseases")) {
                rb[i].setTextColor(Color.parseColor("#BFFB496D"));
            } else {
                rb[i].setTextColor(Color.parseColor("#BFFB496D"));
            }


            rb[i].setId(i);

            final int finalI = i;
            final int finalI1 = i;
            final int finalI2 = i;
            rb[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //  Toast.makeText(context, ""+buttonView.getId(), Toast.LENGTH_SHORT).show();
                    rg.clearCheck();
                    anyCheck = true;
                    fragmentConversationTwoBinding.txtPost.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.button_orange_bg));
                    fragmentConversationTwoBinding.txtPost.setTextColor(getResources().getColor(R.color.white));
                    String str = String.valueOf(buttonView.getText());
                    symptoms = str.replace("S/", ""); //strNew is 'HelloWorldJavaUsers'


                    //Toast.makeText(context, symptoms, Toast.LENGTH_SHORT).show();

                    if (isChecked) {
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_check_circle_light_tometo_red_24dp);
                        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                        if (symptomsList.get(finalI).equals("Diabetes")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_sky_blue_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Cancer")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_yellow_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Mental Health")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_green_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Genetic Discordes")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_sky_blue_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Age Associated Dieases")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_blue_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Heart Diases")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_light_tometo_red_24dp), null);
                        } else if (symptomsList.get(finalI).equals("MND")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_mnd_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Asthma")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_asthma_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Trauma")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_trauma_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Tumors")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_tumors_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Obesity")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_obesity_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Sexually Transmitted Diseases")) {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_light_tometo_red_24dp), null);
                        } else {
                            rb[finalI1].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_check_circle_light_tometo_red_24dp), null);
                        }

                    } else {
                        Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.ic_radio_button_unchecked_light_tometo_red_24dp);
                        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                        if (symptomsList.get(finalI).equals("Diabetes")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_sky_blue_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Cancer")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_yellow_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Mental Health")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_green_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Genetic Discordes")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_sky_blue_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Age Associated Dieases")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_blue_24dp), null);
                        } else if (symptomsList.get(finalI).equals("Heart Diases")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_light_tometo_red_24dp), null);
                        } else if (symptomsList.get(finalI2).equals("MND")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_mnd_24dp), null);
                        } else if (symptomsList.get(finalI2).equals("Asthma")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_asthma_24dp), null);
                        } else if (symptomsList.get(finalI2).equals("Trauma")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_trauma_24dp), null);
                        } else if (symptomsList.get(finalI2).equals("Tumors")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_tumors_24dp), null);
                        } else if (symptomsList.get(finalI2).equals("Obesity")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_obesity_24dp), null);
                        } else if (symptomsList.get(finalI2).equals("Sexually Transmitted Diseases")) {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_light_tometo_red_24dp), null);
                        } else {
                            rb[finalI2].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.ic_radio_button_unchecked_light_tometo_red_24dp), null);
                        }
                    }


                }
            });

            rg.addView(rb[i]);
        }

        fragmentConversationTwoBinding.linCategory.addView(rg);
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }

    private void setUpDialog() {

        final Dialog dialog = new Dialog(context);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_post_it);

        dialog.setTitle("Custom Dialog");

        dialog.show();

        Window window = dialog.getWindow();
        //window.setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        TextView txt_No = dialog.findViewById(R.id.txt_No);

        TextView txt_postIt = dialog.findViewById(R.id.txt_postIt);

        txt_postIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().finish();
                serverCallForPostQuestion();
                dialog.dismiss();
            }
        });

        txt_No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void serverCallForPostQuestion() {
        if (CommonMethods.checkInternetConnection(context)) {

            String url = ServerUrls.SIMUL_POST_QUE;

            fragmentConversationTwoBinding.setProcessing(true);

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            VolleyMultipartRequest stringRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
                @Override
                public void onResponse(NetworkResponse response) {

                    try {
                        JSONObject jsonObject = new JSONObject(new String(response.data));

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if (success == 1) {
                            fragmentConversationTwoBinding.setProcessing(false);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                            getActivity().finish();

                        } else {
                            // fragmentConversationTwoBinding.pbPostQuestion.setVisibility(View.GONE);
                            fragmentConversationTwoBinding.setProcessing(false);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        fragmentConversationTwoBinding.setProcessing(true);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fragmentConversationTwoBinding.setProcessing(true);
                    error.printStackTrace();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);
                    params.put(ServerCredentials.QUESTION, question);
                    params.put(ServerCredentials.SYMPTOMS, symptoms);
                    return params;
                }

                @Override
                protected Map<String, DataPart> getByteData() {

                    Map<String, DataPart> params = new HashMap<>();
                    // file name could found file base or direct access from real path
                    // for now just get bitmap data from ImageView

                    if (postBitmap != null) {

                        preferences.edit().putBoolean("isProfilePicUpadated", false).apply();

                        params.put("question_img", new DataPart(System.currentTimeMillis() + ".jpg", getFileDataFromDrawable(postBitmap)));
                    }

                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);

        } else {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }

}
