package com.kftc.openbankingsample2.biz.center_auth.seller.menu_check;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.seller.create_qrcode.CreateQRcodeFragment;
import com.kftc.openbankingsample2.common.data.ApiCallUserMeResponse;
import com.kftc.openbankingsample2.common.util.view.recyclerview.KmRecyclerViewDividerHeight;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuCheckFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuCheckFragment extends AbstractCenterAuthMainFragment {

    // context
    private Context context;

    // view
    private View view;
    private RecyclerView recyclerView;
    private MenuCheckAdapter adapter;
    ArrayList<MenuCheckItem> mList = new ArrayList<MenuCheckItem>();

    // data
    private Bundle args;
    private ApiCallUserMeResponse result;
    String menuData;
    int total_price=0;

    public MenuCheckFragment() {
        // Required empty public constructor
    }


    public static MenuCheckFragment newInstance(String param1, String param2) {
        MenuCheckFragment fragment = new MenuCheckFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        menuData = args.getString("menuForArgs");

        if (args == null) args = new Bundle();

        //result = args.getParcelable("result");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_menu_check, container, false);
        initView();
        return view;
    }

    void initView(){
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new KmRecyclerViewDividerHeight(10));

        view.findViewById(R.id.btnNext).setOnClickListener(v -> goNext());

        initList();
        initData();

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> onBackPressed());
        view.findViewById(R.id.btnNext).setOnClickListener(v->goNext());

    }

    void initList(){
        // mList 할당
        // addItem(getDrawable(R.drawable.ic_account_box_black_36dp), "Box", "Account Box Black 36dp") ;
        // args로 데이터 받고, 그 데이터 정리해서 mList에 넣기
        String[] array = menuData.split("#");

        for (int i = 0; i<array.length; i++){
            String[] array2 = array[i].split(" ");
            addItem(array2[0], array2[1], array2[2]);
        }
    }

    public void addItem(String info, String price, String count) {
        MenuCheckItem item = new MenuCheckItem();

        item.setInfo(info);
        item.setPrice(price);
        item.setCount(count);

        mList.add(item);
    }

    void initData() {

        // 리사이클러뷰에 어댑터 설정
        adapter = new MenuCheckAdapter(mList, new MenuCheckAdapter.MyAdapterListener() {
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

        for (int i = 0; i < adapter.getItemCount(); i++){
            View menuItem = recyclerView.getLayoutManager().findViewByPosition(i);
            EditText editText = menuItem.findViewById(R.id.orderMenuCount);
            TextView textView = menuItem.findViewById(R.id.orderMenuPrice);
            String cntString = editText.getText().toString();
            String priceString = textView.getText().toString();

            int cntInt = Integer.parseInt(cntString);

            priceString = priceString.substring(0, priceString.length() - 1);

            int priceInt = Integer.parseInt(priceString);

            total_price += cntInt*priceInt;
        }
        String total_Price_String = Integer.toString(total_price);
        args.putString("total_price_arg", total_Price_String);
        startFragment(CreateQRcodeFragment.class, args, R.string.fragment_create_qrcode_to_withdraw);


    }
}