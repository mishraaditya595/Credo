package com.credo;

import java.util.Date;

public class BlogPost {
    public String author, blog_description, blog_title, image_url, thumbnail_url;
    public Date timestamp;

    public BlogPost() { }

    public BlogPost(String author, String blog_description, String blog_title, String image_url, String thumbnail_url, Date timestamp) {
        this.author = author;
        this.blog_description = blog_description;
        this.blog_title = blog_title;
        this.image_url = image_url;
        this.thumbnail_url = thumbnail_url;
        this.timestamp=timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBlog_description() {
        return blog_description;
    }

    public void setBlog_description(String blog_description) {
        this.blog_description = blog_description;
    }

    public String getBlog_title() {
        return blog_title;
    }

    public void setBlog_title(String blog_title) {
        this.blog_title = blog_title;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
