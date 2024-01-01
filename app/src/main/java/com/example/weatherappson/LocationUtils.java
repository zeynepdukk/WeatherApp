package com.example.weatherappson;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class LocationUtils {

    private static final String TAG = "LocationUtils";

    public interface LocationListenerCallback {
        void onLocationReceived(double latitude, double longitude);
    }

    public static void getCurrentLocation(Context context, final LocationListenerCallback callback) {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        if (locationManager != null) {
            try {
                // Request location updates
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();


                        Log.d(TAG, "Location received - Latitude: " + latitude + ", Longitude: " + longitude);

                        callback.onLocationReceived(latitude, longitude);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        Log.d(TAG, "Location status changed - Provider: " + provider + ", Status: " + status);
                        // Handle status changes if needed
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Log.d(TAG, "Location provider enabled - Provider: " + provider);
                        // Handle provider enabled if needed
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Log.d(TAG, "Location provider disabled - Provider: " + provider);
                        // Handle provider disabled if needed
                    }
                }, null);
            } catch (SecurityException e) {

                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Location manager is null");
        }
    }
}
