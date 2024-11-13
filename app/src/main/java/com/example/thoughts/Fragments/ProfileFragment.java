package com.example.thoughts.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thoughts.Adapter.ThoughtAdapter;
import com.example.thoughts.EditActivity;
import com.example.thoughts.Model.ThoughtModel;
import com.example.thoughts.Model.UserModel;
import com.example.thoughts.R;
import com.example.thoughts.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    UserModel  userModel;

    Context context;

    List<ThoughtModel> list;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.context=null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater,container,false);

        list = new ArrayList<>();

        binding.fragmentHomeThoughtsRv.setLayoutManager(new LinearLayoutManager(getContext()));

        ThoughtAdapter thoughtAdapter = new ThoughtAdapter(getContext(),list);

        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Post")
                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        list.clear();

                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                                            ThoughtModel thoughtModel = dataSnapshot.getValue(ThoughtModel.class);
                                            thoughtModel.setPostId(dataSnapshot.getKey());

                                            if(thoughtModel.getPostedBy().equals(FirebaseAuth.getInstance().getUid())){
                                                list.add(0,thoughtModel);
                                            }
                                            binding.fragmentHomeThoughtsRv.setAdapter(thoughtAdapter);
                                            thoughtAdapter.notifyDataSetChanged();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

        binding.progressBar.setVisibility(View.VISIBLE);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if(context==null || !isAdded()){
                            return;
                        }

                        if(snapshot.exists()){

                            binding.progressBar.setVisibility(View.GONE);

                            userModel = snapshot.getValue(UserModel.class);

                            binding.fragmentHomeUsername.setText(userModel.getName());
                            binding.fragmentHomeFollowers.setText(String.valueOf(userModel.getFollowersCount()));
                            binding.fragmentHomeFollowing.setText(String.valueOf(userModel.getFollowingCount()));
                            binding.fragmentHomeThoughts.setText(String.valueOf(userModel.getThoughtsCount()));
                            binding.fragmentHomeDescription.setText(userModel.getDescription());

                            if(userModel.getProfilePic().equals("default")){
                                Glide.with(context).load(R.drawable.dots).into(binding.fragmentHomeProfileImg);
                            }else {
                                Glide.with(context).load(userModel.getProfilePic()).into(binding.fragmentHomeProfileImg);
                            }

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.fragmentHomeEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getContext(), EditActivity.class);

                if(userModel!=null) {
                    i.putExtra("username", userModel.getName());
                    i.putExtra("desc", userModel.getDescription());
                    i.putExtra("profilePic", userModel.getProfilePic());
                    startActivity(i);
                }else {
                    Toast.makeText(context, "Hold on let him cook", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return  binding.getRoot();
    }


}