package com.example.thoughts.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thoughts.Model.FollowersModel;
import com.example.thoughts.Model.Notification;
import com.example.thoughts.Model.UserModel;
import com.example.thoughts.R;
import com.example.thoughts.databinding.FragmentConnectItemBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {


    Context context;
    ArrayList<UserModel> list;

    public FollowersAdapter(Context context, ArrayList<UserModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FollowersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_connect_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersAdapter.ViewHolder holder, int position) {


        UserModel userModel = list.get(position);

        holder.binding.fragmentConnectItemName.setText(userModel.getName());

        Glide.with(context).load(userModel.getProfilePic()).into(holder.binding.fragmentConnectItemDp);

        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(userModel.getId())
                .child("followers")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                       if (snapshot.exists()){

                           holder.binding.fragmentConnectItemFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.following_btn));
                           holder.binding.fragmentConnectItemFollow.setText("Following");
                           holder.binding.fragmentConnectItemFollow.setEnabled(false);

                       }else {

                          holder.binding.fragmentConnectItemFollow.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {

                                  FollowersModel followersModel = new FollowersModel();
                                  followersModel.setFollowedBy(FirebaseAuth.getInstance().getUid());
                                  followersModel.setDate(new Date().getTime());

                                  FirebaseDatabase.getInstance().getReference("Users")
                                          .child(userModel.getId())
                                          .child("followers")
                                          .child(FirebaseAuth.getInstance().getUid())
                                          .setValue(followersModel)
                                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void unused) {

                                                  FirebaseDatabase.getInstance().getReference()
                                                          .child("Users")
                                                          .child(userModel.getId())
                                                          .child("followersCount")
                                                          .setValue(userModel.getFollowersCount()+1)
                                                          .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                              @Override
                                                              public void onSuccess(Void unused) {

                                                                  holder.binding.fragmentConnectItemFollow.setBackgroundDrawable(ContextCompat.getDrawable(context,R.drawable.following_btn));
                                                                  holder.binding.fragmentConnectItemFollow.setText("Following");
                                                                  holder.binding.fragmentConnectItemFollow.setEnabled(false);
                                                                  Toast.makeText(context, "U started following " + userModel.getName(), Toast.LENGTH_SHORT).show();

                                                                  Notification notification = new Notification();
                                                                  notification.setType("follow");
                                                                  notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                                                  notification.setNotificationAt(new Date().getTime());

                                                                  FirebaseDatabase.getInstance().getReference()
                                                                          .child("Notification")
                                                                          .child(userModel.getId())
                                                                          .push()
                                                                          .setValue(notification);

                                                              }
                                                          });

                                                  FirebaseDatabase.getInstance().getReference()
                                                          .child("Users")
                                                          .child(FirebaseAuth.getInstance().getUid())
                                                          .child("followingCount")
                                                          .addListenerForSingleValueEvent(new ValueEventListener() {
                                                              @Override
                                                              public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                  Integer followingCount = snapshot.getValue(Integer.class);

                                                                  FirebaseDatabase.getInstance().getReference()
                                                                          .child("Users")
                                                                          .child(FirebaseAuth.getInstance().getUid())
                                                                          .child("followingCount")
                                                                          .setValue(followingCount+1);

                                                              }

                                                              @Override
                                                              public void onCancelled(@NonNull DatabaseError error) {

                                                              }
                                                          });

                                              }
                                          }).addOnFailureListener(new OnFailureListener() {
                                              @Override
                                              public void onFailure(@NonNull Exception e) {
                                                  Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                              }
                                          });

                              }
                          });

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        FragmentConnectItemBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=FragmentConnectItemBinding.bind(itemView);
        }
    }
}
