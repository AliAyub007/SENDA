package com.example.aliayubkhan.senda;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.aliayubkhan.senda.SettingsActivity.getSamplingRates;
import static com.example.aliayubkhan.senda.SettingsActivity.samplingRate_set_Check;

public class MainActivity extends Activity implements SensorEventListener
{
    @SuppressLint("StaticFieldLeak")
    static TextView tv;
    Button start, stop;

    static boolean isRunning  = false;
    static int i = 0;
    static boolean checkFlag = false;
    private SensorManager msensorManager;
    List<Sensor> sensor;
    List<String> SensorName = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView lv;
    public static ArrayList<String> selectedItems=new ArrayList<String>();

    //Sensor Names
    Sensor mAccelerometer, mLight, mProximity, mGravity, mLinearAcceleration, mRotation, mStepCounter;

    //Sensors Checklist

    static Boolean isAccelerometer = false;
    static Boolean isLight = false;
    static Boolean isProximity = false;
    static Boolean isGravity = false;
    static Boolean isLinearAcceleration = false;
    static Boolean isRotation = false;
    static Boolean isStepCounter = false;
    static Boolean isAudio = false;

    //Streaming Identification
    @SuppressLint("StaticFieldLeak")
    static ImageView streamingNowBtn;
    @SuppressLint("StaticFieldLeak")
    static TextView streamingNow;


    int backButtonCount = 0;

    //Settings button

    ImageView settings_button;

    //Requesting run-time permissions

    //Create placeholder for user's consent to record_audio permission.
    //This will be used in handling callback
    private final int MY_PERMISSIONS_RECORD_AUDIO = 1;

    public static boolean audioPermission = true;

    //

    public static List<Intent> POWERMANAGER_INTENTS = Arrays.asList(
            new Intent().setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
            new Intent().setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
            new Intent().setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
            new Intent().setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
            new Intent().setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
            new Intent().setComponent(new ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.entry.FunctionActivity")).setData(android.net.Uri.parse("mobilemanager://function/entry/AutoStart"))
    );

    //Initializing Sensor data Variables

    static float ax,ay,az;
    static {
        ax = 0;
        ay = 0;
        az = 0;
    }

    // Force of gravity  Data (TYPE_GRAVITY)!
    static float grav_x,grav_y,grav_z;
    static {
        grav_x = 0;
        grav_y = 0;
        grav_z = 0;
    }

    // Calibrated gyroscope Data (TYPE_GYROSCOPE)!
    static private Sensor mGyroscope;
    static float gyro_x,gyro_y,gyro_z;
    static {
        gyro_x = 0;
        gyro_y = 0;
        gyro_z = 0;
    }

    // Calibrated Rotation Vector Data (TYPE_ROTATION_VECTOR)!
    static float rotVec_x,rotVec_y,rotVec_z,rotVec_scalar;
    static {
        rotVec_x      = 0;
        rotVec_y      = 0;
        rotVec_z      = 0;
        rotVec_scalar = 0;
    }


    // Calibrated Step Counter Data (TYPE_STEP_COUNTER)!
    static float stepCounter;
    static {   stepCounter = 0;

    }

    static float lightInt;
    static {
        lightInt = 0;
    }

    static float orient_x,orient_y,orient_z;
    static {   orient_x = 0;
        orient_y = 0;
        orient_z = 0;
    }

    static float proximity;
    static {
        proximity=0;
    }

    static float linear_x, linear_y, linear_z;
    static {
        linear_x = 0;
        linear_y = 0;
        linear_z = 0;
    }

    static float georotVec_x,georotVec_y,georotVec_z;
    static {
        georotVec_x      = 0;
        georotVec_y      = 0;
        georotVec_z      = 0;
    }

    /** Called when the activity is first created. */
    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

//        tv = new TextView(this);
        setContentView(R.layout.activity_main);
        tv = (TextView)findViewById(R.id.tv);
        start = (Button)findViewById(R.id.startLSL);
        stop = (Button)findViewById(R.id.stopLSL);
        streamingNow = (TextView)findViewById(R.id.streamingNow);
        streamingNowBtn = (ImageView) findViewById(R.id.streamingNowBtn);
        settings_button = (ImageView) findViewById(R.id.settings_btn);
        settings_button.setVisibility(View.VISIBLE);

        requestAudioPermissions();
        startPowerSaverIntent(this);

        final Intent intent = new Intent(this, LSLService.class);


        start.setOnClickListener(new View.OnClickListener() {

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();

            @Override
            public void onClick(View v) {
                if(!isRunning){
                    if(!audioPermission){
                        requestAudioPermissions();
                    }
                    if(samplingRate_set_Check){
                        getSamplingRates();
                    }
                    startService(intent);
                }

            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(intent);
            }
        });

        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        tv.setText("Available Streams: ");


        msensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);

        //Setting All sensors

