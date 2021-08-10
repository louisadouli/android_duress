package com.simul.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.simul.R;
import com.simul.activity.ViewOthersProfileActivity;
import com.simul.databinding.AdapterNotificationListBinding;
import com.simul.model.NotificationListModel;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private AdapterNotificationListBinding adapterNotificationListBinding;
    private Context context;
    private ArrayList<NotificationListModel> notificationList;

    public NotificationAdapter(Context context, ArrayList<NotificationListModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        adapterNotificationListBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.adapter_notification_list, parent, false);
        return new NotificationAdapter.MyViewHolder(adapterNotificationListBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        NotificationListModel notificationListModel = notificationList.get(position);

        adapterNotificationListBinding.txtName.setText("Adamlind");

        adapterNotificationListBinding.txtMessage.setText(notificationListModel.getMessage());

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
