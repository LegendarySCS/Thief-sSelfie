package facci.pc.mel.thiefselfie;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class Localizacion implements LocationListener {
    MainActivity mainActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    @Override
    public void onLocationChanged(Location location) {
        location.getLatitude();
        location.getLongitude();
        this.mainActivity.setLocation(location);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
