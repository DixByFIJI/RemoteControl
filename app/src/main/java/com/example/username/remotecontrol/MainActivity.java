package com.example.username.remotecontrol;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.username.remotecontrol.actions.Requests;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "myLogs";

    private static Requests REQUEST;
    private static MediaRecorder MEDIA_RECORDER;
    private static MediaPlayer MEDIA_PLAYER;
    private static String FILE_NAME;
    private static final int REQUEST_PERMISSION_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListener();
    }

    public void addListener(){
        final Button btnEnter = (Button)findViewById(R.id.btnEnter);
        final Button btnPlay = (Button)findViewById(R.id.btnPlay);
        final TextView txtOutput = (TextView)findViewById(R.id.txtOutput);

        //Request Runtime Permission
        if(!checkPermissionFromDevice()) requestPermission();


        btnEnter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(checkPermissionFromDevice()){
                        FILE_NAME = Environment.getExternalStorageDirectory().
                                getAbsolutePath() + "/"
                                + UUID.randomUUID().toString() + "_audio_record.3gp";

                        setupMediaRecorder();

                        try {
                            MEDIA_RECORDER.prepare();
                            MEDIA_RECORDER.start();
                        } catch (Exception e){
                            Log.d(TAG, "Recording start exception", e);
                        }
                    } else {
                        requestPermission();
                    }
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    try{
                        if(MEDIA_RECORDER != null) {
                            MEDIA_RECORDER.stop();
                            REQUEST = new Requests();
                            REQUEST.execute(REQUEST.makeQuery("123", "Command"));
                        }
                    } catch (Exception e){
                        Log.d(TAG, "Recording stop exception", e);
                    }
                }
                return false;
            }
        });

        btnPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    MEDIA_PLAYER = new MediaPlayer();
                    try {
                        MEDIA_PLAYER.setDataSource(FILE_NAME);
                        MEDIA_PLAYER.prepare();
                        MEDIA_PLAYER.start();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                } else if(event.getAction() == MotionEvent.ACTION_UP){
                    if(MEDIA_PLAYER != null){
                        MEDIA_PLAYER.stop();
                        MEDIA_PLAYER.release();
                        setupMediaRecorder();
                    }
                }
                return false;
            }
        });
    }

    /**
     * Setups parameters for MediaRecorder
     */

    private static void setupMediaRecorder() {
        MEDIA_RECORDER = new MediaRecorder();
        MEDIA_RECORDER.setAudioSource(MediaRecorder.AudioSource.MIC);
        MEDIA_RECORDER.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        MEDIA_RECORDER.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        MEDIA_RECORDER.setOutputFile(FILE_NAME);
    }

    /**
     * Executes request for getting permission
     */

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        }, REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    /**
     * Checks getting permission from device
     *
     * @return {@code true} if permission has been received
     */

    private boolean checkPermissionFromDevice(){
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED;
    }
}

