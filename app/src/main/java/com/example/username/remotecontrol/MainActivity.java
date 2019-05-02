package com.example.username.remotecontrol;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.username.remotecontrol.actions.Request;
import com.example.username.remotecontrol.connections.ClientSocketConnection;
import com.example.username.remotecontrol.connections.NetworkServices;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

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

    EditText txtIPAddress;
    ImageButton btnRecord;

    private static Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = getApplicationContext();
        addListener();
        PACKAGE = getPackageName();
    }

    public void addListener(){
        Log.d(TAG, getPackageName());
        btnRecord = (ImageButton)findViewById(R.id.btnRecord);
        txtIPAddress = (EditText)findViewById(R.id.txtIPAddress);

        AsyncTask<Void, Void, List<String>> scanningTask = new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                NetworkServices services = new NetworkServices(getApplicationContext());
                return services.discoveryServices();
            }

            @Override
            protected void onPostExecute(List<String> devices) {
                for (String device : devices) {
                    Log.d(TAG, device);
                }
            }
        };

        scanningTask.execute();

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

        btnRecord.setOnTouchListener(new View.OnTouchListener() {
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

    private void speechRecognition(){
        client = new ClientSocketConnection(txtIPAddress.getText().toString());
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
                                message = query.execute(command);
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

    public static Context getMainContext(){
        return mainContext;
    }

//    private static class NetworkSniffTask extends AsyncTask<Void, Void, Void> {
//        private static final String TAG = "NetworkSniffTask";
//        private Context localContext;
//
//        private NetworkSniffTask(Context context) {
//            localContext = context;
//        }
//
//        @Override
//        protected Void doInBackground(Void ... voids) {
//            Log.e(TAG, "Let's sniff the network");
//            try {
//                Context context = localContext;
//                if (context != null) {
//                    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//                    WifiInfo connectionInfo = wm.getConnectionInfo();
//                    int ipAddress = connectionInfo.getIpAddress();
//                    String ipString = Formatter.formatIpAddress(ipAddress);
//                    Log.e(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
//                    Log.e(TAG, "ipString: " + String.valueOf(ipString));
//                    String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
//                    Log.e(TAG, "prefix: " + prefix);
//                    for (int i = 0; i < 255; i++) {
//                        String testIp = prefix + String.valueOf(i);
//                        InetAddress name = InetAddress.getByName(testIp);
//                        String hostName = name.getCanonicalHostName();
//                        if (name.isReachable(1000))
//                            Log.e(TAG, "Host:" + hostName);
//                    }
//                }
//            } catch (Throwable t) {
//                Log.e(TAG, "Well that's not good.", t);
//            }
//            return null;
//        }
//    }
}

