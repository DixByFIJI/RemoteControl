package com.example.username.remotecontrol.actions;

import android.util.Log;

import com.example.username.remotecontrol.connections.ConnectionTCP_IP;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Requests extends ConnectionTCP_IP {
    private String TAG = "Requests";

    private DataInputStream DATA_INPUT;
    private DataOutputStream DATA_OUTPUT;

    public void execute(String command){
        try {
            DATA_INPUT = new DataInputStream(INPUT_STREAM);
            DATA_OUTPUT = new DataOutputStream(OUTPUT_STREAM);

            DATA_OUTPUT.writeUTF(Hashing.sha256(command));
        } catch (IOException e) {
            Log.d(TAG, "Executing IOException");
        }
    }

//    private ResultSet DB_RESULTSET;
//
//    public String makeQuery(String key, String command){
//        String query = "INSERT INTO " + DB_TABLE + " (unique_key, command) VALUES ('" + Hashing.sha256(key) + "', '" + Hashing.sha256(command) + "')";
//        Log.d(TAG, query);
//        return query;
//    }
//
//    public ResultSet execute(String query){
//        try {
//            DB_RESULTSET = DB_STATEMENT.executeQuery(query);
//        } catch (SQLException e) {
//            Log.d(TAG, "Execute query exception", e);
//        }
//        return DB_RESULTSET;
//    }
}

