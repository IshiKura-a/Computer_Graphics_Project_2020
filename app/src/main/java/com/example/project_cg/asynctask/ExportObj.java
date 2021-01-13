package com.example.project_cg.asynctask;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.example.project_cg.MainActivity;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.shape.Cone;
import com.example.project_cg.shape.Cylinder;
import com.example.project_cg.shape.Frustum;
import com.example.project_cg.shape.Model;
import com.example.project_cg.shape.Prism;
import com.example.project_cg.shape.Pyramid;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.shape.ShapeType;
import com.example.project_cg.util.ExportObjUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;


public class ExportObj extends AsyncTask<Activity, Void, Void> {
    @Override
    protected Void doInBackground(Activity... voids) {
        ExportObjUtil.path = "";
        MainActivity activity = (MainActivity) voids[0];
        LinkedList<Shape> list = new LinkedList<>();
        synchronized(Observe.getCamera()) {
            for (Shape s : activity.getmRender().getShapes()) {
                if (s.isChosen()) {
                    list.add(s);
                    if(s.getType() == ShapeType.CONE) {
                        list.add(((Cone)s).getBottom());
                    }
                    else if(s.getType() == ShapeType.CYLINDER) {
                        list.add(((Cylinder)s).getTop());
                        list.add(((Cylinder)s).getBottom());
                    }
                    else if(s.getType() == ShapeType.FRUSTUM) {
                        list.add(((Frustum)s).getTop());
                        list.add(((Frustum)s).getBottom());
                    }
                    else if(s.getType() == ShapeType.PRISM) {
                        list.add(((Prism)s).getTop());
                        list.add(((Prism)s).getBottom());
                    }
                    else if(s.getType() == ShapeType.PYRAMID) {
                        list.add(((Pyramid)s).getBottom());
                    }
                }
            }
        }
        if (list.size() == 0) return null;
        if (ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE")
                == PackageManager.PERMISSION_GRANTED) {
            try {
                File file = new File(activity.getExternalFilesDir("Models"),
                        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", new Date()).toString() + ".obj");
                FileOutputStream fos = new FileOutputStream(file);

                Model.writeObject(list, fos);
                Log.i("Storage", file.getAbsolutePath());
                ExportObjUtil.path = file.getName();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
