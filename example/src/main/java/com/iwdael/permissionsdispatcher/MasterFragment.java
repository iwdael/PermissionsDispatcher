package com.iwdael.permissionsdispatcher;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcher;
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherDenied;
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherNeeds;
import com.iwdael.permissionsdispatcher.annotation.PermissionsDispatcherRationale;
import com.iwdael.permissionsdispatcher.annotation.PermissionsRationale;

import java.util.Arrays;
import java.util.List;

/**
 * author : iwdael
 * e-mail : iwdael@outlook.com
 */
@PermissionsDispatcher
public class MasterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        view.findViewById(R.id.btnCamera).setOnClickListener(v -> {
            MasterFragmentPermissionDispatcher.takePhotoWithPermission(this, "", "");
        });
        return view;
    }

    @PermissionsDispatcherNeeds(Manifest.permission.CAMERA)
    public void takePhoto(String path, String name) {
        Log.v("dzq", "takePhoto");
    }

    @PermissionsDispatcherDenied(Manifest.permission.CAMERA)
    public void takePhotoDenied(List<String> permission, List<Boolean> bannedResults) {
        Log.v("dzq", "takePhotoDenied:::::" + Arrays.toString(permission.toArray()) + " , " + Arrays.toString(bannedResults.toArray()));
    }

    @PermissionsDispatcherRationale(Manifest.permission.CAMERA)
    public void takePhotoRationale(PermissionsRationale rationale) {
        Log.v("dzq", "takePhotoRationale");
        rationale.apply();
    }

}
