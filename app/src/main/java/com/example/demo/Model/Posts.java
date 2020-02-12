package com.example.demo.Model;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Comparator;

@Entity(tableName = "posts")
public class Posts {

    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("thumbnail_image")
    @ColumnInfo(name = "image")
    private String thumbnail_image;

    @SerializedName("event_name")
    @ColumnInfo(name = "name")
    private String event_name;

    @SerializedName("event_date")
    @ColumnInfo(name = "date")
    private int Event_Date;

    @SerializedName("views")
    @ColumnInfo(name = "views")
    private int views;

    @SerializedName("likes")
    @ColumnInfo(name = "likes")
    private int likes;

    @SerializedName("shares")
    @ColumnInfo(name = "shares")
    private int shares;

    public Posts(String id, String thumbnail_Image, String event_Name, int event_Date, int views, int likes, int shares) {
        this.id = id;
        thumbnail_image = thumbnail_Image;
        event_name = event_Name;
        Event_Date = event_Date;
        this.views = views;
        this.likes = likes;
        this.shares = shares;
    }

    public Posts() {

    }

    public static void remove() {
    }

    public String getId() {
        return id;
    }

    public String getThumbnail_image() {
        return thumbnail_image;
    }

    public String getEvent_name() {
        return event_name;
    }

    public int getEvent_Date() {
        return Event_Date;
    }

    public int getViews() {
        return views;
    }

    public int getLikes() {
        return likes;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setThumbnail_image(String thumbnail_image) {
        this.thumbnail_image = thumbnail_image;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public void setEvent_Date(int event_Date) {
        Event_Date = event_Date;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setShares(int shares) {
        this.shares = shares;
    }

    public int getShares() {
        return shares;
    }

    public static Comparator<Posts> likesComparator = (l1, l2) ->
            (l1.getLikes() < l2.getLikes() ? -1 :
                    (l2.getLikes() == l1.getLikes() ? 0:1));

    public static Comparator<Posts> viewsComparator = (l1, l2) ->
            (l1.getViews() < l2.getViews() ? -1 :
                    (l2.getViews() == l1.getViews() ? 0:1));

    public static Comparator<Posts> sharesComparator = (l1, l2) ->
            (l1.getShares() < l2.getShares() ? -1 :
                    (l2.getShares() == l1.getShares() ? 0:1));

    public static Comparator<Posts> dateComparator = (l1, l2) ->
            (l1.getEvent_Date() < l2.getEvent_Date() ? -1 :
                    (l2.getEvent_Date() == l1.getEvent_Date() ? 0:1));
}
