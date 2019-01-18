package com.example.username.remotecontrol.connections;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class ConnectorMySQL {
    private final String TAG = "connectionMySQL";

    private final String DB_LOGIN = "root";
    private final String DB_PASSWORD = "root";
    private final String DB_URL = "jdbc:mysql://mysql.zzz.com.ua:3306/";
    private final String DB_TIME_ZONE = "?serverTimezone=UTC&useSSL=false";
    protected final String DB_NAME = "remotecontrol";
    protected final String DB_TABLE = "commands";

    private Connection DB_CONNECTION;
    protected Statement DB_STATEMENT;

    public ConnectorMySQL() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.d(TAG, "Driver is already");
        } catch (Exception e) {
            Log.d(TAG, "JDBC driver exception", e);
        }
        try {
            DB_CONNECTION = DriverManager.getConnection(DB_URL + DB_NAME + DB_TIME_ZONE, DB_LOGIN, DB_PASSWORD);
            DB_STATEMENT = DB_CONNECTION.createStatement();
            DB_STATEMENT.execute("set character set utf8");
            DB_STATEMENT.execute("set names utf8");
            Log.d("connectionMySQL", "Connection is setup");
        } catch (SQLException e) {
            Log.d(TAG, "Connection to MySQL-database exception", e);
        }
    }
}

