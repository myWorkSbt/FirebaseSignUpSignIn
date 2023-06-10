package com.example.fbtask;

import android.net.Uri;

public class postsdetail {

    private String user_name;
    private Uri image_url;
    private int nofl;

    public String getUser_name() {
        return user_name;
    }

    public Uri getImageurl() {
        return image_url;
    }

    public int getNofl() {
        return nofl;
    }

    public postsdetail(String user_name, Uri image_url, int nofl) {
        this.user_name = user_name;
        this.image_url = image_url;
        this.nofl = nofl;
    }
}
