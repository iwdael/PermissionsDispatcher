package com.absurd.demo_onpermission;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.absurd.onpermission.OnPermission;
import com.absurd.onpermission.Permission;
import com.absurd.onpermission.PermissionListener;
import com.absurd.onpermission.UsePermission;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PermissionListener {
    OnPermission onPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onPermission = new OnPermission(MainActivity.this);
        onPermission.setPermissionListener(this);
        //  String[] permissions = new String[]{UsePermission.WRITE_EXTERNAL_STORAGE, UsePermission.CAMERA};
        Permission[] permissions1 = new Permission[]{
                new Permission(UsePermission.WRITE_EXTERNAL_STORAGE, getString(R.string.sdcard)),
                new Permission(UsePermission.CAMERA, getString(R.string.camera))
        };
        onPermission.authorize(permissions1);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onAuthorize(String permission, boolean result) {

        Log.v("TAG", "------>>" + permission + "--------->>" + result);
        if (!result) {
            Toast.makeText(MainActivity.this,"3 back",Toast.LENGTH_SHORT).show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        finish();
                        } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).run();
        }
    }
}
