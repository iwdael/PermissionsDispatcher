package com.aliletter.demo_onpermission;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aliletter.onpermission.OnPermission;
import com.aliletter.onpermission.Permission;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new OnPermission(this).grant(new Permission() {
            @Override
            public String[] permissions() {
                return new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS
                };
            }

            @Override
            public void onGranted(String[] pemissions) {
            }

            @Override
            public void onDenied(String[] pemissions) {
            }
        });

    }


}
