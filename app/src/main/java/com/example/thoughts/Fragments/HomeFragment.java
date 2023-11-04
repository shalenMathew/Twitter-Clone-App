package com.example.thoughts.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thoughts.Adapter.StoryAdapter;
import com.example.thoughts.Adapter.ThoughtAdapter;
import com.example.thoughts.AddTweetActivity;
import com.example.thoughts.MainActivity;
import com.example.thoughts.Model.StoryModel;
import com.example.thoughts.Model.ThoughtModel;
import com.example.thoughts.Model.UserModel;
import com.example.thoughts.Model.UserStories;
import com.example.thoughts.R;
import com.example.thoughts.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;

    Context context;

    List<ThoughtModel > list;

    ActivityResultLauncher<String> galleryLauncher;

    ProgressDialog dialog;

    List<StoryModel> storyModelList;

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


        binding= FragmentHomeBinding.inflate(inflater, container, false);

        // dialog bar
        dialog= new ProgressDialog(getContext());
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("story Uploading");
        dialog.setMessage("Please wait...");
        dialog.setCancelable(false);
        binding.fragmentHomeProgress.setVisibility(View.VISIBLE);

        // post rv
        list=new ArrayList<>();
        binding.recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        ThoughtAdapter thoughtAdapter = new ThoughtAdapter(context,list);


        // story rv
        storyModelList = new ArrayList<>();
        binding.storyRv.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        StoryAdapter storyAdapter = new StoryAdapter(context,storyModelList);
        binding.storyRv.setNestedScrollingEnabled(false);
        binding.storyRv.setAdapter(storyAdapter);

        FirebaseDatabase.getInstance().getReference()
                        .child("Story")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                        storyModelList.clear();

                                        //setting story in recyclerview

                                        if(snapshot.exists()){
                                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                                                StoryModel storyModel= new StoryModel();
                                                storyModel.setStoryBy(dataSnapshot.getKey());//getting the id
//
                                                ArrayList<UserStories> storiesArrayList= new ArrayList<>();

                                                for (DataSnapshot snapshot1 : dataSnapshot.child("userStories").getChildren()){
                                                    UserStories userStories = snapshot1.getValue(UserStories.class);
                                                    storiesArrayList.add(userStories);
                                                }
                                                storyModel.setStories(storiesArrayList);
                                                storyModelList.add(storyModel);
                                            }

                                            storyAdapter.notifyDataSetChanged();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // setting profile pic

                                if(context==null || !isAdded()){
                                    return;
                                }

                                UserModel userModel = snapshot.getValue(UserModel.class);

                                // setting profile pic
                                Glide.with(context).load(userModel.getProfilePic())
                                        .into(binding.fragmentHomeProfileImg);
                                // setting story profile pic
                                Glide.with(context).load(userModel.getProfilePic())
                                        .into(binding.fragmentHomeAddStory);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });


        FirebaseDatabase.getInstance().getReference()
                .child("Post")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){

                            binding.fragmentHomeProgress.setVisibility(View.GONE);
                            binding.noPostTV.setVisibility(View.GONE);

                            //setting post
                            list.clear();

                            for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                                ThoughtModel thoughtModel = dataSnapshot.getValue(ThoughtModel.class);
                                thoughtModel.setPostId(dataSnapshot.getKey());
                                list.add(0,thoughtModel);
                            }

                            binding.recyclerView2.setAdapter(thoughtAdapter);
                            thoughtAdapter.notifyDataSetChanged();
                        }else {
                            binding.fragmentHomeProgress.setVisibility(View.GONE);
                            binding.noPostTV.setText("No post Yet!!!");
                            binding.noPostTV.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        binding.fragmentHomeAddStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryLauncher.launch("image/*");
            }
        });

        galleryLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                // story upload

                dialog.show();

               final  StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                       .child("Story")
                       .child(FirebaseAuth.getInstance().getUid())
                       .child(new Date().getTime()+"");

               storageReference.putFile(result)
                       .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                               storageReference.getDownloadUrl()
                                       .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                           @Override
                                           public void onSuccess(Uri uri) {

                                               StoryModel storyModel = new StoryModel();
                                               storyModel.setStoryAt(new Date().getTime());
                                               storyModel.setStoryBy(FirebaseAuth.getInstance().getUid());

                                               FirebaseDatabase.getInstance().getReference()
                                                       .child("Story")
                                                       .child(FirebaseAuth.getInstance().getUid())
                                                       .child("postedBy")
                                                       .setValue(storyModel.getStoryBy())
                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                           @Override
                                                           public void onSuccess(Void unused) {

                                                               UserStories userStories = new UserStories(uri.toString(),storyModel.getStoryAt());

                                                               FirebaseDatabase.getInstance().getReference()
                                                                       .child("Story")
                                                                       .child(FirebaseAuth.getInstance().getUid())
                                                                       .child("userStories")
                                                                       .push()
                                                                       .setValue(userStories)
                                                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                           @Override
                                                                           public void onSuccess(Void unused) {
                                                                               dialog.dismiss();
                                                                           }
                                                                       });

                                                           }
                                                       });

                                           }
                                       });
                           }
                       });

            }
        });



        return binding.getRoot();
    }
}