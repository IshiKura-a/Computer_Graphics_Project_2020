package com.example.project_cg.asynctask;

import android.content.pm.PackageManager;
import android.os.AsyncTask;

import androidx.core.app.ActivityCompat;

import com.example.project_cg.MainActivity;

public class CheckStorage extends AsyncTask<Object, Void, MainActivity> {
    @Override
    protected MainActivity doInBackground(Object... params) {
        int permission;
        MainActivity activity = (MainActivity) params[0];
        try {
            //检测是否有写的权限
            permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, new String[]{
                        "android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"}, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activity;
    }
}