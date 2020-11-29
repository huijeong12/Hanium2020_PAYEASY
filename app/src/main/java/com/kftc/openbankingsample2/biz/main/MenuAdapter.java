package com.kftc.openbankingsample2.biz.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.kftc.openbankingsample2.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private ArrayList<menuList> arrayList;
    private ArrayList<Integer> menu_selected = new ArrayList<Integer>();
    private Context context;
    private OnItemClick mCallback;

    public MenuAdapter(ArrayList<menuList> arrayList, Context context, OnItemClick listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.mCallback = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
    }

    private OnItemClickListener mListener=null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }

    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_item, parent, false);
        MenuViewHolder holder = new MenuViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MenuViewHolder holder, int position) {
        Glide.with(context)
                .load("images/menu1.jpg")
                .into(holder.iv_profile);
        holder.cb_menuName.setText(arrayList.get(position).getMenuName());
        holder.tv_price.setText(arrayList.get(position).getPrice()+"원");
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        CheckBox cb_menuName;
        TextView tv_price;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_profile = itemView.findViewById(R.id.profile);
            this.cb_menuName = itemView.findViewById(R.id.menuName);
            this.tv_price = itemView.findViewById(R.id.price);

            this.cb_menuName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cb_menuName.isChecked()){
                        int pos = getAdapterPosition();
                        Log.d("check", "position " + pos);
                        if(pos!=RecyclerView.NO_POSITION)
                            menu_selected.add(pos);
                    }
                }
            });

            mCallback.onClick(menu_selected);
        }
    }

}