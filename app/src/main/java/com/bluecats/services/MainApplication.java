package com.bluecats.services;

import com.bluecats.sdk.BlueCatsSDK;
import com.bluecats.services.interfaces.BlueCatsSDKInterfaceService;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class MainApplication extends Application {
	protected static final String TAG = "MainApplication";
	
	public static final String BLUECATS_APP_TOKEN = "";

	@Override
	public void onCreate() {
		super.onCreate();
//		Fabric.with(this, new Crashlytics());

		if (TextUtils.isEmpty(BLUECATS_APP_TOKEN)) {
			throw new RuntimeException("BLUECATS_APP_TOKEN is invalid in MainApplication.java.");
		}

		try {
			Log.d(TAG, "googleplayservice_ver "+getPackageManager().getPackageInfo("com.google.android.gms", 0 ).versionCode);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void runSDK(Context context) {
		// start the BlueCatsSDKInterfaceService
		// this service will be responsible for handling SDK events while the app is closed
		Intent intent = new Intent(context, BlueCatsSDKInterfaceService.class);
		Bundle extras = new Bundle();
		extras.putString(BlueCatsSDK.EXTRA_APP_TOKEN, BLUECATS_APP_TOKEN);
		intent.putExtras(extras);
		context.startService(intent);
	}
}
