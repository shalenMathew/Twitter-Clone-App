package com.example.thoughts.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thoughts.Adapter.FollowersAdapter;
import com.example.thoughts.Model.UserModel;
import com.example.thoughts.R;
import com.example.thoughts.databinding.FragmentConnectBinding;
import com.example.thoughts.databinding.FragmentConnectItemBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ConnectFragment extends Fragment {

FragmentConnectBinding binding;

FollowersAdapter followersAdapter;
ArrayList<UserModel> list;

FirebaseDatabase firebaseDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance();
        list=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding= FragmentConnectBinding.inflate(inflater, container, false);

        binding.fragmentConnectPb.setVisibility(View.VISIBLE);

        followersAdapter= new FollowersAdapter(getContext(),list);

        binding.fragmentConnectRv.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.fragmentConnectRv.setAdapter(followersAdapter);


        firebaseDatabase.getReference().child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                binding.fragmentConnectPb.setVisibility(View.GONE);

                if(snapshot.exists()){

                    list.clear();

                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                        UserModel userModel = dataSnapshot.getValue(UserModel.class);
                        userModel.setId(dataSnapshot.getKey());

                        if (!userModel.getId().equals(FirebaseAuth.getInstance().getUid()) ){
                            list.add(userModel);
                        }

                    }

                    followersAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return  binding.getRoot();
    }
}