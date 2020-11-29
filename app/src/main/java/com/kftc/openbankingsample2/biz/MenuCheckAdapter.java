package com.kftc.openbankingsample2.biz;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kftc.openbankingsample2.R;

import java.util.ArrayList;


public class MenuCheckAdapter extends RecyclerView.Adapter<MenuCheckAdapter.ViewHolder> {

    private Context context;
    private View view;

    public MyAdapterListener onClickListener;

    public interface MyAdapterListener {
        void plusBtnOnclick(View v, int position);
        void minusBtnOnclick(View v, int position);
    }


    private ArrayList<MenuCheckItem> mData = null;

    public MenuCheckAdapter(ArrayList<MenuCheckItem> list, MyAdapterListener listener) {
        this.mData = list;
        onClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView menuInfo;
        TextView menuPrice;
        EditText menuCount;

        public Button plusBtn;
        public Button minusBtn;

        ViewHolder(View itemView){
            super(itemView);

            menuInfo =itemView.findViewById(R.id.orderMenuInfo);
            menuPrice = itemView.findViewById(R.id.orderMenuPrice);
            menuCount = itemView.findViewById(R.id.orderMenuCount);

            plusBtn = itemView.findViewById(R.id.orderMenuPlusBtn);
            minusBtn = itemView.findViewById(R.id.orderMenuMinusBtn);

            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.plusBtnOnclick(view, getAdapterPosition());
                }
            });

            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.minusBtnOnclick(view, getAdapterPosition());
                }
            });
        }
    }

    @NonNull
    @Override
    public MenuCheckAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.adapter_menu_check, parent, false);
        MenuCheckAdapter.ViewHolder vh = new MenuCheckAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCheckAdapter.ViewHolder holder, int position) {
        MenuCheckItem item = mData.get(position);

        holder.menuCount.setText(item.getCount());
        holder.menuPrice.setText(item.getPrice());
        holder.menuInfo.setText(item.getInfo());

    }
//
//
//    public void plusBtnOnclick(View v, int position){
//        EditText count = view.findViewById(R.id.orderMenuCount);
//        String counts = count.getText().toString();
//        int counti = Integer.parseInt(counts);
//
//        counti++;
//
//          counts = Integer.toString(counti);
//          count.setText(counts);
//    }
//
//    public void minusBtnOnClick() {
//
//        Toast.makeText(context, "minus", Toast.LENGTH_LONG);
//        EditText count = view.findViewById(R.id.orderMenuCount);
//        String counts = count.getText().toString();
//        int counti = Integer.parseInt(counts);
//
//        if (counti == 0) {
//            return;
//        }
//
//        counti--;
//
//        counts = Integer.toString(counti);
//        count.setText(counts);
//    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

    public EditText editMenuCount(){
        return view.findViewById(R.id.orderMenuCount);
    }
}