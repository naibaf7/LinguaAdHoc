package ch.smartapes.linguaadhoc.android;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationContext implements LocationListener {

	private Context context;
	

	public LocationContext(Context context) {
		this.context = context;
	}

	public Location getLoc() {
		Location location = null;

		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		boolean isGPS = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNET = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (isGPS) {
			location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, 10000, 1000, this);
			}
			location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			// Fallback
			if (location == null) {
				isNET = false;
			}
		}
		if (isNET) {
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (location == null) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 10000, 1000, this);
			}
			location = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		return location;
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
