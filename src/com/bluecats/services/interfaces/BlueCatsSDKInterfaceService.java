/* 
 * Copyright (c) 2014 BlueCats. All rights reserved.
 * http://www.bluecats.com
 * 
 * @author Darren Ireland
 */

package com.bluecats.services.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.Map.Entry;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCEventFilter;
import com.bluecats.sdk.BCEventManager;
import com.bluecats.sdk.BCEventManagerCallback;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCMicroLocationManager;
import com.bluecats.sdk.BCMicroLocationManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BCTrigger;
import com.bluecats.sdk.BCTriggeredEvent;
import com.bluecats.sdk.BlueCatsSDK;
import com.bluecats.sdk.IBCEventFilter;
import com.bluecats.sdk.BCBeacon.BCProximity;
import com.bluecats.services.MainActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class BlueCatsSDKInterfaceService extends Service {
	protected static final String TAG = "BlueCatsSDKInterfaceService";

	private static final String EVENT_HEARD_BEACON = "EVENT_HEARD_BEACON";
	private static final int EVENT_LOCAL_NOTIFICATION_ID = 111;

	private static WeakHashMap<String, IBlueCatsSDKInterfaceServiceCallback> mBlueCatsSDKServiceCallbacks;
	private static WeakHashMap<String, IBlueCatsSDKInterfaceServiceCallback> getBlueCatsSDKServiceCallbacks() {
		if (mBlueCatsSDKServiceCallbacks == null) {
			mBlueCatsSDKServiceCallbacks = new WeakHashMap<String, IBlueCatsSDKInterfaceServiceCallback>();
		}
		synchronized(mBlueCatsSDKServiceCallbacks) {
			return mBlueCatsSDKServiceCallbacks;
		}
	}

	private static Context mServiceContext;
	private static Context getServiceContext() {
		synchronized(mServiceContext) {
			return mServiceContext;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mServiceContext = BlueCatsSDKInterfaceService.this;

		Log.d(TAG, "onCreate");
	}

	@Override
	public int onStartCommand(final Intent intent, final int flags, final int startId) {
		super.onStartCommand(intent, flags, startId);

		Log.d(TAG, "onStartCommand");

		String appToken = "";
		if (intent != null && intent.getStringExtra(BlueCatsSDK.EXTRA_APP_TOKEN) != null) {
			appToken = intent.getStringExtra(BlueCatsSDK.EXTRA_APP_TOKEN);
		}

		// add any options here
		Map<String, String> options = new HashMap<String, String>();
		options.put(BlueCatsSDK.BC_OPTION_CROWD_SOURCE_BEACON_UPDATES, "false");
		BlueCatsSDK.setOptions(options);

		BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), appToken);

		Log.d(TAG, "startPurringWithAppToken " + appToken);

		// add event filters for event
		List<IBCEventFilter> filters = new ArrayList<IBCEventFilter>();
		filters.add(BCEventFilter.filterByClosestBeacon());
		filters.add(BCEventFilter.filterByMinTimeIntervalBetweenTriggers(5000)); // trigger event every 5 seconds

		BCTrigger trigger = new BCTrigger(EVENT_HEARD_BEACON, filters);
		trigger.setRepeatCount(Integer.MAX_VALUE);
		BCEventManager.getInstance().monitorEventWithTrigger(trigger, mEventManagerCallback);

		// start the service and keep running even if activity is destroyed
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.d(TAG, "onDestroy");
	}

	/**
	 * Start updating micro location to begin ranging beacons
	 */
	public static void startUpdatingMicroLocation() {
		Log.d(TAG, "startUpdatingMicroLocation");

		BCMicroLocationManager.getInstance().startUpdatingMicroLocation(mMicroLocationManagerCallback);
	}

	/**
	 * Stop updating micro location to stop ranging beacons
	 */
	public static void stopUpdatingMicroLocation() {
		Log.d(TAG, "stopUpdatingMicroLocation");

		BCMicroLocationManager.getInstance().stopUpdatingMicroLocation(mMicroLocationManagerCallback);
	}

	/**
	 * Register a callback for your activity to receive updates from any SDK events you have defined
	 */
	public static void registerBlueCatsSDKServiceCallback(String className, IBlueCatsSDKInterfaceServiceCallback callback) {
		getBlueCatsSDKServiceCallbacks().put(className, callback);

		Log.d(TAG, "registerBlueCatsSDKServiceCallback");
	}

	/**
	 * Unregister your activity's callback when the activity is closed or destroyed
	 */
	public static void unregisterBlueCatsSDKServiceCallback(String className) {
		getBlueCatsSDKServiceCallbacks().remove(className);
		
		Log.d(TAG, "unregisterBlueCatsSDKServiceCallback");
	}

	/**
	 * Let the SDK know when the app has entered the foreground to increase Beacon scanning rate
	 */
	public static void didEnterForeground() {
		BlueCatsSDK.didEnterForeground();

		Log.d(TAG, "didEnterForeground");
	}

	/**
	 * Let the SDK know when the app has entered the foreground to decrease Beacon scanning rate
	 */
	public static void didEnterBackground() {
		BlueCatsSDK.didEnterBackground();

		Log.d(TAG, "didEnterBackground");
	}

	private BCEventManagerCallback mEventManagerCallback = new BCEventManagerCallback() {
		@Override
		public void onTriggeredEvent(final BCTriggeredEvent triggeredEvent) {
			Log.d(TAG, "onTriggeredEvent" + triggeredEvent.getEvent().getEventIdentifier());
			
			if (triggeredEvent.getEvent().getEventIdentifier().equals(EVENT_HEARD_BEACON)) {
				// if there are no callbacks registered the the app is probably closed or in the background
				// send a local notification to wake the app up
				if (getBlueCatsSDKServiceCallbacks().size() == 0) {
					// if closest beacon is unknown dont fire notification
					if (triggeredEvent.getFilteredMicroLocation().getBeacons().size() > 0) {
						BCBeacon closestBeacon = triggeredEvent.getFilteredMicroLocation().getBeacons().get(0);
						if (closestBeacon.getProximity() == BCProximity.BC_PROXIMITY_UNKNOWN) {
							return;
						}
					}
					
					Intent mainActivityIntent = new Intent(getServiceContext(), MainActivity.class);
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(getServiceContext());
					stackBuilder.addParentStack(mainActivityIntent.getComponent());
					stackBuilder.addNextIntent(mainActivityIntent);

					PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
					NotificationCompat.Builder builder = new NotificationCompat.Builder(getServiceContext())
					.setContentTitle("A beacon was detected")
					.setContentText("Open your app!")
					.setContentIntent(resultPendingIntent)
					.setOnlyAlertOnce(true)
					.setAutoCancel(true);

					int applicationIcon = 0;
					try {
						PackageManager packageManager = getServiceContext().getPackageManager();
						String packageName = mServiceContext.getPackageName();
						applicationIcon = packageManager.getPackageInfo(packageName, 0).applicationInfo.icon;
					} catch (NameNotFoundException e) {
						Log.e(TAG, e.toString());
					}
					builder.setSmallIcon(applicationIcon);

					Uri defaultSound = RingtoneManager.getActualDefaultRingtoneUri(getServiceContext(), RingtoneManager.TYPE_NOTIFICATION);
					builder.setSound(defaultSound);

					NotificationManager notificationManager = (NotificationManager)getServiceContext().getSystemService(Context.NOTIFICATION_SERVICE);
					notificationManager.notify(EVENT_LOCAL_NOTIFICATION_ID, builder.build());
				} else {
					// handle beacon logic here or return the event to your activity
					Iterator<Entry<String, IBlueCatsSDKInterfaceServiceCallback>> iterator = getBlueCatsSDKServiceCallbacks().entrySet().iterator();
					while (iterator.hasNext()) {
						Entry<String, IBlueCatsSDKInterfaceServiceCallback> entry = iterator.next();

						IBlueCatsSDKInterfaceServiceCallback callback = entry.getValue();
						if (callback != null) {
							callback.onTriggeredEvent(triggeredEvent);
						}
					}
				}
			}
		}
	};

	private static BCMicroLocationManagerCallback mMicroLocationManagerCallback = new BCMicroLocationManagerCallback() {
		@Override
		public void onDidEnterSite(BCSite site) {
			Log.d(TAG, "onDidEnterSite");
			
			// handle logic here or return the event to your activity
			Iterator<Entry<String, IBlueCatsSDKInterfaceServiceCallback>> iterator = getBlueCatsSDKServiceCallbacks().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IBlueCatsSDKInterfaceServiceCallback> entry = iterator.next();

				IBlueCatsSDKInterfaceServiceCallback callback = entry.getValue();
				if (callback != null) {
					callback.onDidEnterSite(site);
				}
			}
		}

		@Override
		public void onDidExitSite(final BCSite site) {
			Log.d(TAG, "onDidExitSite");
			
			// handle logic here or return the event to your activity
			Iterator<Entry<String, IBlueCatsSDKInterfaceServiceCallback>> iterator = getBlueCatsSDKServiceCallbacks().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IBlueCatsSDKInterfaceServiceCallback> entry = iterator.next();

				IBlueCatsSDKInterfaceServiceCallback callback = entry.getValue();
				if (callback != null) {
					callback.onDidExitSite(site);
				}
			}
		}

		@Override
		public void onDidRangeBeaconsForSiteID(final BCSite site, final List<BCBeacon> beacons) {
			Log.d(TAG, "onDidRangeBeaconsForSiteID");
			
			// handle logic here or return the event to your activity
			Iterator<Entry<String, IBlueCatsSDKInterfaceServiceCallback>> iterator = getBlueCatsSDKServiceCallbacks().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IBlueCatsSDKInterfaceServiceCallback> entry = iterator.next();

				IBlueCatsSDKInterfaceServiceCallback callback = entry.getValue();
				if (callback != null) {
					callback.onDidRangeBeaconsForSiteID(site, beacons);
				}
			}
		}

		@Override
		public void onDidUpdateMicroLocation(final List<BCMicroLocation> microLocations) {
			Log.d(TAG, "onDidUpdateMicroLocation");
			
			// handle logic here or return the event to your activity
			Iterator<Entry<String, IBlueCatsSDKInterfaceServiceCallback>> iterator = getBlueCatsSDKServiceCallbacks().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IBlueCatsSDKInterfaceServiceCallback> entry = iterator.next();

				IBlueCatsSDKInterfaceServiceCallback callback = entry.getValue();
				if (callback != null) {
					callback.onDidUpdateMicroLocation(microLocations);
				}
			}
		}

		@Override
		public void onDidUpdateNearbySites(final List<BCSite> sites) {
			Log.d(TAG, "onDidUpdateNearbySites");
			
			// handle logic here or return the event to your activity
			Iterator<Entry<String, IBlueCatsSDKInterfaceServiceCallback>> iterator = getBlueCatsSDKServiceCallbacks().entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, IBlueCatsSDKInterfaceServiceCallback> entry = iterator.next();

				IBlueCatsSDKInterfaceServiceCallback callback = entry.getValue();
				if (callback != null) {
					callback.onDidUpdateNearbySites(sites);
				}
			}
		}
	};

	public class LocalBinder extends Binder {
		public BlueCatsSDKInterfaceService getService() {
			return BlueCatsSDKInterfaceService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBlueCatsSDKServiceBinder;
	}

	private final IBinder mBlueCatsSDKServiceBinder = new LocalBinder();
}
