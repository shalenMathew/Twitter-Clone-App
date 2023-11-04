package com.example.thoughts;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.thoughts.databinding.ActivityEditBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class EditActivity extends AppCompatActivity {

FirebaseStorage firebaseStorage;

ActivityEditBinding binding;

Uri imgUri;

ActivityResultLauncher<String> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseStorage = FirebaseStorage.getInstance();

        Intent i = getIntent();

        binding.activityEditProfileUsername.setText(i.getStringExtra("username"));
        binding.activityEditProfileDescription.setText(i.getStringExtra("desc"));

        if(i.getStringExtra("profilePic").equals("default")){
            Glide.with(getApplicationContext()).load(R.drawable.dots).into(binding.activityEditProfileDp);
        }else {
            Glide.with(getApplicationContext()).load(i.getStringExtra("profilePic")).into(binding.activityEditProfileDp);
        }

        binding.activityEditProfileDp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              galleryLauncher.launch("image/*");

            }
        });

        binding.activityEditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              HashMap<String, Object> map = new HashMap<>();
              map.put("name",binding.activityEditProfileUsername.getText().toString());
              map.put("description",binding.activityEditProfileDescription.getText().toString());

              FirebaseDatabase.getInstance().getReference().child("Users")
                      .child(FirebaseAuth.getInstance().getUid())
                      .updateChildren(map)
                      .addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              Toast.makeText(EditActivity.this, "Updated successfully", Toast.LENGTH_SHORT).show();
                          }
                      });

                final StorageReference reference = firebaseStorage.getReference().child("profile_pic")
                        .child(FirebaseAuth.getInstance().getUid());

                if(imgUri!=null){
                    reference.putFile(imgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                            if(task.isSuccessful()){

                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {

                                        FirebaseDatabase.getInstance().getReference().child("Users")
                                                .child(FirebaseAuth.getInstance().getUid())
                                                .child("profilePic").setValue(uri.toString());

                                    }
                                });

                            }else {
                                Toast.makeText(EditActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                finish();

            }
        });

        galleryLauncher =registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {

                binding.activityEditProfileDp.setImageURI(result);
                imgUri = result;

            }
        });

    }


}