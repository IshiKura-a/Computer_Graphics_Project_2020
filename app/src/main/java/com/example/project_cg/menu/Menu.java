package com.example.project_cg.menu;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.project_cg.MainActivity;
import com.example.project_cg.R;
import com.example.project_cg.asynctask.Screenshot;
import com.example.project_cg.util.ScreenShotUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class Menu {
    private Activity activity;
    private boolean isOpen = false;
    private ArrayList<View> menuList = new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    public Menu(Activity activity) {
        View v = activity.findViewById(R.id.img_1);
        v.setVisibility(View.GONE);
        v.setOnClickListener(notUsed -> {
            View view = activity.findViewById(R.id.lightRecycler);
            if (view.getVisibility() == View.GONE) {
                activity.findViewById(R.id.objectRecycler).setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
            } else view.setVisibility(View.GONE);
        });
        menuList.add(v);

        v = activity.findViewById(R.id.img_2);
        v.setOnClickListener(notUsed -> {
            new Screenshot() {
                @Override
                protected void onPostExecute(MainActivity mainActivity) {
                    super.onPostExecute(mainActivity);
                    Toast.makeText(activity, "Save as " + ScreenShotUtil.path, Toast.LENGTH_SHORT).show();
                }
            }.execute(activity);
        });
        v.setVisibility(View.GONE);
        menuList.add(v);

        v = activity.findViewById(R.id.img_3);
        v.setVisibility(View.GONE);
        v.setOnClickListener(notUsed -> {
            View view = activity.findViewById(R.id.objectRecycler);
            if (view.getVisibility() == View.GONE) {
                activity.findViewById(R.id.lightRecycler).setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
            } else view.setVisibility(View.GONE);
        });
        menuList.add(v);

        this.activity = activity;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public ArrayList<View> getMenuList() {
        return menuList;
    }

}
