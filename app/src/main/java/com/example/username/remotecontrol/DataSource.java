package com.example.username.remotecontrol;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class DataSource {
    private final String TAG = "DataSources";

    public static String EXTERNAL_FILES_DIR_PATH;

    private File grammarFile;
    private File languageModelFile;
    private File acousticModelDir;

    private final String GRAMMAR_FILE_NAME = "grammar.jsgf";
    private final String LANGUAGE_MODEL_FILE_NAME = "model.dict";
    private final String ACOUSTIC_MODEL_DIR_NAME = "acoustic_model";

    private Context context;

    public DataSource(Context context) {
        this.context = context;
        EXTERNAL_FILES_DIR_PATH = context.getExternalFilesDir(null).getAbsolutePath();
        getDataFromAssets();
    }

    public File getGrammarFile(){
        grammarFile = new File(EXTERNAL_FILES_DIR_PATH + "/" + GRAMMAR_FILE_NAME);
        if(grammarFile.exists()){
            return grammarFile;
        } else {
            Log.d(TAG, "Grammar file does`t found");
            return null;
        }
    }

    public File getLanguageModelFile(){
        languageModelFile = new File(EXTERNAL_FILES_DIR_PATH + "/" + LANGUAGE_MODEL_FILE_NAME);
        if(languageModelFile.exists()){
            return languageModelFile;
        } else {
            Log.d(TAG, "Grammar file does`t found");
            return null;
        }
    }

    public File getAcousticModelDir(){
        acousticModelDir = new File(EXTERNAL_FILES_DIR_PATH + "/" + ACOUSTIC_MODEL_DIR_NAME);
        if(acousticModelDir.exists()){
            return acousticModelDir;
        } else {
            Log.d(TAG, "Grammar file does`t found");
            return null;
        }
    }

    private void getDataFromAssets(){
        AssetManager assetManager = context.getAssets();
        String[] fileNames = null;

        try {
            fileNames = assetManager.list("");
        } catch (IOException e) {
            Log.e(TAG, "Failed to get asset file list.", e);
        }

        if (fileNames != null) {
            for (String filename : fileNames) {
                File outFile = new File(EXTERNAL_FILES_DIR_PATH + "/" + filename);
                try (
                    InputStream in = assetManager.open(filename);
                    OutputStream out = new FileOutputStream(outFile);
                ) {
                    copyStreams(in, out);
                } catch (IOException e) {
                    Log.e(TAG, "Failed to copy asset file: " + filename, e);
                }
            }
        }
    }

    private boolean copyStreams(InputStream inputStream, OutputStream outputStream) {
        byte[] buffer = new byte[1024];
        int read;
        try {
            while((read = inputStream.read(buffer)) != -1){
                outputStream.write(buffer, 0, read);
            }
        } catch (IOException e) {
            Log.d(TAG, "Failed to copy streams", e);
            return false;
        }
        return true;
    }
}
