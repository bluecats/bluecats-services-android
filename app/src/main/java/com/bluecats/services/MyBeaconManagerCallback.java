package com.bluecats.services;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BCSite;

import android.util.Log;

import java.net.URL;
import java.util.List;

/**
 * Created by henrycheng on 18/07/2016.
 */
public class MyBeaconManagerCallback extends BCBeaconManagerCallback {
    private static final String TAG = "MyBeaconManagerCallback";

    @Override
    public void didEnterSite(BCSite site) {
        super.didEnterSite(site);
        Log.d(TAG, "didEnterSite "+site.getName());
    }

    @Override
    public void didExitSite(BCSite site) {
        super.didExitSite(site);
        Log.d(TAG, "didExitSite "+site.getName());
    }

    @Override
    public void didDetermineState(BCSite.BCSiteState state, BCSite forSite) {
        super.didDetermineState(state, forSite);
        Log.d(TAG, "didDetermineState "+forSite.getName());
    }

    @Override
    public void didEnterBeacons(List<BCBeacon> beacons) {
        super.didEnterBeacons(beacons);
        Log.d(TAG, "didEnterBeacons "+getBeaconNames(beacons));
    }

    @Override
    public void didExitBeacons(List<BCBeacon> beacons) {
        super.didExitBeacons(beacons);
        Log.d(TAG, "didExitBeacons "+getBeaconNames(beacons));
    }

    @Override
    public void didDetermineState(BCBeacon.BCBeaconState state, BCBeacon forBeacon) {
        super.didDetermineState(state, forBeacon);
        Log.d(TAG, "didDetermineState "+forBeacon.getSerialNumber());
    }

    @Override
    public void didRangeBeacons(List<BCBeacon> beacons) {
        super.didRangeBeacons(beacons);
        Log.d(TAG, "didRangeBeacons "+getBeaconNames(beacons));
    }

    @Override
    public void didRangeBlueCatsBeacons(List<BCBeacon> beacons) {
        super.didRangeBlueCatsBeacons(beacons);
        Log.d(TAG, "didRangeBlueCatsBeacons "+getBeaconNames(beacons));
    }

    @Override
    public void didRangeNewbornBeacons(List<BCBeacon> newBornBeacons) {
        super.didRangeNewbornBeacons(newBornBeacons);
        Log.d(TAG, "didRangeNewbornBeacons "+getBeaconNames(newBornBeacons));
    }

    @Override
    public void didRangeIBeacons(List<BCBeacon> iBeacons) {
        super.didRangeIBeacons(iBeacons);
        Log.d(TAG, "didRangeIBeacons "+getBeaconNames(iBeacons));
    }

    @Override
    public void didRangeEddystoneBeacons(List<BCBeacon> eddystoneBeacons) {
        super.didRangeEddystoneBeacons(eddystoneBeacons);
        Log.d(TAG, "didRangeEddystoneBeacons "+getBeaconNames(eddystoneBeacons));
    }

    @Override
    public void didDiscoverEddystoneURL(URL eddystoneUrl) {
        super.didDiscoverEddystoneURL(eddystoneUrl);
        Log.d(TAG, "didDiscoverEddystoneURL "+eddystoneUrl.toString());
    }

    private String getBeaconNames(List<BCBeacon> beacons) {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (BCBeacon beacon: beacons) {
            sb.append(beacon.getSerialNumber());
            sb.append(',');
        }
        sb.append(']');
        return sb.toString();
    }

}
