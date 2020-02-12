package com.example.demo.Room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.demo.Model.Posts;

import java.util.List;

@Dao
public interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Posts> posts);

    @Delete
    void delete(Posts posts);

    @Query("SELECT * FROM posts")
    List<Posts> getAllPosts();
}
