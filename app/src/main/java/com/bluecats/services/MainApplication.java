package com.bluecats.services;

import com.bluecats.sdk.BlueCatsSDK;
import com.bluecats.services.interfaces.BlueCatsSDKInterfaceService;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

public class MainApplication extends Application {
	protected static final String TAG = "MainApplication";
	
	private static final String BLUECATS_APP_TOKEN = "2cac72de-8100-4b19-b601-26290e98fb13";

	@Override
	public void onCreate() {
		super.onCreate();

		// start the BlueCatsSDKInterfaceService
		// this service will be responsible for handling SDK events while the app is closed
		Intent intent = new Intent(MainApplication.this, BlueCatsSDKInterfaceService.class);
		Bundle extras = new Bundle();
		extras.putString(BlueCatsSDK.EXTRA_APP_TOKEN, BLUECATS_APP_TOKEN);
		intent.putExtras(extras);
		startService(intent);
	}
}