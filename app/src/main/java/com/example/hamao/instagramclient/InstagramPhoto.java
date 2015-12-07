package com.example.hamao.instagramclient;

import java.util.ArrayList;

/**
 * Created by hamao on 12/6/15.
 */
public class InstagramPhoto {
    public String userName;
    public String caption;
    public String imageUrl;
    public int iamgeHeight;
    public int likesCount;
    public int commentsCount;
    public long createdTime;
    public String profileAvator;
    public ArrayList<InstagramPhotoComment> comments;
}
