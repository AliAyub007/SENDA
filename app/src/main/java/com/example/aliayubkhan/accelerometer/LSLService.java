package com.example.aliayubkhan.accelerometer;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;



/**
 * Created by aliayubkhan on 19/04/2018.
 */

public class LSLService extends Service {

    private static final String TAG = "LSLService";


    Context context;
    TextView tv;


    //LSL Outlets
    static LSL.StreamOutlet accelerometerOutlet, lightOutlet, proximityOutlet, linearAccelerationOutlet, rotationOutlet, gravityOutlet, stepCountOutlet, audioOutlet = null;

    //LSL Streams
    private LSL.StreamInfo accelerometer, light, proximity, linearAcceleration, rotation, gravity, stepCount, audio = null;

    // the audio recording options
    private static final int RECORDING_RATE = 44100;
    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // the audio recorder
    private AudioRecord recorder;

    // the minimum buffer size needed for audio recording
    private static int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            RECORDING_RATE, CHANNEL, FORMAT);

    // are we currently sending audio data
    public static boolean currentlySendingAudio = false;


    public LSLService(){
        super();
    }

    @Override
    public void onCreate() {
        //Toast.makeText(this,"Service Created!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


            Log.i(TAG, "Service onStartCommand");
            Toast.makeText(this,"Starting LSL!", Toast.LENGTH_SHORT).show();


            //Creating new thread for my service
            //Always write your long running tasks in a separate thread, to avoid ANR
            new Thread(new Runnable() {
                @Override
                public void run() {
                    accelerometer = new LSL.StreamInfo("Accelerometer", "EEG", 3, 100, LSL.ChannelFormat.float32, "myuidaccelerometer");
                    light = new LSL.StreamInfo("Light", "EEG", 1, 100, LSL.ChannelFormat.float32, "myuidlight");
                    proximity = new LSL.StreamInfo("Proximity", "EEG", 1,100, LSL.ChannelFormat.float32, "myuidproximity");
                    linearAcceleration = new LSL.StreamInfo("LinearAcceleration", "EEG", 3,100, LSL.ChannelFormat.float32, "myuidlinearacceleration");
                    rotation = new LSL.StreamInfo("Rotation", "EEG", 4, 100, LSL.ChannelFormat.float32, "myuidrotation");
                    gravity = new LSL.StreamInfo("Gravity", "EEG", 3, 100, LSL.ChannelFormat.float32, "myuidgravity");
                    stepCount = new LSL.StreamInfo("StepCount", "EEG", 1, LSL.IRREGULAR_RATE, LSL.ChannelFormat.float32, "myuidstep");
                    audio = new LSL.StreamInfo("Audio", "audio", 1, 44100, LSL.ChannelFormat.double64, "myuid324457");

                    //showMessage("Creating an outlet...");
                    //showText("Creating an outlet...");
                    try {
                        accelerometerOutlet = new LSL.StreamOutlet(accelerometer);
                        lightOutlet = new LSL.StreamOutlet(light);
                        proximityOutlet = new LSL.StreamOutlet(proximity);
                        linearAccelerationOutlet = new LSL.StreamOutlet(linearAcceleration);
                        rotationOutlet = new LSL.StreamOutlet(rotation);
                        gravityOutlet = new LSL.StreamOutlet(gravity);
                        stepCountOutlet = new LSL.StreamOutlet(stepCount);
                        audioOutlet = new LSL.StreamOutlet(audio);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //For Audio
                    try {
                        byte[] buffer = new byte[BUFFER_SIZE];

                        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                RECORDING_RATE, CHANNEL, FORMAT, BUFFER_SIZE * 10);

                        recorder.startRecording();

                        while (currentlySendingAudio) {

                            // read the data into the buffer
                            readFully(buffer, 0, buffer.length);
//                            System.out.println(Arrays.toString(buffer));
                            audioOutlet.push_sample(buffer);
                        }

                        Log.d(TAG, "AudioRecord finished recording");
                    } catch (Exception e) {
                        Log.e(TAG, "Exception: " + e);
                    }

//            @SuppressLint("SimpleDateFormat") SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
//            String format;
//            double ts;
//            System.out.println(ts);

                    //showText("Sending data...");
                    float[] accelerometerData = new float[3];
                    float[] linearAccelerationData = new float[3];
                    float[] gravityData = new float[3];
                    float[] rotationData = new float[4];
                    float[] lightData = new float[1];
                    float[] proximityData = new float[1];
                    float[] stepCountData = new float[1];

                    while (!MainActivity.checkFlag) {
                        //Setting Accelerometer Data
                        accelerometerData[0] = MainActivity.ax;
                        accelerometerData[1] = MainActivity.ay;
                        accelerometerData[2] = MainActivity.az;


                        //Setting Light Data
                        lightData[0] =  MainActivity.lightInt;


                        //Setting Proximity Data
                        proximityData[0] = MainActivity.proximity;

                        //Setting Linear Acceleration Data
                        linearAccelerationData[0] = MainActivity.linear_x;
                        linearAccelerationData[1] = MainActivity.linear_y;
                        linearAccelerationData[2] = MainActivity.linear_z;


                        //Setting Rotation Data
                        rotationData[0] = MainActivity.rotVec_x;
                        rotationData[1] = MainActivity.rotVec_y;
                        rotationData[2] = MainActivity.rotVec_z;
                        rotationData[3] = MainActivity.rotVec_scalar;

                        //Setting Gravity Data
                        gravityData[0] = MainActivity.grav_x;
                        gravityData[1] = MainActivity.grav_y;
                        gravityData[2] = MainActivity.grav_z;

                        //Setting Step Data
                        stepCountData[0] = MainActivity.stepCounter;

//                format = s.format(new Date());
//                ts = Double.parseDouble(format);
//                System.out.println(ts);

                        assert accelerometerOutlet != null;
                        accelerometerOutlet.push_sample(accelerometerData);
                        lightOutlet.push_sample(lightData);
                        proximityOutlet.push_sample(proximityData);
                        linearAccelerationOutlet.push_sample(linearAccelerationData);
                        rotationOutlet.push_sample(rotationData);
                        gravityOutlet.push_sample(gravityData);
                        stepCountOutlet.push_sample(stepCountData);
//                try
//                    sleep(10);
//                } catch (InterruptedException e) {
//
//                    e.printStackTrace();
//                }
                    }

                    //Stop service once it finishes its task
                    stopSelf();
                }
            }).start();

        MainActivity.isRunning = true;
        return Service.START_STICKY;
    }


    private void readFully(byte[] data, int off, int length) {
        int read;
        while (length > 0) {
            read = recorder.read(data, off, length);
            length -= read;
            off += read;
        }
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
        recorder.stop();
        recorder.release();

        accelerometerOutlet.close();
        accelerometer.destroy();

        lightOutlet.close();
        light.destroy();

        proximityOutlet.close();
        proximity.destroy();

        linearAccelerationOutlet.close();
        linearAcceleration.destroy();

        rotationOutlet.close();
        rotation.destroy();

        gravityOutlet.close();
        gravity.destroy();

        stepCountOutlet.close();
        stepCount.destroy();

        audioOutlet.close();
        audio.destroy();

        currentlySendingAudio = false;

        if (null != recorder) {
            try{
                recorder.stop();
            }catch(RuntimeException ex){
                //Ignore
            }
        }

//        try {
//            mediaPlayer.setDataSource(OUTPUT_FILE);
//            mediaPlayer.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mediaPlayer.start();

//        recorder.stop();
//        recorder.reset();
//        recorder.release();
    }
}