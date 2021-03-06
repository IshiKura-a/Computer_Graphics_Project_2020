package com.example.project_cg.menu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.project_cg.MainActivity;
import com.example.project_cg.R;
import com.example.project_cg.dialog.LightDialog;
import com.example.project_cg.dialog.ShapeDialog;
import com.example.project_cg.asynctask.ExportObj;
import com.example.project_cg.asynctask.Screenshot;
import com.example.project_cg.util.ExportObjUtil;
import com.example.project_cg.util.RequestUtil;
import com.example.project_cg.util.ScreenShotUtil;

import java.util.ArrayList;

public class Menu {
    private AlertDialog alertDialog;
    private AlertDialog editShape;
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

        v = activity.findViewById(R.id.img_4);
        v.setVisibility(View.GONE);
        v.setOnClickListener(notUsed -> {
            new ExportObj(){
                @Override
                protected void onPostExecute(Void aVoid) {
                    Toast.makeText(activity, ExportObjUtil.path.isEmpty() ?
                            "Please select some objectes first!" :
                            "Save as " + ExportObjUtil.path, Toast.LENGTH_SHORT).show();
                    super.onPostExecute(aVoid);
                }
            }.execute(activity);
        });
        menuList.add(v);

        v = activity.findViewById(R.id.img_5);
        v.setVisibility(View.GONE);
        v.setOnClickListener(notUsed -> {
            final String[] items = {"Shape", "Light", "Texture", "Model"};

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(activity);
            alertBuilder.setTitle("Create");
            alertBuilder.setSingleChoiceItems(items, 0, null);

            alertBuilder.setPositiveButton("Confirm", (dialog, which) -> {
                ListView listView = alertDialog.getListView();
                int position = listView.getCheckedItemPosition();
                String res = (String) listView.getAdapter().getItem(position);
                Toast.makeText(activity, "Create " + res, Toast.LENGTH_SHORT).show();

                if (res.compareTo("Shape") == 0) {
                    ShapeDialog.displayDialog(activity);
                } else if (res.compareTo("Model") == 0) {
                    loadModel();
                }
                else if(res.compareTo("Texture") == 0) {
                    loadTexture();
                }
                else if(res.compareTo("Light") == 0) {
                    LightDialog.displayDialog(activity);
                }
            });

            alertBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> alertDialog.dismiss());

            alertDialog = alertBuilder.create();
            alertDialog.setIcon(activity.getResources().getDrawable(R.drawable.create));
            alertDialog.show();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            alertDialog.getWindow().setLayout(displayMetrics.widthPixels / 2, (int) (displayMetrics.heightPixels / 1.3f));
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

    private void loadModel() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        try {
            activity.startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"), RequestUtil.REQUEST_OBJ);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(activity, "Please Install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadTexture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        try {
            activity.startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"), RequestUtil.REQUEST_PNG);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(activity, "Please Install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }
}
