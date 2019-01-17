package com.example.username.remotecontrol.actions;

import android.util.Log;

import com.example.username.remotecontrol.connections.ConnectorMySQL;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Requests extends ConnectorMySQL {
    private String TAG = "ExecuteQuery";

    private ResultSet DB_RESULTSET;

    public String makeQuery(String key, String command){
        String query = "INSERT INTO" + DB_NAME + "(unique_key, command) VALUES ('" + Hashing.sha256(key) + "', '" + Hashing.sha256(command) + "')";
        return query;
    }

    public ResultSet execute(String query){
        try {
            DB_RESULTSET = DB_STATEMENT.executeQuery(query);
        } catch (SQLException e) {
            Log.d(TAG, "Execute query exception", e);
        }
        return DB_RESULTSET;
    }
}

