# bluecats-services-android
A sample app that uses a service to communicate with the SDK. The benefits of this approach are that the service will allow you to wake your app from the background upon a beacon event, even if it has been closed. Also, going via the service allows the app greater control in what should happen in your app, for example opening a specific activity, triggering a notification, etc.

## SDK version
### v2.0.0-rc-1 and above
- Use BCBeaconManager to monitor the Beacons and Sites

### v1.13.8 and below
- Use BCMicroLocationManager to mointor the Beacons and Sites
- Check out the code before commit #aa3f6f7

See the sample BlueCats Scratching Post app for usage and integration instructions https://github.com/bluecats/bluecats-scratchingpost-android
