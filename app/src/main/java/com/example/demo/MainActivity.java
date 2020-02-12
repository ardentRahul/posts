package com.example.demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.Model.DemoData;
import com.example.demo.Model.Posts;
import com.example.demo.Network.GetPosts;
import com.example.demo.Network.RetrofitClient;
import com.example.demo.Room.DatabaseClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.demo.PaginationListener.PAGE_START;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.temperatureRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.like)
    Button btn;
    @BindView(R.id.view)
    Button btn1;
    @BindView(R.id.share)
    Button btn2;
    @BindView(R.id.dat)
    Button btn3;
    @BindView(R.id.main_progress)
    ProgressBar progressBar;

    private PostsRecyclerAdapter adapter;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int totalPage = 10;
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
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        adapter = new PostsRecyclerAdapter(new ArrayList<Posts>(),this);
        mRecyclerView.setAdapter(adapter);

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
            GetAppPosts getAppPosts = new GetAppPosts();
            getAppPosts.execute();
        }



        btn.setOnClickListener(v -> {
            itemCount = 0;
            currentPage = PAGE_START;
            isLastPage = false;
            adapter.clear();

            Collections.sort(posts1, Posts.likesComparator);
            for (Posts posts : posts1){
                adapter.addItems(Collections.singletonList(posts));
            }
            adapter.notifyDataSetChanged();
        });

        btn1.setOnClickListener(v -> {
            itemCount = 0;
            currentPage = PAGE_START;
            isLastPage = false;
            adapter.clear();

            Collections.sort(posts1, Posts.viewsComparator);
            for (Posts posts : posts1){
                adapter.addItems(Collections.singletonList(posts));
            }
            adapter.notifyDataSetChanged();
        });

        btn2.setOnClickListener(v -> {
            itemCount = 0;
            currentPage = PAGE_START;
            isLastPage = false;
            adapter.clear();

            Collections.sort(posts1, Posts.sharesComparator);
            for (Posts posts : posts1){
                adapter.addItems(Collections.singletonList(posts));
            }
            adapter.notifyDataSetChanged();
        });

        btn3.setOnClickListener(v -> {
            itemCount = 0;
            currentPage = PAGE_START;
            isLastPage = false;
            adapter.clear();

            Collections.sort(posts1, Posts.dateComparator);
            for (Posts posts : posts1){
                adapter.addItems(Collections.singletonList(posts));
            }
            adapter.notifyDataSetChanged();
        });
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
                       if (currentPage != PAGE_START) adapter.removeLoading();
                       adapter.addItems(posts1);
                       // check weather is last page or not
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
            adapter.clear();
            adapter.addItems(postsList);
            adapter.notifyDataSetChanged();
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

        //Means that we are connected to a network (mobile or wi-fi)
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                        .getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }
}