        assert msensorManager != null;
        mAccelerometer = msensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mLight = msensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProximity = msensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mGravity = msensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mLinearAcceleration = msensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mRotation = msensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            mStepCounter = msensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        }


        //Registering listeners for Sensors
        msensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);//SensorManager.SENSOR_DELAY_FASTEST);
        msensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        msensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
        msensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);
        msensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        msensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
        msensorManager.registerListener(this, mStepCounter, SensorManager.SENSOR_DELAY_UI);

        lv = (ListView) findViewById (R.id.sensors);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        sensor = msensorManager.getSensorList(Sensor.TYPE_ALL);

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_view_text, R.id.streamsSelected, SensorName);
        lv.setAdapter(adapter);

        SensorName.add("Accelerometer");
        SensorName.add("Light");
        SensorName.add("Proximity");
        SensorName.add("Gravity");
        SensorName.add("Linear Acceleration");
        SensorName.add("Rotation Vector");
        SensorName.add("Step Count");
        SensorName.add("Audio");


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // selected item
                String selectedItem = ((TextView) view).getText().toString();
                if(selectedItems.contains(selectedItem))
                    selectedItems.remove(selectedItem); //remove deselected item from the list of selected items
                else
                    selectedItems.add(selectedItem); //add selected item to the list of selected items

                getSelectedItems();
            }

        });
    }


    private void requestAudioPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            //When permission is not granted by user, show them message why this permission is needed.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to send Audio Stream", Toast.LENGTH_LONG).show();
                audioPermission = false;

                //Give user option to still opt-in the permissions
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);

            } else {
                // Show user dialog to grant permission to record audio
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_RECORD_AUDIO);
            }
        }
    }

    //Handling callback
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    audioPermission = true;
                    // permission was granted, yay!
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    audioPermission = false;
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        if(backButtonCount >= 1)
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            backButtonCount = 0;
        }
        else
        {
            Toast.makeText(this, "Press the back button once again to close the application.", Toast.LENGTH_SHORT).show();
            backButtonCount++;
        }
    }

    public static void startPowerSaverIntent(final Context context) {
        SharedPreferences settings = context.getSharedPreferences("ProtectedApps", Context.MODE_PRIVATE);
        boolean skipMessage = settings.getBoolean("skipProtectedAppCheck", false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            boolean foundCorrectIntent = false;
            for (final Intent intent : POWERMANAGER_INTENTS) {
                if (isCallable(context, intent)) {
                    foundCorrectIntent = true;
                    final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(context);
                    dontShowAgain.setText(R.string.dont_show_again);
                    dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            editor.putBoolean("skipProtectedAppCheck", isChecked);
                            editor.apply();
                        }
                    });

                    new AlertDialog.Builder(context)
                            .setTitle(Build.MANUFACTURER + " Protected Apps")
                            .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to function properly.%n", context.getString(R.string.app_name)))
                            .setView(dontShowAgain)
                            .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    context.startActivity(intent);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                    break;
                }
            }
            if (!foundCorrectIntent) {
                editor.putBoolean("skipProtectedAppCheck", true);
                editor.apply();
            }
        }
    }

    private static boolean isCallable(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void getSelectedItems() {
        for(String item:selectedItems){

            if(item.contains("Accelerometer")){
                isAccelerometer = true;
            }
            if(item.contains("Light")){
                isLight = true;
            }
            if(item.contains("Proximity")){
                isProximity = true;
            }
            if(item.contains("Gravity")){
                isGravity = true;
            }
            if(item.contains("Linear Acceleration")){
                isLinearAcceleration = true;
            }
            if(item.contains("Rotation Vector")){
                isRotation = true;
            }
            if(item.contains("Step Count")){
                isStepCounter = true;
            }
            if(item.contains("Audio")){
                isAudio = true;
            }

//            if(selItems=="")
//                selItems=item;
//            else
//                selItems+="/"+item;
        }
        //Toast.makeText(this, selItems, Toast.LENGTH_LONG).show();
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        if (sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];

            //System.out.print("\nAccelerometer = "+ ax + " /" + ay + " /" + az+ " /\n");
        } else if (sensor.getType() == Sensor.TYPE_LIGHT){
            lightInt= event.values[0];
            //System.out.print("Light = "+ lightInt + "\n");
        } else if (sensor.getType() == Sensor.TYPE_PROXIMITY){
            proximity = event.values[0];
            //System.out.print("Proximity = "+ proximity + "\n");
        } else if (sensor.getType() == Sensor.TYPE_GRAVITY){
            grav_x = event.values[0];
            grav_y = event.values[1];
            grav_z = event.values[2];
            //System.out.print("\nGravity = "+ grav_x + " /" + grav_y + " /" + grav_z+ " /\n");
        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            linear_x = event.values[0];
            linear_y = event.values[1];
            linear_z = event.values[2];
            //System.out.print("\nLinea Acceleration = "+ linear_x + " /" + linear_y + " /" + linear_z+ " /\n");
        } else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
            rotVec_x = event.values[0];
            rotVec_y = event.values[1];
            rotVec_z = event.values[2];
            rotVec_scalar = event.values[3];
            //System.out.print("\nRotation Vector = "+ rotVec_x + " /" + rotVec_y + " /" + rotVec_z + " /" +rotVec_scalar+ " /\n");
        } else if (sensor.getType() == Sensor.TYPE_SIGNIFICANT_MOTION){
            //nothing
        } else if (sensor.getType() == Sensor.TYPE_STEP_COUNTER){
            stepCounter = event.values[0];
            //System.out.print("Step Counter = "+ stepCounter + "\n");
        } else if (sensor.getType() == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR){
            georotVec_x = event.values[0];
            georotVec_y = event.values[1];
            georotVec_z = event.values[2];
            //System.out.print("\nGeomagnetic = "+ georotVec_x + " /" + georotVec_y + " /" + georotVec_z+ " /\n");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static void showText(String s){
        tv.setText(s);

    }
}

