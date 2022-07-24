package com.iwdael.permissionsdispatcher;

import android.Manifest;
import android.util.Log;

import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher;
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied;
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds;
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale;
import com.iwdael.permissionsdispatcher.dispatcher.PermissionsRationale;

import java.util.Arrays;
import java.util.List;
/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@PermissionsDispatcher
public class SampleKt {
    @PermissionsDispatcherNeeds(value = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void takePhoto(String path, String name) {
        Log.v("dzq", "takePhoto");
    }

    @PermissionsDispatcherDenied(value = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void takePhotoDenied(List<String> permission, List<Boolean> bannedResults) {
        Log.v("dzq", "takePhotoDenied::" + Arrays.toString(permission.toArray()) + " , " + Arrays.toString(bannedResults.toArray()));
    }

    @PermissionsDispatcherRationale(value = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void takePhotoRationale(PermissionsRationale rationale) {
        Log.v("dzq", "takePhotoRationale");
        rationale.apply();
    }
}
