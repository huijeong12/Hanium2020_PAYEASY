package com.kftc.openbankingsample2.biz.center_auth.seller.menu_list;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kftc.openbankingsample2.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {

    private ArrayList<menuList> arrayList;
    private Context context;

    String imgName;

    FirebaseStorage storage;
    StorageReference storageRef ;

    public MenuAdapter(ArrayList<menuList> arrayList, Context context) {
        this.arrayList = arrayList;
        //this.context = context;
    }

    @NonNull
    @Override
    public MenuAdapter.MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_menu_item, parent, false);
        MenuViewHolder holder = new MenuViewHolder(itemView);

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("images");

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.MenuViewHolder holder, int position) {

        holder.cb_menuName.setText(arrayList.get(position).getMenuName());
        holder.tv_price.setText(arrayList.get(position).getPrice());

        imgName = arrayList.get(position).getProfile();
        Log.d("getProfile() value: ", imgName);
        StorageReference imgRef = storageRef.child(imgName);

        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .into(holder.iv_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {
        Context vh_context = context;
        ImageView iv_profile;
        CheckBox cb_menuName;
        TextView tv_price;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile = itemView.findViewById(R.id.profile);
            cb_menuName = itemView.findViewById(R.id.menuName);
            tv_price = itemView.findViewById(R.id.price);
        }


    }
}