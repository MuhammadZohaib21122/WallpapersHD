package com.example.wallpaper_hd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class FullScreenWallpaper extends AppCompatActivity {

    String originalUrl="";
    PhotoView photoView;
    private Button backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FullScreenWallpaper.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//  Remove Status bar

//        Window window = getWindow();
//        // Set the status bar color
//        window.setStatusBarColor(getResources().getColor(R.color.your_color));

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);  // hide mobile key button

//        getSupportActionBar().hide();

        Intent intent = getIntent();
        originalUrl = intent.getStringExtra("originalUrl");
        photoView = findViewById(R.id.photoView);

        Glide.with(this).load(originalUrl).into(photoView);
    }
}