package com.credo;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class BlogPostID {
    @Exclude
    public String blogPostID;

    public <T extends BlogPostID> T withId(@NonNull final String id){
        this.blogPostID=id;
        return (T) this;
    }
}
