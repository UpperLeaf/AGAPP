package com.wonsang.agapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class AnimationActivity extends AppCompatActivity {

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CAMERA
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anim_main);
        ImageView imageView = findViewById(R.id.start_anim_image);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.start_anim);
        imageView.startAnimation(animation);


        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent;

            if(!hasPermissions()){
                intent = new Intent(this, PermissionActivity.class);
            }
            else
                intent = new Intent(this, MainActivity.class);

            startActivity(intent);
        }, 3000);
    }

    private boolean hasPermissions() {
        for(String permission : PERMISSIONS) {
            if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }
}
