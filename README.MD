## Abstract
Nowadays, along with the popularity of QR codes, there are many libraries that support us decode into text in our app. 
Some of them are Mobile Vision (using Google services and now it is a part of ML Kit), XZing and XZingScanner.
Unfortunately, Mobile Vision only support detecting QR code from Bitmap, XZing is too hard to implement and XZingScanner sometimes occurs "black camera" issue after back from another activity.
So, I decide to make this library as a replacement. I'm using CameraX and XZing because some of resons below: <br>
1. CameraX is a powerful Jetpack support library and is the easiest way to add camera capabilities to our apps. It's a perfect choose instead of Camera2 API <br>
2. According to testing, XZing seems stronger than Mobile Vision about accuracy and performance.


# 1. Installation
Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
````

Add the dependency
```
implementation 'com.github.thaitv21:QRCodeScanner:1.0.0'
```

# 2. Usage
Create a class named MainApplication
```java
public class MainApplication extends Application implements CameraXConfig.Provider {

    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig()
    }
}

```

Register your application in Manifest
```xml
<application
    ...
    android:name=".MainApplication"
    ...>
    ...
</application>
```

Declare CAMERA permission for your app
```
<uses-permission android:name="android.permission.CAMERA"/>
```

In your layout, add the view
```xml
<com.nullexcom.qrscanner.QRScannerView
    android:id="@+id/qrScannerView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
```

QRScannerView provides some methods, which helps you scan QR code:
1. ***startCamera(LifecycleOwner)***<br>
Start the rear camera and scan QR code.<br>
LifecycleOwner may be Activity or Fragment<br>

2. ***stopCamera()***<br>
Deactive scannable camera<br>

3. ***setCallback(long delay, Result result)***<br>
Register callback, which invokes when any QR founded.<br>
delay: timeout between two reported events<br>
result: QR result<br>

# ***NOTE***
1. On Android 6 or higher, you need request runtime permission for CAMERA
2. *startCamera* should called in *onResume()*
3. *stopCamera* should called in *onPause()*
4. If you are getting any error related to library's dependencies, add the following dependencies:
```
implementation 'androidx.camera:camera-core:1.0.0-beta02'
implementation 'androidx.camera:camera-camera2:1.0.0-beta02'
implementation 'com.google.zxing:core:3.3.3'
```
