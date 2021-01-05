package com.example.project_cg.layout;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_cg.R;
import com.example.project_cg.observe.Light;
import com.example.project_cg.observe.Observe;
import com.example.project_cg.util.FontUtil;

public class LightRecyclerAdapter extends RecyclerView.Adapter<LightRecyclerAdapter.RecyclerViewHolder> {
    private LightOnItemClickListener mItemClickListener;

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView lightIndex;
        private final ImageView ambient;
        private final ImageView diffuse;
        private final ImageView specular;
        private final View contentView;

        public RecyclerViewHolder(View v) {
            super(v);
            contentView = v;
            lightIndex = v.findViewById(R.id.lightIndex);
            ambient = v.findViewById(R.id.ambient);
            diffuse = v.findViewById(R.id.diffuse);
            specular = v.findViewById(R.id.specular);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onBind(int position, Light light, View v) {
            lightIndex.setText("#"+(position+1));
            lightIndex.setTypeface(FontUtil.gillSans);

            ambient.setBackgroundColor(Color.argb(light.getAmbient()[3],light.getAmbient()[0],light.getAmbient()[1],light.getAmbient()[2]));
            diffuse.setBackgroundColor(Color.argb(light.getDiffuse()[3],light.getDiffuse()[0],light.getDiffuse()[1],light.getDiffuse()[2]));
            specular.setBackgroundColor(Color.argb(light.getSpecular()[3],light.getSpecular()[0],light.getSpecular()[1],light.getSpecular()[2]));
        }

        public void setOnClickListener(View.OnClickListener listener) {
            if (listener != null) {
                contentView.setOnClickListener(listener);
            }
        }

        public void setOnLongClickListener(View.OnLongClickListener listener) {
            if (listener != null) {
                contentView.setOnLongClickListener(listener);
            }
        }
    }


    public void removeData(int position) {
        // todo
    }

    public void setOnItemClickListener(LightOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("TAG","add");
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.light_item, parent, false));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.onBind(position, Observe.getLightList().get(position), holder.contentView);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, Observe.getLightList().get(position));
                }
            }
        });
        holder.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemLongCLick(position, Observe.getLightList().get(position));
                }
                return false;
            }

        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return Observe.getLightList().size();
    }

    public interface LightOnItemClickListener {

        void onItemCLick(int position, Light light);

        void onItemLongCLick(int position, Light light);
    }
}