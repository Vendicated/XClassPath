package dev.vendicated.xclasspath;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Main implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.spotify.music")) return;

        XClassPath.init(lpparam, "dev.vendicated.xclasspath.ActualMain");
    }
}