package com.bluecats.services;

import java.net.URL;
import java.util.List;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconManager;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BCTriggeredEvent;
import com.bluecats.sdk.BlueCatsSDK;
import com.bluecats.services.interfaces.BlueCatsSDKInterfaceService;
import com.bluecats.services.interfaces.IBlueCatsSDKInterfaceServiceCallback;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "MainActivity";
	private TextView mTxtMessage;
	private Button mButton;
	private Button mButton2;

	BCBeaconManager mBCBeaconManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTxtMessage = (TextView)findViewById(R.id.txt_message);
		mButton = (Button)findViewById(R.id.btn);
		mButton2 = (Button)findViewById(R.id.btn2);
		mButton.setOnClickListener(this);
		mButton2.setOnClickListener(this);

		MainApplication.runSDK(this.getApplicationContext());
		mBCBeaconManager = new BCBeaconManager();
		mBCBeaconManager.registerCallback(mCallback);

	}

	BCBeaconManagerCallback mCallback = new BCBeaconManagerCallback() {
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
	};

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		BlueCatsSDKInterfaceService.registerBlueCatsSDKServiceCallback(MainActivity.this.getClass().getName(), mBlueCatsSDKInterfaceServiceCallback);
		
		BlueCatsSDKInterfaceService.didEnterForeground();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		BlueCatsSDKInterfaceService.unregisterBlueCatsSDKServiceCallback(MainActivity.this.getClass().getName());

		BlueCatsSDKInterfaceService.didEnterBackground();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
		mBCBeaconManager.unregisterCallback(mCallback);
		BlueCatsSDKInterfaceService.unregisterBlueCatsSDKServiceCallback(MainActivity.this.getClass().getName());
		
		BlueCatsSDKInterfaceService.didEnterBackground();
	}
	
	private IBlueCatsSDKInterfaceServiceCallback mBlueCatsSDKInterfaceServiceCallback = new IBlueCatsSDKInterfaceServiceCallback() {
		@Override
		public void onDidEnterSite(BCSite site) {
			
		}

		@Override
		public void onDidExitSite(BCSite site) {
			
		}

		@Override
		public void onDidUpdateNearbySites(List<BCSite> sites) {
			
		}

		@Override
		public void onDidRangeBeaconsForSiteID(BCSite site, List<BCBeacon> beacons) {
			
		}

		@Override
		public void onDidUpdateMicroLocation(List<BCMicroLocation> microLocations) {
			
		}
		
		@Override
		public void onTriggeredEvent(BCTriggeredEvent triggeredEvent) {
			final BCBeacon beacon = triggeredEvent.getFilteredMicroLocation().getBeacons().get(0);
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mTxtMessage.setText("Closest to beacon " + beacon.getSerialNumber());
				}
			});
		}
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn) {

		} else if (v.getId() == R.id.btn2) {

		}
	}
}
