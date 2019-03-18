package com.example.username.remotecontrol.actions;

import java.io.OutputStream;
import java.io.PrintWriter;

public class Request {

    private OutputStream outputStream;

    public Request(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void execute(String command){
        PrintWriter printWriter = new PrintWriter(outputStream, true);
        printWriter.println(command);
        printWriter.close();
    }
}
