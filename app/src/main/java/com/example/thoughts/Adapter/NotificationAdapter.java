package com.example.thoughts.Adapter;

import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thoughts.Model.Notification;
import com.example.thoughts.Model.UserModel;
import com.example.thoughts.R;
import com.example.thoughts.databinding.FragmentNotificationItemBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

   private Context context;
  private  List<Notification> list;

    public NotificationAdapter(Context context, List<Notification> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NotificationAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_notification_item,parent,false);

       return  new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.viewHolder holder, int position) {

        Notification notification = list.get(position);

        String type = notification.getType();



        FirebaseDatabase.getInstance()
                .getReference()
                .child("Users")
                .child(notification.getNotificationBy())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel userModel = snapshot.getValue(UserModel.class);
                        String time = TimeAgo.using(notification.getNotificationAt());

                        Glide.with(context).load(userModel.getProfilePic()).into(holder.binding.fragmentNotificationItemDp);

                        if(type.equals("like")){

                            holder.binding.fragmentNotificationItemText.setText(Html.fromHtml("<b>"+userModel.getName()+"</b>" + " liked ur post "));

                            holder.binding.fragmentNotificationItemDate.setText(time +"");
                        }else {

                            holder.binding.fragmentNotificationItemText.setText(Html.fromHtml("<b>"+userModel.getName()+"</b>" + " started following u "));

                            holder.binding.fragmentNotificationItemDate.setText(time +"");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        FragmentNotificationItemBinding binding;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            binding=FragmentNotificationItemBinding.bind(itemView);
        }
    }
}
