# XClassPath

A library that gives you direct access to the classes of the apps you're hooking into with Xposed.

This allows you to write (or [autogenerate](#automatically-generating-stubs)!) stubs for your target apps so you no longer have to use Reflection for everything.

## Getting started

As this library uses hidden api, a hidden api bypass may be required. But any modern Xposed implementation should already include this.

```gradle
repositories {
    maven("https://maven.aliucord.com/snapshots")
}

dependencies {
    // or change main-SNAPSHOT to short commit hash to target a specific commit
    implementation "dev.vendicated.xlcasspath:XClassPath:main-SNAPSHOT"
}
```

## Usage

The way it works is that you have two classes implementing IXposedHookLoadPackage.
The first should be in xposed_init and contain only the following code (and the class BoilerPlate of course)
```java
@Override
public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
    if (lpparam.packageName.equals(YOUR_TARGET_PACKAGE_NAME)){
        XClassPath.init(lpparam, "com.yourname.TheSecondImplementation");
    }
}
```
The first one MUST NOT use classes of the target app.

The second one will be called by this library and can freely use classes of the target app.

See the examples directory for a very simple example targeting the Spotify App.

## Automatically generating stubs

You can use [our fork](https://github.com/Aliucord/dex2jar) of the awesome dex2jar project to convert the apk to a .jar file with all classes

Just grab the [latest release](https://github.com/Aliucord/dex2jar/releases/latest/download/dex2jar.jar) and run
```shell
java -jar dex2jar.jar --no-code --debug-info YOURAPK.apk --output stubs.jar
```
Now add that jar as compileOnly dependency and you're good to go!

Please note that while this jar does not contain any code, only implementation-less interfaces, pushing it to your repo
may be Copyright infringement, so you should probably gitignore it.