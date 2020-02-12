package com.example.demo.Room;

import androidx.room.Database;

import com.example.demo.Model.Posts;

@Database(entities = {Posts.class}, version = 1, exportSchema = false)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    public abstract PostDao postDao();

}
