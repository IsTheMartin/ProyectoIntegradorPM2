package com.mcuadrada.proyectointegrador;

import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mcuadrada.proyectointegrador.Fragments.MapsFragment;
import com.mcuadrada.proyectointegrador.Patterns.SingletonPreferences;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //fragmentMaps();
    }

    private void fragmentMaps() {
        MapsFragment mapsFragment = new MapsFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_layout, mapsFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder closeSessionDialog = new AlertDialog.Builder(MainActivity.this);
        closeSessionDialog.setTitle("Cerrando sesión")
                .setIcon(R.mipmap.ic_launcher)
                .setMessage("¿Estás seguro de cerrar sesión?")
                .setPositiveButton("Sí, señor!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: closing session");
                        SingletonPreferences.getInstance(MainActivity.this).clearLoginData();
                        finish();
                    }
                })
                .setNegativeButton("Me quedaré", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "onClick: ");
                    }
                })
                .show();
    }
}
