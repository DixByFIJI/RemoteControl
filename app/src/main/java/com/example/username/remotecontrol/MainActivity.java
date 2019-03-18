package com.example.username.remotecontrol;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.username.remotecontrol.actions.Request;
import com.example.username.remotecontrol.connections.ClientSocketConnection;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "myLogs";

    private static final String COMMAND_SEARCH = "command";
    private static final String HOTWORD_SEARCH = "hotword";

    private static MediaRecorder MEDIA_RECORDER;
    private static MediaPlayer MEDIA_PLAYER;
    private static String FILE_NAME;
    private static final int REQUEST_PERMISSION_CODE = 1000;
    private static final int REQUEST_RECOGNITION_CODE = 200;

    private static boolean HOST_IS_CORRECT;

    private static String PACKAGE;
    private static DataSource dataSource;

    EditText txtIPAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListener();
        PACKAGE = getPackageName();
        //dataSource = new DataSource(this);
    }

    public void addListener(){

        final Button btnEnter = (Button)findViewById(R.id.btnEnter);
        txtIPAddress = (EditText)findViewById(R.id.txtIPAddress);

//        try {
//            InetAddress inet = InetAddress.getLocalHost();
//            InetAddress[] ips = InetAddress.getAllByName(inet.getCanonicalHostName());
//			if (ips  != null ) {
//				for (int i = 0; i < ips.length; i++) {
//					Log.d(TAG, ips[i].getHostName());
//				}
//			}
//		} catch (UnknownHostException e) {
//
//		}

        //Request Runtime Permission
        if(!checkPermissionFromDevice()) requestPermission();

        txtIPAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(txtIPAddress.getText().toString().matches("(\\d?\\d?\\d?\\.){3}(\\d?\\d?\\d?)")){
                    txtIPAddress.setTextColor(Color.parseColor("#689eb8"));
                    HOST_IS_CORRECT = true;
                } else {
                    txtIPAddress.setTextColor(Color.parseColor("#e51e2b"));
                    HOST_IS_CORRECT = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnEnter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(checkPermissionFromDevice()){
                        if(HOST_IS_CORRECT){
                            speechRecognition();
                        } else {
                            //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "Incorrect host", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Incorrect host");
                        }
//                        FILE_NAME = Environment.getExternalStorageDirectory().
//                                getAbsolutePath() + "/"
//                                + UUID.randomUUID().toString() + "_audio_record.3gp";
//
//                        setupMediaRecorder();
//
//                        try {
//                            MEDIA_RECORDER.prepare();
//                            MEDIA_RECORDER.start();
//                        } catch (Exception e){
//                            Log.d(TAG, "Recording start exception", e);
//                        }
                    } else {
                        requestPermission();
                    }
                } else if(event.getAction() == MotionEvent.ACTION_UP){
//                    try{
//                        if(MEDIA_RECORDER != null) {
//                            MEDIA_RECORDER.stop();
//
////                            REQUEST = new Requests(txtIPAddress.getText().toString(), Integer.parseInt(txtPort.getText().toString()));
////                            REQUEST.execute("Make me happy");
//                        }
//                    } catch (Exception e){
//                        Log.d(TAG, "Recording stop exception", e);
//                    }
                }
                return false;
            }
        });

//        btnPlay.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_DOWN){
//                    MEDIA_PLAYER = new MediaPlayer();
//                    try {
//                        MEDIA_PLAYER.setDataSource(FILE_NAME);
//                        MEDIA_PLAYER.prepare();
//                        MEDIA_PLAYER.start();
//                    } catch (Exception e){
//                        e.printStackTrace();
//                    }
//                } else if(event.getAction() == MotionEvent.ACTION_UP){
//                    if(MEDIA_PLAYER != null){
//                        MEDIA_PLAYER.stop();
//                        MEDIA_PLAYER.release();
//                        setupMediaRecorder();
//                    }
//                }
//                return false;
//            }
//        });
    }

    /**
     * Executes the speech recognition
     */

    private void speechRecognition(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("ru", ""));

        try{
            startActivityForResult(intent,REQUEST_RECOGNITION_CODE);
        } catch (ActivityNotFoundException ex){
            String appPackageName = this.getPackageName();
            Log.d(TAG, appPackageName);

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

            Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Intent problem", ex);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_RECOGNITION_CODE && resultCode == RESULT_OK && data != null){
            Thread process = new Thread(() -> {
                ClientSocketConnection client = new ClientSocketConnection(txtIPAddress.getText().toString());
                if(client.connect()){
                    ArrayList<String> arrOfResults = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String command = arrOfResults.get(0);

                    Request query = new Request(client.outputStream);
                    query.execute(command);
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect IP-Address", Toast.LENGTH_SHORT).show();
                }
            });

            process.start();
        }
    }

    /**
     * Setups parameters for MediaRecorder instance
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
                Manifest.permission.READ_EXTERNAL_STORAGE,
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
        int read_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED &&
               record_audio_result == PackageManager.PERMISSION_GRANTED &&
               read_external_storage_result == PackageManager.PERMISSION_GRANTED;
    }
}

