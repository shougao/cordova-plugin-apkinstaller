# cordova-plugin-apkinstaller
Cordova installer plugin for Android. The plugin reads apk file path, and install it.

## install
```
cordova plugin add cordova-plugin-apkinstaller
```

## usage
``Installer`` is a ``com.mycompany.installer.Installer`` install apk
```
function success(message){
    console.log("progress = " + message);
}

function error(message){
    console.log("error: reason is " + message);
}

path = '/storage/emulated/0/Download/abc.apk';
Installer.install(path, success, error);
```
if success, it will show android defaulter PackageInstaller to install apk.
if error, will show the error message in callback.