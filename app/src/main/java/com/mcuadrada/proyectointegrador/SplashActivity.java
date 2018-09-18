package com.mcuadrada.proyectointegrador;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    public static final String PERMISSIONS[] = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    public static final int PERMISSION_CODE[] = {
            100, //PHONE_STATE
            200, //LOCATION_FINE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(checkPermissions())
            startLoginActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < PERMISSION_CODE.length; i++) {
            if(PERMISSION_CODE[i] == requestCode){
                if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    checkPermissions();
                }
            }
        }
    }

    private boolean checkPermissions(){
        boolean isGranted= false;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < PERMISSIONS.length; i++) {
                if (checkSelfPermission(PERMISSIONS[i])
                        == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(new String[] {PERMISSIONS[i]},
                            PERMISSION_CODE[i]);
                    isGranted = false;
                } else {
                    isGranted = true;
                }
            }
            if(!isGranted){
                return isGranted;
            }
        }
        isGranted = true;
        return isGranted;
    }

    private void startLoginActivity(){
        Timer waitSplash = new Timer();
        waitSplash.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 1000);
    }
}
