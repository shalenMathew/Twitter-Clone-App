package com.example.thoughts.Model;

public class ThoughtModel {


   private String thought;
   private String img;
   private int likeCount;
   private int commentCount;
   private int retweetCount;
   private String postedBy;

   private long postedAt;

   private String postId;



   public String getThought() {
      return thought;
   }

   public void setThought(String thought) {
      this.thought = thought;
   }

   public String getImg() {
      return img;
   }

   public void setImg(String img) {
      this.img = img;
   }

   public int getLikeCount() {
      return likeCount;
   }

   public void setLikeCount(int likeCount) {
      this.likeCount = likeCount;
   }

   public int getCommentCount() {
      return commentCount;
   }

   public void setCommentCount(int commentCount) {
      this.commentCount = commentCount;
   }

   public int getRetweetCount() {
      return retweetCount;
   }

   public void setRetweetCount(int retweetCount) {
      this.retweetCount = retweetCount;
   }

   public String getPostedBy() {
      return postedBy;
   }

   public void setPostedBy(String postedBy) {
      this.postedBy = postedBy;
   }

   public long getPostedAt() {
      return postedAt;
   }

   public void setPostedAt(long postedAt) {
      this.postedAt = postedAt;
   }

   public String getPostId() {
      return postId;
   }

   public void setPostId(String postId) {
      this.postId = postId;
   }
}
