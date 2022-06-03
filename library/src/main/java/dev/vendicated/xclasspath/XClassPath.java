/*
 * This file is part of XClassPath
 * Copyright 2022 Vendicated
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.vendicated.xclasspath;

import android.annotation.SuppressLint;

import java.util.Arrays;

import dalvik.system.BaseDexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XClassPath {
    // Reference
    // https://android.googlesource.com/platform/libcore/+/58b4e5dbb06579bec9a8fc892012093b6f4fbe20/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java
    // https://android.googlesource.com/platform/libcore/+/refs/heads/master/dalvik/src/main/java/dalvik/system/BaseDexClassLoader.java#

    /**
     * Initialise your Xposed Module with direct access to the target app's classes.
     * Loads the specified class as if it were loaded by Xposed.
     *
     * @param param                          The LoadPackageParam
     * @param xposedHookLoadPackageClassName The full class name of your entry class. This class MUST implement {@link IXposedHookLoadPackage}
     * @throws IllegalArgumentException If the specified class does not implement {@link IXposedHookLoadPackage}
     * @throws Throwable                If something goes wrong or your class throws an exception
     */
    @SuppressWarnings({"SuspiciousSystemArraycopy", "ConstantConditions", "DiscouragedPrivateApi", "JavaReflectionMemberAccess"})
    public static void init(XC_LoadPackage.LoadPackageParam param, String xposedHookLoadPackageClassName) throws Throwable {
        var myClassLoader = XClassPath.class.getClassLoader();
        var theirClassLoader = param.classLoader;

        @SuppressLint("BlockedPrivateApi")
        var sharedLibraryLoadersField = BaseDexClassLoader.class.getDeclaredField("sharedLibraryLoaders");
        sharedLibraryLoadersField.setAccessible(true);

        var oldLoaders = (ClassLoader[]) sharedLibraryLoadersField.get(myClassLoader);
        ClassLoader[] newLoaders;
        int idx;
        if (oldLoaders != null) {
            idx = oldLoaders.length;
            newLoaders = Arrays.copyOf(oldLoaders, oldLoaders.length + 1);
        } else {
            idx = 0;
            newLoaders = new ClassLoader[1];
        }

        newLoaders[idx] = theirClassLoader;
        sharedLibraryLoadersField.set(myClassLoader, newLoaders);


        // This approach does not require HiddenApi bypass, however
        // java.lang.InternalError: Attempt to register dex file with multiple classloaders
        // An option would be to make new DexElements, however that would probably lead to
        // other issues and also increase memory usage.
        // TODO:? Make below approach work to no longer rely on HiddenAPI

        /*var pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
        pathListField.setAccessible(true);

        var myPathList = pathListField.get(myClassLoader);
        var theirPathList = pathListField.get(theirClassLoader);

        var dexElementsField = myPathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);

        var myDexElements = dexElementsField.get(myPathList);
        var theirDexElements = dexElementsField.get(theirPathList);

        var myLength = Array.getLength(myDexElements);
        var theirLength = Array.getLength(theirDexElements);
        var mergedDexElements = Array.newInstance(myDexElements.getClass().getComponentType(), myLength + theirLength);
        System.arraycopy(myDexElements, 0, mergedDexElements, 0, myLength);
        System.arraycopy(theirDexElements, 0, mergedDexElements, myLength, theirLength);
        dexElementsField.set(myPathList, mergedDexElements);*/


        var initClazz = Class.forName(xposedHookLoadPackageClassName);
        if (!IXposedHookLoadPackage.class.isAssignableFrom(initClazz)) {
            throw new IllegalArgumentException("Class " + xposedHookLoadPackageClassName + " does not implement IXposedHookLoadPackage.");
        }

        var inst = (IXposedHookLoadPackage) initClazz.newInstance();
        inst.handleLoadPackage(param);
    }
}
