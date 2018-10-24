package com.example.aliayubkhan.senda;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by aliayubkhan on 21/10/2018.
 */



public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    static EditText accelerometer_sampling_rate;
    @SuppressLint("StaticFieldLeak")
    static EditText light_sampling_rate;
    @SuppressLint("StaticFieldLeak")
    static EditText proximity_sampling_rate;
    @SuppressLint("StaticFieldLeak")
    static EditText gravity_sampling_rate;
    @SuppressLint("StaticFieldLeak")
    static EditText linear_acceleration_sampling_rate;
    @SuppressLint("StaticFieldLeak")
    static EditText rotation_vector_sampling_rate;
    @SuppressLint("StaticFieldLeak")
    static EditText step_count_sampling_rate;
    @SuppressLint("StaticFieldLeak")
    static EditText audio_sampling_rate;

    //Variables to store sampling rates

    public static int accelerometer_sampling_rate_data = 12;
    public static int light_sampling_rate_data = 12;
    public static int proximity_sampling_rate_data = 12;
    public static int gravity_sampling_rate_data = 12;
    public static int linear_acceleration_sampling_rate_data = 12;
    public static int rotation_vector_sampling_rate_data = 12;
    public static int step_count_sampling_rate_data = 12;
    public static int audio_sampling_rate_data = 8000;

    public static Boolean samplingRate_set_Check = Boolean.FALSE;

    //Button for setting values
    Button samplingSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        samplingRate_set_Check = Boolean.TRUE;
        samplingSet = (Button) findViewById(R.id.samplingSetBtn);

        samplingSet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getSamplingRates();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        accelerometer_sampling_rate = (EditText) findViewById(R.id.accelerometer_sampling_rate);
        light_sampling_rate = (EditText) findViewById(R.id.light_sampling_rate);
        proximity_sampling_rate = (EditText) findViewById(R.id.proximity_sampling_rate);
        gravity_sampling_rate = (EditText) findViewById(R.id.gravity_sampling_rate);
        linear_acceleration_sampling_rate = (EditText) findViewById(R.id.linear_acceleration_sampling_rate);
        rotation_vector_sampling_rate = (EditText) findViewById(R.id.rotation_vector_sampling_rate);
        step_count_sampling_rate = (EditText) findViewById(R.id.step_count_sampling_rate);
        audio_sampling_rate = (EditText) findViewById(R.id.audio_sampling_rate);


        accelerometer_sampling_rate.setText(String.valueOf(accelerometer_sampling_rate_data));
        light_sampling_rate.setText(String.valueOf(light_sampling_rate_data));
        proximity_sampling_rate.setText(String.valueOf(proximity_sampling_rate_data));
        gravity_sampling_rate.setText(String.valueOf(gravity_sampling_rate_data));
        linear_acceleration_sampling_rate.setText(String.valueOf(linear_acceleration_sampling_rate_data));
        rotation_vector_sampling_rate.setText(String.valueOf(rotation_vector_sampling_rate_data));
        step_count_sampling_rate.setText(String.valueOf(step_count_sampling_rate_data));
        audio_sampling_rate.setText(String.valueOf(audio_sampling_rate_data));
    }

    @Override
    public void onBackPressed() {
        getSamplingRates();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    static void getSamplingRates(){
        accelerometer_sampling_rate_data = Integer.parseInt(String.valueOf(accelerometer_sampling_rate.getText()));
        light_sampling_rate_data = Integer.parseInt(String.valueOf(light_sampling_rate.getText()));
        proximity_sampling_rate_data = Integer.parseInt(String.valueOf(proximity_sampling_rate.getText()));
        gravity_sampling_rate_data = Integer.parseInt(String.valueOf(gravity_sampling_rate.getText()));
        linear_acceleration_sampling_rate_data = Integer.parseInt(String.valueOf(linear_acceleration_sampling_rate.getText()));
        rotation_vector_sampling_rate_data = Integer.parseInt(String.valueOf(rotation_vector_sampling_rate.getText()));
        step_count_sampling_rate_data = Integer.parseInt(String.valueOf(step_count_sampling_rate.getText()));
        audio_sampling_rate_data = Integer.parseInt(String.valueOf(audio_sampling_rate.getText()));
    }
}
