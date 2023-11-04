package com.example.thoughts.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.thoughts.AddTweetActivity;
import com.example.thoughts.Model.Notification;
import com.example.thoughts.Model.ThoughtModel;
import com.example.thoughts.Model.UserModel;
import com.example.thoughts.R;
import com.example.thoughts.databinding.ThoughtItemBinding;
import com.example.thoughts.databinding.ThoughtItemWithImgBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.List;

public class ThoughtAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

Context context;

List<ThoughtModel> list;

boolean testClick=false;

public final int LAYOUT_ITEM_WITHOUT_IMG=0;
public final int LAYOUT_ITEM_WITH_IMG=1;



    public ThoughtAdapter(Context context, List<ThoughtModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType==LAYOUT_ITEM_WITHOUT_IMG) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thought_item, parent, false);

            return new ThoughtWithoutImg(view);
        }else {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.thought_item_with_img, parent, false);

            return new ThoughtWithImg(view);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ThoughtModel thoughtModel = list.get(position);

        if(holder.getItemViewType()==LAYOUT_ITEM_WITHOUT_IMG){

            ThoughtWithoutImg viewHolder = (ThoughtWithoutImg) holder;

            viewHolder.binding.tweetItemThought.setText(thoughtModel.getThought());

            viewHolder.getStatus(thoughtModel);

            String postId= thoughtModel.getPostId();
            String currUserId = FirebaseAuth.getInstance().getUid();

            viewHolder.getLikeStatus(postId,currUserId);

            viewHolder.binding.tweetItemLike.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                   testClick=true;

                  FirebaseDatabase.getInstance().getReference()
                          .child("Post")
                          .child(postId)
                          .child("likes")
                          .addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot snapshot) {


                                  if (testClick){

                                      if (snapshot.hasChild(currUserId)){

                                          FirebaseDatabase.getInstance().getReference()
                                                  .child("Post")
                                                  .child(postId)
                                                  .child("likes")
                                                  .child(currUserId).removeValue();

                                          testClick=false;
                                      }
                                      else {

                                          FirebaseDatabase.getInstance().getReference()
                                                  .child("Post")
                                                  .child(postId)
                                                  .child("likes")
                                                  .child(currUserId).setValue(true);

                                          Notification notification = new Notification();
                                          notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                          notification.setNotificationAt(new Date().getTime());
                                          notification.setPostID(thoughtModel.getPostId());
                                          notification.setPostedBy(thoughtModel.getPostedBy());
                                          notification.setType("like");

                                          FirebaseDatabase.getInstance().getReference()
                                                  .child("Notification")
                                                  .child(thoughtModel.getPostedBy())
                                                  .push()
                                                  .setValue(notification);

                                          testClick=false;
                                      }

                                  }

                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError error) {

                              }
                          });

              }
          });

            viewHolder.binding.tweetBackground.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Post")
                            .child(postId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    ThoughtModel thoughtModel = snapshot.getValue(ThoughtModel.class);

                                    if(thoughtModel!=null && thoughtModel.getPostedBy().equals(FirebaseAuth.getInstance().getUid())){
                                        alertDialogBox(postId);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    return true;
                }
            });


            viewHolder.binding.tweetItemRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ThoughtModel thoughtModel2 = new ThoughtModel();
                    if(thoughtModel.getImg()!=null) {
                        thoughtModel2.setImg(thoughtModel.getImg());
                    }
                    thoughtModel2.setPostedBy(FirebaseAuth.getInstance().getUid());
                    thoughtModel2.setPostedAt(new Date().getTime());
                    thoughtModel2.setThought(thoughtModel.getThought());

                    FirebaseDatabase.getInstance().getReference()
                            .child("Post")
                            .push()
                            .setValue(thoughtModel2)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Glide.with(context).load(R.drawable.icon_retweet_black).into(viewHolder.binding.tweetItemRetweet);

                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("Post")
                                            .child(postId)
                                            .child("retweetCount")
                                            .setValue(thoughtModel.getRetweetCount()+1);

                                    Toast.makeText(context, "retweeted successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }
        else if (holder.getItemViewType()==LAYOUT_ITEM_WITH_IMG) {

            ThoughtWithImg viewHolder = (ThoughtWithImg) holder;

            viewHolder.binding.thoughtItemWithImgThought.setText(thoughtModel.getThought());

            Glide.with(context).load(thoughtModel.getImg()).into(viewHolder.binding.thoughtItemWithImgImg);

            viewHolder.getStatus(thoughtModel);
            String postId= thoughtModel.getPostId();

            String currUserId = FirebaseAuth.getInstance().getUid();

            viewHolder.getLikeStatus(postId,currUserId);

            viewHolder.binding.thoughtItemWithImgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    testClick=true;

                    FirebaseDatabase.getInstance().getReference()
                            .child("Post")
                            .child(postId)
                            .child("likes")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {


                                    if (testClick){

                                        if (snapshot.hasChild(currUserId)){

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Post")
                                                    .child(postId)
                                                    .child("likes")
                                                    .child(currUserId).removeValue();

                                            testClick=false;
                                        }
                                        else {

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Post")
                                                    .child(postId)
                                                    .child("likes")
                                                    .child(currUserId).setValue(true);

                                            Notification notification = new Notification();
                                            notification.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notification.setNotificationAt(new Date().getTime());
                                            notification.setPostID(thoughtModel.getPostId());
                                            notification.setPostedBy(thoughtModel.getPostedBy());
                                            notification.setType("like");

                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("Notification")
                                                    .child(thoughtModel.getPostedBy())
                                                    .push()
                                                    .setValue(notification);

                                            testClick=false;
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                }
            });

            viewHolder.binding.thoughtItemWithImgBk.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {


                    FirebaseDatabase.getInstance()
                            .getReference()
                            .child("Post")
                            .child(postId)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    ThoughtModel thoughtModel = snapshot.getValue(ThoughtModel.class);

                                    if(thoughtModel!=null && thoughtModel.getPostedBy().equals(FirebaseAuth.getInstance().getUid())){
                                        alertDialogBox(postId);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    return true;
                }
            });


            viewHolder.binding.thoughtItemWithImgRetweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ThoughtModel thoughtModel2 = new ThoughtModel();
                    if(thoughtModel.getImg()!=null) {
                        thoughtModel2.setImg(thoughtModel.getImg());
                    }
                    thoughtModel2.setPostedBy(FirebaseAuth.getInstance().getUid());
                    thoughtModel2.setPostedAt(new Date().getTime());
                    thoughtModel2.setThought(thoughtModel.getThought());

                    FirebaseDatabase.getInstance().getReference()
                            .child("Post")
                            .push()
                            .setValue(thoughtModel2)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    FirebaseDatabase.getInstance()
                                            .getReference()
                                            .child("Post")
                                            .child(postId)
                                            .child("retweetCount")
                                            .setValue(thoughtModel.getRetweetCount()+1);

                                    Toast.makeText(context, "retweeted successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });


        }
    }

    private void alertDialogBox(String postId) {

        AlertDialog.Builder alertBox = new AlertDialog.Builder(context);
        alertBox.setTitle("Delete post?");
        alertBox.setMessage("r u sure u want to delete this post ?");
        alertBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Delete(postId);
            }
        });

        alertBox.setNegativeButton("No",null);

        alertBox.show();

    }

    private void Delete(String postId) {

        FirebaseDatabase.getInstance()
                .getReference()
                .child("Post")
                .child(postId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "error occured", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

  public class ThoughtWithoutImg extends RecyclerView.ViewHolder{

        ThoughtItemBinding binding;
        public ThoughtWithoutImg(@NonNull View itemView) {
            super(itemView);
            binding = ThoughtItemBinding.bind(itemView);
        }

        public void getLikeStatus(String postId,String currUserId){

            FirebaseDatabase.getInstance().getReference()
                    .child("Post")
                    .child(postId)
                    .child("likes")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.hasChild(currUserId)){

                                int likeCount = (int) snapshot.getChildrenCount();
                                binding.tweetItemLikeTv.setText(likeCount+"");
                                if(context!=null && !((Activity)context).isDestroyed()  ) {
                                    Glide.with(context).load(R.drawable.icon_heart_black).into(binding.tweetItemLike);
                                }

                            }else {

                                int likeCount = (int) snapshot.getChildrenCount();
                                binding.tweetItemLikeTv.setText(likeCount+"");
                                if(context!=null && !((Activity)context).isDestroyed()) {
                                    Glide.with(context).load(R.drawable.icon_heart_grey).into(binding.tweetItemLike);
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }

        public void getStatus(ThoughtModel thoughtModel){

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(thoughtModel.getPostedBy())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){

                                UserModel userModel = snapshot.getValue(UserModel.class);
                                binding.tweetItemName.setText(userModel.getName());
                                Glide.with(context).load(userModel.getProfilePic()).into(binding.tweetItemDp);
                              binding.tweetItemCommentTv.setText(thoughtModel.getCommentCount()+"");
                              binding.tweetItemRetweetTv.setText(thoughtModel.getRetweetCount()+"");

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }


    }


    public class ThoughtWithImg extends RecyclerView.ViewHolder{

        ThoughtItemWithImgBinding binding;

        public ThoughtWithImg(@NonNull View itemView) {
            super(itemView);
            binding = ThoughtItemWithImgBinding.bind(itemView);
        }

        public void getLikeStatus(String postId,String currUserId){

            FirebaseDatabase.getInstance().getReference()
                    .child("Post")
                    .child(postId)
                    .child("likes")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.hasChild(currUserId)){

                                int likeCount = (int) snapshot.getChildrenCount();
                                binding.thoughtItemWithImgLikeTv.setText(likeCount+"");
                                if(context!=null && !((Activity)context).isDestroyed()  ) {
                                    Glide.with(context).load(R.drawable.icon_heart_black).into(binding.thoughtItemWithImgLike);
                                }

                            }else {

                                int likeCount = (int) snapshot.getChildrenCount();
                                binding.thoughtItemWithImgLikeTv.setText(likeCount+"");
                                if(context!=null && !((Activity)context).isDestroyed()) {
                                    Glide.with(context).load(R.drawable.icon_heart_grey).into(binding.thoughtItemWithImgLike);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


        }

        public void getStatus(ThoughtModel thoughtModel){

            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Users")
                    .child(thoughtModel.getPostedBy())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){

                                UserModel userModel = snapshot.getValue(UserModel.class);
                                binding.thoughtItemWithImgName.setText(userModel.getName());
                                Glide.with(context).load(userModel.getProfilePic()).into(binding.thoughtItemWithImgDp);
                                binding.thoughtItemWithImgCommentTv.setText(thoughtModel.getCommentCount()+"");
                                binding.thoughtItemWithImgRetweetTv.setText(thoughtModel.getRetweetCount()+"");

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

        }

    }


    @Override
    public int getItemViewType(int position) {
        return list.get(position).getImg()!=null?LAYOUT_ITEM_WITH_IMG:LAYOUT_ITEM_WITHOUT_IMG ;
    }

}
