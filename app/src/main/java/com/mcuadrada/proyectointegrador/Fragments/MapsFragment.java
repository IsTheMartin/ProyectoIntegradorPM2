package com.mcuadrada.proyectointegrador.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.mcuadrada.proyectointegrador.R;
import com.mcuadrada.proyectointegrador.SplashActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private static final String TAG = MapsFragment.class.getSimpleName();

    MapView mMapView;
    private GoogleMap googleMap;
    SupportMapFragment supportMapFragment;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapView);

        if(supportMapFragment == null){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            supportMapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.mapView, supportMapFragment).commit();
        }

        supportMapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;

    }
}
