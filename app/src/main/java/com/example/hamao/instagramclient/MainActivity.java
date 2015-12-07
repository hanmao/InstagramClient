package com.example.hamao.instagramclient;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private final String clientId = "e05c462ebd86446ea48a5af73769b602";
    private ArrayList<InstagramPhoto> photos;
    private InstagramPhotoAdapter aPhotos;
    private SwipeRefreshLayout swipeContainer;
    private  AsyncHttpClient client;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        photos = new ArrayList<>();
        aPhotos = new InstagramPhotoAdapter(this, photos);
        ListView lvPhotos = (ListView) findViewById(R.id.lvPhotos);
        lvPhotos.setAdapter(aPhotos);
        fetchPopularPhotos();
        swipeRefrest();




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void swipeRefrest(){
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchPopularPhotos();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }



    private void fetchPopularPhotos(){
        String url = "https://api.instagram.com/v1/media/popular?client_id=" + clientId;
        client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                aPhotos.clear();
                JSONArray photosJason = null;
                try{
                    photosJason = response.getJSONArray("data");
                    for(int i = 0; i < photosJason.length(); i++){
                        JSONObject photoJason = photosJason.getJSONObject(i);

                        InstagramPhoto photo = new InstagramPhoto();
                        photo.userName = photoJason.getJSONObject("user").getString("username");
                        photo.caption = photoJason.getJSONObject("caption").getString("text");
                        photo.imageUrl = photoJason.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
                        photo.iamgeHeight = photoJason.getJSONObject("images").getJSONObject("standard_resolution").getInt("height");
                        photo.likesCount = photoJason.getJSONObject("likes").getInt("count");
                        photo.commentsCount = photoJason.getJSONObject("comments").getInt("count");
                        photo.comments = new ArrayList<InstagramPhotoComment>();
                        photo.profileAvator = photoJason.getJSONObject("user").getString("profile_picture");
                        photo.createdTime = photoJason.getLong("created_time") * 1000;

                        JSONArray photoCommentsJSON = photoJason.getJSONObject("comments").getJSONArray("data");
                        for (int j = 0; j < photoCommentsJSON.length(); j++) {
                            JSONObject commentJSON = photoCommentsJSON.getJSONObject(j);
                            InstagramPhotoComment comment = new InstagramPhotoComment();
                            comment.text = commentJSON.getString("text");
                            comment.userName = commentJSON.getJSONObject("from").getString("username");
                            comment.profilePhotoUrl = commentJSON.getJSONObject("from").getString("profile_picture");
                            comment.createdTime = commentJSON.getLong("created_time") * 1000;
                            photo.comments.add(comment);
                        }

                        photos.add(photo);

                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
                aPhotos.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }


        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
