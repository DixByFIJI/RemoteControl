package com.example.username.remotecontrol.connections;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionTCP_IP {
    private final String TAG = "ConnectionTCP_IP";

    private final int PORT = 3000;
    private final String HOST = "192.168.3.102";

    private Socket CLIENT;
    protected InputStream INPUT_STREAM;
    protected OutputStream OUTPUT_STREAM;

    public ConnectionTCP_IP(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    CLIENT = new Socket(InetAddress.getByName(HOST), PORT);

                    INPUT_STREAM = CLIENT.getInputStream();
                    OUTPUT_STREAM = CLIENT.getOutputStream();
                } catch (IOException e) {
                    Log.d(TAG, "Client socket exception", e);
                }
            }
        }).start();
    }

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

