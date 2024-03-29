package com.example.thoughts;

import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thoughts.Model.ThoughtModel;
import com.example.thoughts.Model.UserModel;
import com.example.thoughts.databinding.ActivityAddThoughtBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class AddTweetActivity extends AppCompatActivity {

    ActivityAddThoughtBinding binding;

    Uri imgUri;

    ActivityResultLauncher<Intent> gallery;

    String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddThoughtBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        UserModel userModel = snapshot.getValue(UserModel.class);

                        Glide.with(AddTweetActivity.this).load(userModel.getProfilePic())
                                .into(binding.activityAddThoughtDp);

                        binding.activityAddThoughtUsername.setText(userModel.getName());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.activityAddThoughtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(binding.activityAddThoughtEt.getText())){
                ThoughtModel  thoughtModel = new ThoughtModel();

                    if(imgUrl!=null) {
                        thoughtModel.setImg(imgUrl);
                    }
                    thoughtModel.setPostedBy(FirebaseAuth.getInstance().getUid());
                    thoughtModel.setPostedAt(new Date().getTime());
                    thoughtModel.setThought(binding.activityAddThoughtEt.getText().toString());

                    FirebaseDatabase.getInstance().getReference()
                            .child("Users")
                            .child(FirebaseAuth.getInstance().getUid())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    UserModel userModel= snapshot.getValue(UserModel.class);
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("Users")
                                            .child(FirebaseAuth.getInstance().getUid())
                                            .child("thoughtsCount")
                                            .setValue(userModel.getThoughtsCount()+1);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });


                    FirebaseDatabase.getInstance().getReference()
                            .child("Post")
                            .push()
                            .setValue(thoughtModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    Toast.makeText(AddTweetActivity.this, "Posted successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });

                }else {
                    Toast.makeText(AddTweetActivity.this, "field is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.activityAddThoughtGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                gallery.launch(intent);
            }
        });

        gallery=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (result.getResultCode()==RESULT_OK){
                    if (result.getData() != null && result.getData().getData() != null) {

                        Uri uri = result.getData().getData();
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        binding.activityAddThoughtImg.setImageURI(uri);
                        imgUri = uri;

                        Toast.makeText(AddTweetActivity.this, "Image uploading to server...Don't hit Post yet", Toast.LENGTH_SHORT).show();

                        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                                .child("Post")
                                .child(FirebaseAuth.getInstance().getUid())
                                .child(new Date().getTime()+"");

                        storageReference.putFile(result.getData().getData())
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        storageReference.getDownloadUrl()
                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imgUrl = uri.toString();
                                                        Toast.makeText(AddTweetActivity.this, "Ready to go...", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                });
                    }
                }
            }
        });
    }
}