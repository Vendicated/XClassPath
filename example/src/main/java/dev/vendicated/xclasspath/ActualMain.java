package dev.vendicated.xclasspath;

import android.util.Log;

import com.spotify.core.http.HttpResponse;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ActualMain implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XposedBridge.hookMethod(HttpResponse.class.getDeclaredConstructor(int.class, String.class, String.class), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                var res = (HttpResponse) param.thisObject;
                Log.i("XClassPath", "Received HttpResponse: " + res.getUrl() + " - " + res.getStatus());
            }
        });
    }
}
