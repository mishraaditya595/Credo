package com.credo;

public class BlogPost {
    public String author, blogDescription, blogTitle, imageURL, thumbnailURL;

    public BlogPost() { }

    public BlogPost(String author, String blogDescription, String blogTitle, String imageURL, String thumbnailURL, String timestamp) {
        this.author = author;
        this.blogDescription = blogDescription;
        this.blogTitle = blogTitle;
        this.imageURL = imageURL;
        this.thumbnailURL = thumbnailURL;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBlogDescription() {
        return blogDescription;
    }

    public void setBlogDescription(String blogDescription) {
        this.blogDescription = blogDescription;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

}
