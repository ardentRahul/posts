package com.example.demo.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DemoData {

    @SerializedName("page")
    private int page;
    @SerializedName("posts")
    private ArrayList<Posts> posts;

    public DemoData(int page, ArrayList<Posts> posts) {
        this.page = page;
        this.posts = posts;
    }

    public int getPage() {
        return page;
    }

    public List<Posts> getPosts() {
        return posts;
    }


}
