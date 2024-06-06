package com.example.wallpaper_hd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    private ImageView scrollToTopButton;
    WallpaperAdapter wallpaperAdapter;
    List<WallpaperModel> wallpaperModelList;
    int pageNumber = 1;
     Boolean isScrollingUp = false;

    Boolean isScrolling =false;
    int currentItems,totalItems,scrollOutItems;
    String url = "https://api.pexels.com/v1/curated/?page=\"+pageNumber+\"&per_page=80";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF5A5353"))); // Your custom color for action bar
        }

        recyclerView = findViewById(R.id.recyclerView);
        scrollToTopButton = findViewById(R.id.scrollToTopButton);
        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter  = new WallpaperAdapter(this,wallpaperModelList);
        Window window = getWindow();
        // Set the status bar color
        window.setStatusBarColor(getResources().getColor(R.color.your_color));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);  // hide mobile key button


        recyclerView.setAdapter(wallpaperAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);

        scrollToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
                scrollToTopButton.setVisibility(View.GONE);
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState== AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling= true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if (isScrolling && (currentItems+scrollOutItems==totalItems)){
                    isScrolling= false;
                    fetchWallpaper();
                }
                if (dy < 0 && !isScrollingUp) {
                    // User scrolled up, show the button
                    scrollToTopButton.setVisibility(View.VISIBLE);
                    isScrollingUp = true;
                } else if (dy > 0 && isScrollingUp) {
                    // User scrolled down, hide the button
                    scrollToTopButton.setVisibility(View.GONE);
                    isScrollingUp = false;
                }

            }
        });

        fetchWallpaper();
    }

    public void fetchWallpaper(){

        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArray = jsonObject.getJSONArray("photos");
                            int Length = jsonArray.length();

                            for (int i=0;i<Length;i++){

                                JSONObject object = jsonArray.getJSONObject(i);

                                int id = object.getInt("id");

                                JSONObject objectImages = object.getJSONObject("src");
                                String orignalUrl = objectImages.getString("original");
                                String mediumUrl = objectImages.getString("medium");

                                WallpaperModel wallpaperModel = new WallpaperModel(id,orignalUrl,mediumUrl);
                                wallpaperModelList.add(wallpaperModel);

                            }
                            wallpaperAdapter.notifyDataSetChanged();
                            pageNumber++;

                        }catch (JSONException e){

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("Authorization","oZVfopZSKUVvS0tlvkoX6NxIBRs10t0p28T8SIH0Cksgz3klrUhFvh8c");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==R.id.nav_search){
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            final EditText editText = new EditText(this);
            editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            alert.setMessage("Enter Category e.g, nature");
            alert.setTitle("Search Wallpaper");
            alert.setView(editText);

            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String query = editText.getText().toString().toLowerCase();
                    url ="https://api.pexels.com/v1/search/?page="+pageNumber+"&per_page=80&query="+query;
                    wallpaperModelList.clear();
                    fetchWallpaper();
                }
            });

            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }
}