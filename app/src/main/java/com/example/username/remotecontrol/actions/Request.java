package com.example.username.remotecontrol.actions;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Request {

    private final String TAG = "Requests";

    private Socket socket;

    public Request(Socket socket) {
        this.socket = socket;
    }

    public String execute(String command) throws IOException {
        String message = null;
        FutureTask<String> task = new FutureTask(new Callable<String>() {
            @Override
            public String call() {
                String callback = null;
                try (
                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();
                    PrintWriter writerStream = new PrintWriter(outputStream, true);
                    BufferedReader readerStream = new BufferedReader(new InputStreamReader(inputStream, "cp1251"));
                ) {
                    writerStream.println(command);
                    callback = readerStream.readLine();
                } catch (IOException e) {
                    callback = "Request was interrupted...";
                    e.printStackTrace();
                }
                return callback;
            }
        });

        Thread flow = new Thread(task);
        flow.start();

        try {
            message = task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return message;
    }
}
