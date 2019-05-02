
package com.example.username.remotecontrol.connections;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.username.remotecontrol.MainActivity;
import com.example.username.remotecontrol.exceptions.ClientConnectionException;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ClientSocketConnection {
    private final String TAG = "ClientSocketConnection";

    private int port = 49150;
    private String host;

    public Socket client;

    public InputStream inputStream;
    public OutputStream outputStream;

    public ClientSocketConnection(String host){
        this.host = host;
    }

    public ClientSocketConnection(String host, int port){
        this.host = host;
        this.port = port;
    }

    /**
     * Connects to remote server with help of Socket by IPAddress of host and TCP-Port and exetutes the request to remote server
     */

    public boolean connect(){
        Boolean isConnected = false;

        FutureTask<Boolean> task = new FutureTask(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    client = new Socket();
                    client.connect(new InetSocketAddress(host, port), 500);
                } catch (IOException ex) {
                    Log.d(TAG, "Client socket exception", ex);
                    return false;
                }
                return true;
            }
        });

        Thread flow = new Thread(task);
        flow.start();

        try {
            isConnected = task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


//        AsyncTask<Void, Void, Boolean> asyncProcess = new AsyncTask<Void, Void, Boolean>() {
//            @Override
//            protected Boolean doInBackground(Void... voids) {
//                try {
//                    client = new Socket();
//                    client.connect(new InetSocketAddress(host, port), 1000);
//                } catch (IOException ex) {
//                    Log.d(TAG, "Client socket exception", ex);
//                    return false;
//                }
//                return true;
//            }
//        };
//
//        asyncProcess.execute();
//
//        try {
//            isConnected = asyncProcess.get();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return isConnected;
    }

    public Socket getClientSocket(){
        return client;
    }

//    public OutputStream getOutputStream() {
//        return outputStream;
//    }
//
//    public InputStream getInputStream() {
//        return inputStream;
//    }
}

