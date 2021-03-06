package com.example.username.remotecontrol;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.username.remotecontrol.actions.Request;
import com.example.username.remotecontrol.connections.ClientSocketConnection;
import com.example.username.remotecontrol.connections.NetworkServiceManager;
import com.example.username.remotecontrol.connections.DiscoveryServiceListener;
import com.example.username.remotecontrol.custom_views.DeviceAdapter;
import com.example.username.remotecontrol.entities.NetworkDevice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.jmdns.ServiceInfo;

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

    private static volatile ClientSocketConnection client;

    private static volatile List<ServiceInfo> services;

    private static volatile NetworkServiceManager serviceManager;

    ImageButton btnRecord;
    Spinner spnIPAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addListener();
    }

    public void addListener(){
        btnRecord = (ImageButton)findViewById(R.id.btnRecord);
        spnIPAddress = (Spinner) findViewById(R.id.spnIPAddress);

        AsyncTask<Void, NetworkDevice, Void> scanningTask = new AsyncTask<Void, NetworkDevice, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                serviceManager = new NetworkServiceManager(getApplicationContext());
                serviceManager.discoveryServices(new DiscoveryServiceListener() {
                    @Override
                    public void onFound(ServiceInfo serviceInfo) {
                        services = serviceManager.getCurrentServices();
                        services.removeIf(service -> service.getPropertyString(NetworkServiceManager.serviceInfoTags.TYPE_TAG.toString())
                                .equals(NetworkServiceManager.deviceTypes.PHONE.toString()));

                        NetworkDevice[] devices = services.stream().map(service -> {
                            String deviceType = service.getPropertyString(NetworkServiceManager.serviceInfoTags.TYPE_TAG.toString());
                            String deviceName = service.getPropertyString(NetworkServiceManager.serviceInfoTags.DEVICE_NAME_TAG.toString());
                            String hostName = service.getInetAddresses()[0].getHostAddress();
                            return new NetworkDevice()
                                    .setType(deviceType)
                                    .setName(deviceName)
                                    .setIp(hostName);
                        }).toArray(NetworkDevice[]::new);

                        publishProgress(devices);
                    }

                    @Override
                    public void onRemoved(ServiceInfo serviceInfo) {
                        services = serviceManager.getCurrentServices();
                        services.removeIf(service -> service.getPropertyString(NetworkServiceManager.serviceInfoTags.TYPE_TAG.toString())
                                .equals(NetworkServiceManager.deviceTypes.PHONE.toString()));

                        NetworkDevice[] devices = services.stream().map(service -> {
                            String deviceType = service.getPropertyString(NetworkServiceManager.serviceInfoTags.TYPE_TAG.toString());
                            String deviceName = service.getPropertyString(NetworkServiceManager.serviceInfoTags.DEVICE_NAME_TAG.toString());
                            String hostName = service.getInetAddresses()[0].getHostAddress();
                            return new NetworkDevice()
                                    .setType(deviceType)
                                    .setName(deviceName)
                                    .setIp(hostName);
                        }).toArray(NetworkDevice[]::new);

                        publishProgress(devices);
                    }
                });
                return null;
            }

            @Override
            protected void onProgressUpdate(NetworkDevice ... values) {
                DeviceAdapter adapter = new DeviceAdapter(getApplicationContext(), Arrays.asList(values));
                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spnIPAddress.setAdapter(adapter);
            }
        };

        scanningTask.execute();

        //Request Runtime Permission
        if(!checkPermissionFromDevice()) requestPermission();

        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if(checkPermissionFromDevice()){
                        NetworkDevice selectedDevice = ((NetworkDevice) spnIPAddress.getSelectedItem());
                        if(selectedDevice != null) {
                            speechRecognition(selectedDevice);
                        } else {
                            Toast.makeText(getApplicationContext(), "No device selected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        requestPermission();
                    }
                } else if(event.getAction() == MotionEvent.ACTION_UP){

                }
                return false;
            }
        });
    }

    /**
     * Executes the speech recognition
     */

    private void speechRecognition(NetworkDevice device){
        client = new ClientSocketConnection(device.getIp());
        AsyncTask<Void, Void, Boolean> connectionTask = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return client.connect();
            }

            @Override
            protected void onPostExecute(Boolean isConnected) {
                if(isConnected){
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("ru", ""));
                    SpeechRecognizer recognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
                    recognizer.setRecognitionListener(new RecognitionListener() {
                        @Override
                        public void onReadyForSpeech(Bundle params) {

                        }

                        @Override
                        public void onBeginningOfSpeech() {
                            btnRecord.setImageResource(R.drawable.wave);
                        }

                        @Override
                        public void onRmsChanged(float rmsdB) {

                        }

                        @Override
                        public void onBufferReceived(byte[] buffer) {

                        }

                        @Override
                        public void onEndOfSpeech() {
                            btnRecord.setImageResource(R.drawable.microphone);
                        }

                        @Override
                        public void onError(int error) {

                        }

                        @Override
                        public void onPartialResults(Bundle partialResults) {

                        }

                        @Override
                        public void onEvent(int eventType, Bundle params) {}

                        @Override
                        public void onResults(Bundle results) {
                            String result = null;
                            ArrayList<String> arrOfResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                            result = String.join(" ", arrOfResults);

                            String command = arrOfResults.get(0);

                            Request query = new Request(client.getClientSocket());
                            String message = null;
                            try {
                                message = query.execute(device, command);
                            } catch (IOException e) {
                                message = "Execution interrupted...";
                                Log.d(TAG, message, e);
                            }

                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });

                    recognizer.startListening(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect IP-Address", Toast.LENGTH_SHORT).show();
                }
            }
        };

        connectionTask.execute();
    }

    /**
     * Executes a request for getting permission
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
     * Checks a getting permission from device
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceManager != null) {
            serviceManager.unregisterServices();
        }
    }
}

