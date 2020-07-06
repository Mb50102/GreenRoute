# GreenRoute
Green routes are created in such a way that when calculating the route, preference is given to roads on which there are currently a lower number of motor vehicles.
The user can define in the mobile application how much environmentally friendly the route will be. In settings the variable radius can be adjusted to determine how far we will move away from roads that have an increased number of cars. The traffic congestion factor can also be customized in the settings. The traffic congestion factor determines those parts of the roads that will be avoided. Among other things, in the application, there is a possibility to graphically display to the user the congested parts of the roads. Traffic congestion data is in real-time retrieved from the Here server.

# Features
<p align="center">
  <img src="https://github.com/Mb50102/GreenRoute/blob/master/AppExample.png" />
</p>

- Route planning  
- Load routes planned on [ GraphHopper route optimization API ](https://www.graphhopper.com/route-optimization/)
- Traffic data retrived from [ Here API ](https://developer.here.com/documentation/traffic/dev_guide/topics/what-is.html)

# Installation and setup

- App wont work properly on the emulator, it needs to be installed directly on the mobile device.

In String.xml GraphHopperApiKey and HereApiKey needs to be replaced with Api keys that are currently active.

In the AndroidManifest.xml also Here SDK key_id and key_secret needs to be replaced.
```python
 <meta-data android:name="com.here.sdk.access_key_id" android:value="insert key id"/>
 <meta-data android:name="com.here.sdk.access_key_secret" android:value="insert key secret "/>
```

