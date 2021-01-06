package com.example.project_cg.asynctask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.project_cg.MainActivity;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shape.Model;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.util.ScreenShotUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

public class Screenshot extends AsyncTask<Object, Void, MainActivity> {
    @Override
    protected MainActivity doInBackground(Object... objects) {
        MainActivity activity = (MainActivity) objects[0];
        ScreenShotUtil.path = "";
        String mPath;
        if (ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE")
                == PackageManager.PERMISSION_GRANTED) {
            Date now = new Date();
            String date = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now).toString();

            try {
                // image naming and path  to include sd card  appending name you choose for file
                mPath = date + ".jpg";
                ScreenShotUtil.path = date + ".jpg";
                File file = new File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), mPath);
                ScreenShotUtil.bos = new BufferedOutputStream(new FileOutputStream(file));
                // create bitmap screen capture

                DisplayMetrics displayMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                ScreenShotUtil.height = displayMetrics.heightPixels;
                ScreenShotUtil.width = displayMetrics.widthPixels;

                Log.i("Size",ScreenShotUtil.height+":"+ScreenShotUtil.width);

                ScreenShotUtil.toScreenShot = true;

                Log.i("Save", file.getAbsolutePath());
            } catch (Throwable e) {
                // Several error may come out with file handling or DOM
                e.printStackTrace();
            }
        }
        return activity;
    }

}
