package com.bluecats.services.interfaces;

import java.util.List;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCMicroLocation;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BCTriggeredEvent;

public interface IBlueCatsSDKInterfaceServiceCallback {
	void onDidEnterSite(BCSite site);
	void onDidExitSite(BCSite site);
	void onDidUpdateNearbySites(List<BCSite> sites);
	void onDidRangeBeaconsForSiteID(BCSite site, List<BCBeacon> beacons);
	void onDidUpdateMicroLocation(List<BCMicroLocation> microLocations);
	void onTriggeredEvent(BCTriggeredEvent triggeredEvent);
}