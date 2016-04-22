package com.bluecats.services;

import java.util.List;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BCTriggeredEvent;
import com.bluecats.services.interfaces.BlueCatsSDKInterfaceService;
import com.bluecats.services.interfaces.IBlueCatsSDKInterfaceServiceCallback;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {
	private TextView mTxtMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTxtMessage = (TextView)findViewById(R.id.txt_message);
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
		
		BlueCatsSDKInterfaceService.startUpdatingMicroLocation();
		
		BlueCatsSDKInterfaceService.didEnterForeground();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		BlueCatsSDKInterfaceService.unregisterBlueCatsSDKServiceCallback(MainActivity.this.getClass().getName());
		
		BlueCatsSDKInterfaceService.stopUpdatingMicroLocation();
		
		BlueCatsSDKInterfaceService.didEnterBackground();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		BlueCatsSDKInterfaceService.unregisterBlueCatsSDKServiceCallback(MainActivity.this.getClass().getName());
		
		BlueCatsSDKInterfaceService.stopUpdatingMicroLocation();
		
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
}
