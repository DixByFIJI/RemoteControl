package com.example.username.remotecontrol.actions;

import android.util.Log;

import java.math.BigInteger;
import java.security.MessageDigest;

public abstract class Hashing {
    private final static String TAG = "Hashing";

    /**
     * Makes the hash-representation of input value
     * @param input String value for hashing
     * @return hash-representation of input
     */

    public static String sha256(String input){
        try {
            MessageDigest SHA256 =  MessageDigest.getInstance("SHA-256");
            SHA256.update(input.getBytes("UTF-8"));

            byte[] digest = SHA256.digest();

            return String.format("%064x", new BigInteger(1, digest));
        } catch (Exception e) {
            Log.d(TAG, "SHA256 coding exception", e);
        }
        return "MissingHashing";
    }
}

