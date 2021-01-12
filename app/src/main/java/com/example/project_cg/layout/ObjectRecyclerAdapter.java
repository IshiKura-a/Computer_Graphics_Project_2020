package com.example.project_cg.layout;

import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_cg.R;
import com.example.project_cg.shape.Shape;
import com.example.project_cg.shape.ShapeType;
import com.example.project_cg.util.FontUtil;

import java.util.ArrayList;
import java.util.LinkedList;

public class ObjectRecyclerAdapter extends RecyclerView.Adapter<ObjectRecyclerAdapter.RecyclerViewHolder> {
    private ObjectOnItemClickListener mItemClickListener;
    private LinkedList<Shape> mShapes;

    public ObjectRecyclerAdapter(LinkedList<Shape> shapes) {
        mShapes = shapes;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private final TextView objectIndex;
        private final ImageView objectIcon;
        private final View contentView;
        private final CheckBox isChosen;

        public RecyclerViewHolder(View v) {
            super(v);
            contentView = v;
            objectIcon = v.findViewById(R.id.objectIcon);
            objectIndex = v.findViewById(R.id.objectIndex);
            isChosen = v.findViewById(R.id.isChosen);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        public void onBind(int position, Shape shape, View v) {
            if(shape.getType() == ShapeType.CUBE) {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.cube));
            }
            else if(shape.getType() == ShapeType.MODEL) {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.model));
            }
            else if(shape.getType() == ShapeType.BALL) {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.ball));
            }
            else if(shape.getType() == ShapeType.CONE) {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.cone));
            }
            else if(shape.getType() == ShapeType.CYLINDER) {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.cylinder));
            }
            else if(shape.getType() == ShapeType.PRISM) {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.prism));
            }
            else if(shape.getType() == ShapeType.PYRAMID) {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.pyramid));
            }
            else if(shape.getType() == ShapeType.FRUSTUM) {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.frustum));
            }
            else {
                objectIcon.setImageDrawable(v.getResources().getDrawable(R.drawable.shape));
            }

            objectIndex.setText("#"+position+" "+shape.getType().getName());
            objectIndex.setTypeface(FontUtil.gillSans);
            isChosen.setOnCheckedChangeListener((btn, flag) -> {
                shape.setChosen(flag);
            });
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

    public void setOnItemClickListener(ObjectOnItemClickListener listener) {
        mItemClickListener = listener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i("TAG","add");
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.object_item, parent, false));
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.onBind(position, mShapes.get(position), holder.contentView);
        holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemCLick(position, mShapes.get(position));
                }
            }
        });
        holder.setOnLongClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onItemLongCLick(position, mShapes.get(position));
            }
            return true;
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mShapes.size();
    }

    public interface ObjectOnItemClickListener {

        void onItemCLick(int position, Shape shape);

        void onItemLongCLick(int position, Shape shape);
    }

    public void remove(int position) {
        if (null != mShapes && mShapes.size() > position) {
            mShapes.remove(position);
            notifyItemRemoved(position);
            if (position != mShapes.size()) {
                //刷新改变位置item下方的所有Item的位置,避免索引错乱
                notifyItemRangeChanged(position, mShapes.size() - position);
            }
        }
    }

    public void add(int position) {
        notifyItemInserted(position);
        if (position != mShapes.size()) {
            //刷新改变位置item下方的所有Item的位置,避免索引错乱
            notifyItemRangeChanged(position, mShapes.size() - position);
        }
    }
}