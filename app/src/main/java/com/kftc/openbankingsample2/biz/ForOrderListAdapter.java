package com.kftc.openbankingsample2.biz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kftc.openbankingsample2.R;

import java.util.ArrayList;

public class ForOrderListAdapter extends RecyclerView.Adapter<ForOrderListAdapter.ViewHolder> {

    private Context context;
    private View view;

    public MyAdapterListener onClickListener;


    public interface MyAdapterListener {
        void plusBtnOnclick(View v, int position);
        void minusBtnOnclick(View v, int position);
    }

    private ArrayList<ForOrderListItem> mData = null;

    //생성자
    public ForOrderListAdapter(ArrayList<ForOrderListItem> list,MyAdapterListener listener){
        this.mData = list;
        onClickListener=listener;

    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView menuPrice ;
        TextView menuCount ;
        TextView menuName ;

        public Button plusBtn;
        public Button minusBtn;

        ViewHolder(View itemView) {
            super(itemView) ;

            // 뷰 객체에 대한 참조. (hold strong reference)
            menuPrice =itemView.findViewById(R.id.orderMenuPrice) ;
            menuCount = itemView.findViewById(R.id.orderMenuCount) ;
            menuName = itemView.findViewById(R.id.orderMenuName) ;

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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context =parent.getContext();
        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view =inflater.inflate(R.layout.adapter_for_order_list,parent,false);
        ForOrderListAdapter.ViewHolder viewHolder = new ForOrderListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ForOrderListItem item = mData.get(position);

        holder.menuCount.setText(item.getCount());
        holder.menuName.setText(item.getName());
        holder.menuPrice.setText(item.getPrice());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }



}