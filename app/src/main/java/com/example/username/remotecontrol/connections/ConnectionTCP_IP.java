package com.example.username.remotecontrol.connections;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public abstract class ConnectionTCP_IP {
    private final String TAG = "ConnectionTCP_IP";

    private int PORT;
    private String HOST;

    private Socket CLIENT;

    private InputStream INPUT_STREAM;
    private OutputStream OUTPUT_STREAM;

    protected DataInputStream DATA_INPUT_STREAM;
    protected DataOutputStream DATA_OUTPUT_STREAM;

    public ConnectionTCP_IP(String host, int port){
        this.HOST = host;
        this.PORT = port;

        connect();
    }

    /**
     * Connects to remote server with the help of Socket by IPAddress of host and TCP-Port
     */

    private void connect() {
        Thread flow = new Thread(() -> {
            try{
//                CLIENT = new Socket(InetAddress.getByName(HOST), PORT);
                CLIENT = new Socket();
                CLIENT.connect(new InetSocketAddress(HOST, PORT));

                INPUT_STREAM = CLIENT.getInputStream();
                OUTPUT_STREAM = CLIENT.getOutputStream();

                DATA_OUTPUT_STREAM = new DataOutputStream(OUTPUT_STREAM);
                DATA_INPUT_STREAM = new DataInputStream(INPUT_STREAM);
            } catch (IOException e) {
                Log.d(TAG, "Client socket exception", e);
            }
        });

        try {
            flow.start();
            flow.join();
        } catch (InterruptedException e) {
            Log.d(TAG, "Thread running exception", e);
        }
    }

    protected abstract void execute(String command);

//    private final String DB_LOGIN = "root";
//    private final String DB_PASSWORD = "qwe123";
//    private final String DB_URL = "jdbc:mysql://localhost:3306/";
//    private final String DB_TIME_ZONE = "?useUnicode=true&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC\"";
//    protected final String DB_NAME = "remotecontrol";
//    protected final String DB_TABLE = "commands";
//
//    private Connection DB_CONNECTION;
//    protected Statement DB_STATEMENT;
//
//    public ConnectionTCP_IP() {
////        try {
////            //Class.forName("com.mysql.cj.jdbc.Driver");
////            Driver driver = new com.mysql.cj.jdbc.Driver();
////            DriverManager.deregisterDriver(driver);
////            Log.d(TAG, "Driver is already");
////        } catch (Exception e) {
////            Log.d(TAG, "JDBC driver exception", e);
////        }
//        try {
//            DB_CONNECTION = DriverManager.getConnection(DB_URL + DB_NAME + DB_TIME_ZONE, DB_LOGIN, DB_PASSWORD);
//            Log.d(TAG, "Connection is already");
//            DB_STATEMENT = DB_CONNECTION.createStatement();
//            DB_STATEMENT.execute("set character set utf8");
//            DB_STATEMENT.execute("set names utf8");
//        } catch (SQLException e) {
//            Log.d(TAG, "Connection to MySQL-database exception", e);
//        }
//    }
}

