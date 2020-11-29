package com.kftc.openbankingsample2.biz.center_auth.seller.order_list;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.seller.menu_check.MenuCheckFragment;
import com.kftc.openbankingsample2.biz.center_auth.seller.menu_list.MenuFragment;
import com.kftc.openbankingsample2.common.util.view.recyclerview.KmRecyclerViewDividerHeight;

import java.util.ArrayList;

//order List에 뜨게 될 메뉴들

public class ForOrderListFragment extends AbstractCenterAuthMainFragment {
    //
    private Context context;

    private View view;
    private RecyclerView recyclerView;
    private ForOrderListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList <ForOrderListItem> mList =new ArrayList<ForOrderListItem>();

    //data
    private Bundle args;


    //생성자
   public ForOrderListFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();

        if (args == null) args = new Bundle();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveInstanceState){
        view = inflater.inflate(R.layout.fragment_for_order_list,container,false);
        initData();
        return view;
    }



    /////////////////
    //디비에서 메뉴들을 받아와야하는데..어떻게 받아와야할지..고민입니다요.........이렇게 하면 안될거같은뎅 ㅇㅁㅇ
    ////////////////



    void initData() {

       view.findViewById(R.id.makeQRcode).setOnClickListener(v->{

           int menuCnt = adapter.getItemCount();
           Log.d("Menu count: ", Integer.toString(menuCnt));

           String menuForArgs = "";
           int valid=0;

           for (int i = 0; i < menuCnt; i++){
               View menuItem = recyclerView.getLayoutManager().findViewByPosition(i);
               EditText menuCount = menuItem.findViewById(R.id.orderMenuCount);
               TextView menuName = menuItem.findViewById(R.id.orderMenuName);
               TextView menuPrice = menuItem.findViewById(R.id.orderMenuPrice);
               String menuCount_s = menuCount.getText().toString();
               String menuName_s = menuName.getText().toString();
               String menuPrice_s = menuPrice.getText().toString();

               if (!menuCount_s.equals("0")){
                   Log.d("name, price, count: ", menuName_s + ", " + menuPrice_s + ", " + menuCount_s);
                   valid++;

                   if (i == menuCnt-1){
                       menuForArgs += menuName_s + " " + menuPrice_s + " " + menuCount_s;
                   }
                   else {
                       menuForArgs += menuName_s + " " + menuPrice_s + " " + menuCount_s + "#";
                   }
               }
           }

           if (valid==0) {
               Toast.makeText(context, "주문을 입력해주세요.", Toast.LENGTH_LONG).show();
               return;
           }
           Log.d("menuForArgs: ", menuForArgs);
            args.putString("menuForArgs", menuForArgs);

           startFragment(MenuCheckFragment.class, args, R.string.fragment_id_manu_check);
       });

       view.findViewById(R.id.btnChangemenuPage).setOnClickListener(v->{
           startFragment(MenuFragment.class, args, R.string.fragment_menu);
       });
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new KmRecyclerViewDividerHeight(10));


        layoutManager = new LinearLayoutManager(getContext());

        mList =new ArrayList<>();

        FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("market_info");


        myRef.child("hanium2020").child("Menu").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mList.clear();
                for(DataSnapshot snapshot:datasnapshot.getChildren()){
                    ForOrderListItem items = new ForOrderListItem();

                    String price = snapshot.child("Price").getValue().toString();
                    String name = snapshot.child("Name").getValue().toString();

                    items.setCount("0");
                    items.setPrice(price);
                    items.setName(name);

                    mList.add(items);

                    Object value =snapshot.getValue();
                    Log.d("price value: ", value.toString());

                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w("pay-easy", "Failed to read value.", error.toException());
            }
        });


        // 리사이클러뷰에 어댑터 설정
        adapter = new ForOrderListAdapter(mList, new ForOrderListAdapter.MyAdapterListener() {
            @Override
            public void plusBtnOnclick(View v, int position) {
                Log.d("ming", "plus button on clicked");
                EditText editText;
                View menuItem = recyclerView.getLayoutManager().findViewByPosition(position);
                editText = menuItem.findViewById(R.id.orderMenuCount);

                String str = editText.getText().toString();
                Log.d("edittext", str);
                int menuCountText=Integer.parseInt(str);

                menuCountText++;
                str = Integer.toString(menuCountText);
                editText.setText(str);

            }

            @Override
            public void minusBtnOnclick(View v, int position) {
                Log.d("ming", "minus button on clicked");
                EditText editText;
                View menuItem = recyclerView.getLayoutManager().findViewByPosition(position);
                editText = menuItem.findViewById(R.id.orderMenuCount);

                String str = editText.getText().toString();
                Log.d("edittext", str);
                int menuCountText=Integer.parseInt(str);

                if (menuCountText == 0){
                    return;
                }
                menuCountText--;
                str = Integer.toString(menuCountText);
                editText.setText(str);
            }
        });
        recyclerView.setAdapter(adapter);

    }

    void goNext() {
        //startFragment(Fragment.class, null, R.string.fragment_id_center_api_call);
    }
}
