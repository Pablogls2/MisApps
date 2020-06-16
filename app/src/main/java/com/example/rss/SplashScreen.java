package com.example.rss;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class SplashScreen extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //hilo para que espere dudrante dos segundos y muestre el logo y el nombre de la app
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent in= new Intent(SplashScreen.this,MainActivity.class);
                startActivity(in);
            }
        },2000);
    }
}
