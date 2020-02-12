package com.example.demo.Room;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private Context mCtx;
    private static DatabaseClient mInstance;
    private RoomDatabase appDatabase;


    private DatabaseClient(Context mCtx)
    {
        this.mCtx = mCtx;

        //creating the app database with Room database builder
        //MyToDos is the name of the database
        appDatabase = Room.databaseBuilder(mCtx,RoomDatabase.class,"PostsWall").build();
    }

    public static synchronized DatabaseClient getInstance(Context mCtx)
    {
        if (mInstance == null)
        {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public RoomDatabase getAppDatabase()
    {
        return appDatabase;
    }
}
