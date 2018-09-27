package com.example.aliayubkhan.senda;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;

import static com.example.aliayubkhan.senda.MainActivity.isAccelerometer;
import static com.example.aliayubkhan.senda.MainActivity.isAudio;
import static com.example.aliayubkhan.senda.MainActivity.isGravity;
import static com.example.aliayubkhan.senda.MainActivity.isLight;
import static com.example.aliayubkhan.senda.MainActivity.isLinearAcceleration;
import static com.example.aliayubkhan.senda.MainActivity.isProximity;
import static com.example.aliayubkhan.senda.MainActivity.isRotation;
import static com.example.aliayubkhan.senda.MainActivity.isStepCounter;
import static com.example.aliayubkhan.senda.MainActivity.streamingNow;
import static com.example.aliayubkhan.senda.MainActivity.streamingNowBtn;


/**
 * Created by aliayubkhan on 19/04/2018.
 */

public class LSLService extends Service {

    private static final String TAG = "LSLService";

    //LSL Outlets
    static LSL.StreamOutlet accelerometerOutlet, lightOutlet, proximityOutlet, linearAccelerationOutlet, rotationOutlet, gravityOutlet, stepCountOutlet, audioOutlet = null;

    //LSL Streams
    private LSL.StreamInfo accelerometer, light, proximity, linearAcceleration, rotation, gravity, stepCount, audio = null;

    // the audio recording options
    private static final int RECORDING_RATE = 8000;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // the audio recorder
    private AudioRecord recorder = null;

