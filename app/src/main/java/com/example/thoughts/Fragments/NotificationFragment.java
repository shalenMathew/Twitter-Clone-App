package com.example.thoughts.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thoughts.Adapter.NotificationAdapter;
import com.example.thoughts.Model.Notification;
import com.example.thoughts.R;
import com.example.thoughts.databinding.FragmentNotificationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NotificationFragment extends Fragment {

    FragmentNotificationBinding binding;
    List<Notification> list;
    NotificationAdapter notificationAdapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       binding =FragmentNotificationBinding.inflate(inflater,container,false);

       binding.fragmentNotificationProgress.setVisibility(View.VISIBLE);

       binding.fragmentNotificationTv.setVisibility(View.GONE);

       binding.fragmentNotificationRv.setLayoutManager(new LinearLayoutManager(getContext()));
       list = new ArrayList<>();
       notificationAdapter= new NotificationAdapter(getContext(),list);
       binding.fragmentNotificationRv.setAdapter(notificationAdapter);


       FirebaseDatabase.getInstance().getReference()
               .child("Notification")
               .child(FirebaseAuth.getInstance().getUid())
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                       binding.fragmentNotificationProgress.setVisibility(View.GONE);

                       if(snapshot.exists()){
                           binding.fragmentNotificationTv.setVisibility(View.GONE);

                           list.clear();

                           for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                               Notification notification=dataSnapshot.getValue(Notification.class);
                               if (!notification.getNotificationBy().equals(FirebaseAuth.getInstance().getUid())) {
                                   list.add(0, notification);
                               }
                           }

                           notificationAdapter.notifyDataSetChanged();

                       }else {
                           binding.fragmentNotificationTv.setVisibility(View.VISIBLE);
                       }

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

       return binding.getRoot();
    }
}