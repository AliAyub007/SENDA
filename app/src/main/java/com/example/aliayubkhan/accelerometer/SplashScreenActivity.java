package com.example.aliayubkhan.accelerometer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by aliayubkhan on 15/09/2018.
 */


public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        /* SPLASH SCREEN TIMER */
        Thread timer = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }
                catch(InterruptedException e){
                    e.printStackTrace();
                }
                finally{

                    /* DISPLAY LOGIN SCREEN AFTER SPLASH SCREEN SHOWS FOR 3 SECONDS */
                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        timer.start();
    }
}