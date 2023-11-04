package com.example.thoughts.Model;

import java.util.ArrayList;

public class StoryModel {

    private String storyBy;
    private long storyAt;

    ArrayList<UserStories> stories;

    public StoryModel() {
    }

    public String getStoryBy() {
        return storyBy;
    }

    public ArrayList<UserStories> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStories> stories) {
        this.stories = stories;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}

