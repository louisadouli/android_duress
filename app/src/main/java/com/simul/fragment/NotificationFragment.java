package com.simul.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.simul.R;
import com.simul.adapter.HomeMessagesAdapter;
import com.simul.adapter.NotificationAdapter;
import com.simul.credentials.ServerCredentials;
import com.simul.credentials.ServerUrls;
import com.simul.databinding.FragmentNotificationBinding;
import com.simul.model.NotificationListModel;
import com.simul.utils.CommonMethods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private FragmentNotificationBinding fragmentNotificationBinding;

    private Context context;

    private NotificationAdapter notificationAdapter;

    private SharedPreferences preferences;

    private String user_id;

    private ArrayList<NotificationListModel> notificationList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentNotificationBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_notification, container, false);

        idMapping();

        serverCallForNotificationList();

        return fragmentNotificationBinding.getRoot();
    }

    private void idMapping() {

        context = getContext();

        notificationList = new ArrayList<>();

        preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);

        user_id = preferences.getString(ServerCredentials.USER_ID, null);
    }

    private void serverCallForNotificationList() {

        if(CommonMethods.checkInternetConnection(context)){

            fragmentNotificationBinding.pbNotification.setVisibility(View.VISIBLE);

            String url = ServerUrls.SIMUL_NOTIFICATION_LIST;

            RequestQueue requestQueue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);

                        int success = Integer.valueOf(jsonObject.getString(ServerCredentials.SUCCESS));

                        String message = jsonObject.getString(ServerCredentials.MESSAGE);

                        if(success == 1){

                            fragmentNotificationBinding.linMotificatioNotFound.setVisibility(View.GONE);

                            JSONArray jsonArray = jsonObject.getJSONArray(ServerCredentials.DATA);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject dataObject = jsonArray.getJSONObject(i);

                                NotificationListModel notificationListModel = new NotificationListModel();

                                String notification_id = dataObject.getString(ServerCredentials.NOTIFICATION_ID);

                                String notification_message = dataObject.getString(ServerCredentials.MESSAGE);

                                notificationListModel.setNotification_id(notification_id);
                                notificationListModel.setMessage(notification_message);

                                notificationList.add(notificationListModel);
                            }

                            notificationAdapter = new NotificationAdapter(context, notificationList);
                            fragmentNotificationBinding.recylerNotification.setLayoutManager(new LinearLayoutManager(context));
                            fragmentNotificationBinding.recylerNotification.setAdapter(notificationAdapter);

                        }else {
                            fragmentNotificationBinding.linMotificatioNotFound.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    fragmentNotificationBinding.pbNotification.setVisibility(View.GONE);
                    error.printStackTrace();
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(ServerCredentials.USER_ID, user_id);
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,

                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);

        }else {
            Toast.makeText(context, R.string.error_no_internet, Toast.LENGTH_SHORT).show();
        }
    }
}
