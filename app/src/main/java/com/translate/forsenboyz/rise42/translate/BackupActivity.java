package com.translate.forsenboyz.rise42.translate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.translate.forsenboyz.rise42.translate.MainActivity.TAG;

public class BackupActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 42;

    private File directoryDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermission();
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
            new DataBackup().execute();
        }
        else{
            Log.d(TAG, "checkPermission: NO DAMN PERMISSION");
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST
                    );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"Nice",Toast.LENGTH_SHORT).show();
            checkPermission();
        } else{
            Toast.makeText(this,"What a disgusting person you are",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class DataBackup extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            try {

                File[] externalDirs = getApplicationContext().getExternalFilesDirs(null);

                if(externalDirs != null && externalDirs.length >=2){
                    directoryDB = externalDirs[1];
                    Log.d(TAG, "doInBackground: dir on SD:"+directoryDB.getAbsolutePath());
                } else{
                    Log.d(TAG, "doInBackground: no SD found");
                    return RESULT_CANCELED;
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH..mm");

                File targetFile = new File(directoryDB, dateFormat.format(new Date())+".json");
                targetFile.createNewFile();

                Log.d(TAG, "target db path: "+targetFile.getAbsolutePath());

                copyDB(targetFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return RESULT_OK;
        }

        @Override
        protected void onPostExecute(Object o) {
            setResult((int)o);
            finish();
        }

        private void copyDB(File copy) throws IOException {
            Log.d(TAG, "doInBackground: coping file");

            FileWriter writer = new FileWriter(copy);

            writer.write(DatabaseHandler.getInstance(BackupActivity.this).getAllAsJson().toString());
            writer.flush();

            writer.close();

            Log.d(TAG, "doInBackground: copied");
        }
    }

}