    // the minimum buffer size needed for audio recording
    private static int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            RECORDING_RATE, CHANNEL, FORMAT);

    short[] buffer = new short[BUFFER_SIZE];

    // are we currently sending audio data
    public static boolean currentlySendingAudio = false;

    public LSLService(){
        super();
    }

    String uniqueID = Build.FINGERPRINT;
    String deviceName = Build.MODEL;

    // Data Variables
    float[] accelerometerData = new float[3];
    float[] linearAccelerationData = new float[3];
    float[] gravityData = new float[3];
    float[] rotationData = new float[4];
    float[] lightData = new float[1];
    float[] proximityData = new float[1];
    float[] stepCountData = new float[1];

    //Wake Lock
    PowerManager.WakeLock wakelock;

    //Animation for Streaming
    Animation animation = new AlphaAnimation((float) 0.5, 0);

    @SuppressLint("WakelockTimeout")
    @Override
    public void onCreate() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        wakelock= pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
        wakelock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


            streamingNow.setVisibility(View.VISIBLE);
            streamingNowBtn.setVisibility(View.VISIBLE);

            animation.setDuration(850);
            animation.setInterpolator(new LinearInterpolator()); // do not alter
            // animation rate
            animation.setRepeatCount(Animation.INFINITE); // Repeat animation
            // infinitely
            animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
            // end so the button will fade back in
            streamingNowBtn.startAnimation(animation);
            streamingNow.startAnimation(animation);

            Log.i(TAG, "Service onStartCommand");
            Toast.makeText(this,"Starting LSL!", Toast.LENGTH_SHORT).show();

            //Creating new thread for my service
            //Always write your long running tasks in a separate thread, to avoid ANR
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if(isAccelerometer){
                        accelerometer = new LSL.StreamInfo("Accelerometer "+deviceName, "marker", 3, 100, LSL.ChannelFormat.float32, "myuidaccelerometer"+uniqueID);
                        try {
                            accelerometerOutlet = new LSL.StreamOutlet(accelerometer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(isLight){
                        light = new LSL.StreamInfo("Light "+deviceName, "marker", 1, 100, LSL.ChannelFormat.float32, "myuidlight"+uniqueID);
                        try {
                            lightOutlet = new LSL.StreamOutlet(light);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(isProximity){
                        proximity = new LSL.StreamInfo("Proximity "+deviceName, "marker", 1,100, LSL.ChannelFormat.float32, "myuidproximity"+uniqueID);
                        try {
                            proximityOutlet = new LSL.StreamOutlet(proximity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(isLinearAcceleration){
                        linearAcceleration = new LSL.StreamInfo("LinearAcceleration "+deviceName, "marker", 3,100, LSL.ChannelFormat.float32, "myuidlinearacceleration"+uniqueID);
                        try {
                            linearAccelerationOutlet = new LSL.StreamOutlet(linearAcceleration);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    if(isRotation){
                        rotation = new LSL.StreamInfo("Rotation "+deviceName, "marker", 4, 100, LSL.ChannelFormat.float32, "myuidrotation"+uniqueID);
                        try {
                            rotationOutlet = new LSL.StreamOutlet(rotation);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    if(isGravity){
                        gravity = new LSL.StreamInfo("Gravity "+deviceName, "marker", 3, 100, LSL.ChannelFormat.float32, "myuidgravity"+uniqueID);
                        try {
                            gravityOutlet = new LSL.StreamOutlet(gravity);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    if(isStepCounter){
                        stepCount = new LSL.StreamInfo("StepCount "+deviceName, "marker", 1, LSL.IRREGULAR_RATE, LSL.ChannelFormat.float32, "myuidstep"+uniqueID);
                        try {
                            stepCountOutlet = new LSL.StreamOutlet(stepCount);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(isAudio){
                        audio = new LSL.StreamInfo("Audio "+deviceName, "audio", 1, 8000, LSL.ChannelFormat.float32, "myuidaudio"+uniqueID);
                        try {
                            audioOutlet = new LSL.StreamOutlet(audio);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //For Audio
                        try {
                            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                    RECORDING_RATE, CHANNEL, FORMAT, BUFFER_SIZE * 10);

                            recorder.startRecording();

                            Log.d(TAG, "AudioRecord finished recording");
                        } catch (Exception e) {
                            Log.e(TAG, "Exception: " + e);
                        }
                    }

                    while (!MainActivity.checkFlag) {

                        System.out.println(Arrays.toString(accelerometerData));

                        if(isAccelerometer){
                            //Setting Accelerometer Data
                            accelerometerData[0] = MainActivity.ax;
                            accelerometerData[1] = MainActivity.ay;
                            accelerometerData[2] = MainActivity.az;
                            accelerometerOutlet.push_sample(accelerometerData);
                        }

                        if(isLight){
                            //Setting Light Data
                            lightData[0] =  MainActivity.lightInt;
                            lightOutlet.push_sample(lightData);
                        }

                        if(isProximity){
                            //Setting Proximity Data
                            proximityData[0] = MainActivity.proximity;
                            proximityOutlet.push_sample(proximityData);
                        }

                        if(isLinearAcceleration){
                            //Setting Linear Acceleration Data
                            linearAccelerationData[0] = MainActivity.linear_x;
                            linearAccelerationData[1] = MainActivity.linear_y;
                            linearAccelerationData[2] = MainActivity.linear_z;
                            linearAccelerationOutlet.push_sample(linearAccelerationData);
                        }

                        if(isRotation){
                            //Setting Rotation Data
                            rotationData[0] = MainActivity.rotVec_x;
                            rotationData[1] = MainActivity.rotVec_y;
                            rotationData[2] = MainActivity.rotVec_z;
                            rotationData[3] = MainActivity.rotVec_scalar;
                            rotationOutlet.push_sample(rotationData);
                        }

                        if(isGravity){
                            //Setting Gravity Data
                            gravityData[0] = MainActivity.grav_x;
                            gravityData[1] = MainActivity.grav_y;
                            gravityData[2] = MainActivity.grav_z;
                            gravityOutlet.push_sample(gravityData);
                        }

                        if(isStepCounter){
                            //Setting Step Data
                            stepCountData[0] = MainActivity.stepCounter;
                            stepCountOutlet.push_sample(stepCountData);
                        }

                        if(isAudio){
                            recorder.read(buffer, 0, buffer.length);
                            float[] pcmAsFloats = floatMe(buffer);
                            audioOutlet.push_chunk(pcmAsFloats);
                        }


                    }

                    //Stop service once it finishes its task
                    stopSelf();
                }
            }).start();

        MainActivity.isRunning = true;
        return START_STICKY;
    }

    public static float[] floatMe(short[] pcms) {
        float[] floaters = new float[pcms.length];
        for (int i = 0; i < pcms.length; i++) {
            floaters[i] = pcms[i];
        }
        return floaters;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        MainActivity.isRunning = false;

        Log.i(TAG, "Service onDestroy");
        Toast.makeText(this,"Closing LSL!", Toast.LENGTH_SHORT).show();
        MainActivity.stepCounter = 0;

        streamingNow.setVisibility(View.INVISIBLE);
        streamingNowBtn.setVisibility(View.INVISIBLE);
        streamingNowBtn.clearAnimation();
        streamingNow.clearAnimation();
        wakelock.release();

        if(isAccelerometer){
            accelerometerOutlet.close();
            accelerometer.destroy();
        }

        if(isLight){
            lightOutlet.close();
            light.destroy();

        }

        if(isProximity){
            proximityOutlet.close();
            proximity.destroy();
        }

        if(isLinearAcceleration){
            linearAccelerationOutlet.close();
            linearAcceleration.destroy();
        }

        if(isRotation){
            rotationOutlet.close();
            rotation.destroy();
        }

        if(isGravity){
            gravityOutlet.close();
            gravity.destroy();
        }

        if(isStepCounter){
            stepCountOutlet.close();
            stepCount.destroy();
        }

        if(isAudio){
            audioOutlet.close();
            audio.destroy();

            if (null != recorder) {
                try{
                    recorder.stop();
                }catch(RuntimeException ex){
                    recorder.release();
                }
            }
        }
    }
}