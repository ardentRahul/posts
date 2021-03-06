package com.example.demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.Model.DemoData;
import com.example.demo.Model.Posts;
import com.example.demo.Network.GetPosts;
import com.example.demo.Network.RetrofitClient;
import com.example.demo.Room.DatabaseClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.demo.PaginationListener.PAGE_START;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private static final String TAG = "MainActivity";
    @BindView(R.id.temperatureRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.main_progress)
    ProgressBar progressBar;
    @BindView(R.id.floatingActionButton)
    FloatingActionButton floatingActionButton;

    private PostsRecyclerAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 4;
    private boolean isLoading = false;
    int itemCount = 0;
    List<Posts> posts1;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new PostsRecyclerAdapter(new ArrayList<Posts>(),this);
        mRecyclerView.setAdapter(adapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(MainActivity.this, v);
                popup.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) MainActivity.this);
                popup.inflate(R.menu.sort_posts);
                popup.show();
            }
        });

        if (CheckInternetConnection()){
            fetchPosts();
            mRecyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading = true;
                    currentPage++;
                    fetchPosts();
                }
                @Override
                public boolean isLastPage() {
                    return isLastPage;
                }
                @Override
                public boolean isLoading() {
                    return isLoading;
                }
            });
        }else{
            Toast.makeText(this, "No Internet...", Toast.LENGTH_SHORT).show();
            GetAppPosts getAppPosts = new GetAppPosts();
            getAppPosts.execute();
        }
    }




    private void fetchPosts(){
       new Handler().postDelayed(() -> {
           GetPosts getPosts = RetrofitClient.getRetrofit().create(GetPosts.class);
           Call<DemoData> call = getPosts.getPosts(currentPage);
           call.enqueue(new Callback<DemoData>() {
               @Override
               public void onResponse(Call<DemoData> call, Response<DemoData> response) {
                   if (response.code() == 200){
                       DemoData demoData = response.body();
                       progressBar.setVisibility(View.GONE);
                       posts1 = demoData.getPosts();
                       floatingActionButton.setVisibility(View.VISIBLE);
                       if (currentPage != PAGE_START) adapter.removeLoading();
                       adapter.addItems(posts1);
                       if (currentPage < totalPage) {
                           adapter.addLoading();
                       } else {
                           isLastPage = true;
                       }
                       isLoading = false;

                       SaveData saveData = new SaveData();
                       saveData.execute();

                   }
               }

               @Override
               public void onFailure(Call<DemoData> call, Throwable t) {

               }
           });
       },1500);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sortLikes:
                itemCount = 0;
                currentPage = PAGE_START;
                isLastPage = false;
                adapter.clear();

                Collections.sort(posts1, Posts.likesComparator);
                for (Posts posts : posts1){
                    adapter.addItems(Collections.singletonList(posts));
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.sortViews:
                itemCount = 0;
                currentPage = PAGE_START;
                isLastPage = false;
                adapter.clear();

                Collections.sort(posts1, Posts.viewsComparator);
                for (Posts posts : posts1){
                    adapter.addItems(Collections.singletonList(posts));
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.sortShares:
                itemCount = 0;
                currentPage = PAGE_START;
                isLastPage = false;
                adapter.clear();

                Collections.sort(posts1, Posts.sharesComparator);
                for (Posts posts : posts1){
                    adapter.addItems(Collections.singletonList(posts));
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.sortDate:
                itemCount = 0;
                currentPage = PAGE_START;
                isLastPage = false;
                adapter.clear();

                Collections.sort(posts1, Posts.dateComparator);
                for (Posts posts : posts1){
                    adapter.addItems(Collections.singletonList(posts));
                }
                adapter.notifyDataSetChanged();
                break;
            default:

        }
        return false;
    }

    public class GetAppPosts extends AsyncTask<Void,Void,List<Posts>>{

        List<Posts> postsList;
        @Override
        protected List<Posts> doInBackground(Void... voids) {
            postsList = DatabaseClient
                    .getInstance(getApplicationContext())
                    .getAppDatabase()
                    .postDao()
                    .getAllPosts();
            return postsList;
        }

        @Override
        protected void onPostExecute(List<Posts> posts) {
            super.onPostExecute(posts);
            progressBar.setVisibility(View.GONE);
            adapter.clear();
            adapter.addItems(postsList);
            if (postsList.isEmpty()){
                Toast.makeText(MainActivity.this, "There aren't any posts yet...", Toast.LENGTH_LONG).show();
            }
            else{
                adapter.notifyDataSetChanged();
            }
        }
    }

    private class SaveData extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            List<Posts> postsList = new ArrayList<>();
            int idd=1;
            for (int i=0;i<posts1.size();i++){
                postsList.addAll(posts1);
                postsList.get(i).setId(String.valueOf(idd));
                idd++;
            }

            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase()
                    .postDao()
                    .insert(posts1);
            return null;
        }
    }

    public boolean CheckInternetConnection()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                        .getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }
}
