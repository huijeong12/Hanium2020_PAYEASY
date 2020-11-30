package com.kftc.openbankingsample2.biz.center_auth;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.seller.menu_check.MenuCheckFragment;
import com.kftc.openbankingsample2.biz.center_auth.seller.order_list.ForOrderListFragment;
import com.kftc.openbankingsample2.biz.center_auth.seller.menu_info.MenuInfoFragment;
import com.kftc.openbankingsample2.biz.main.HomeFragment;
import com.kftc.openbankingsample2.biz.center_auth.seller.menu_list.MenuFragment;

import java.util.Random;

/**
 * 센터인증 메인화면
 */
public class CenterAuthHomeFragment extends AbstractCenterAuthMainFragment {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;

    FirebaseDatabase database;
    String items;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

        database = FirebaseDatabase.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center_auth_home, container, false);
        initView();
        return view;
    }

    private void initView() {


        // 메뉴 추가 및 수정
        view.findViewById(R.id.btnRegisterItem).setOnClickListener(v -> editArgs("R"));
        view.findViewById(R.id.btnEditItem).setOnClickListener(v -> editArgs("E"));

        // 메뉴 확인
        view.findViewById(R.id.btnCallMenuCheck).setOnClickListener(v -> startFragment(MenuCheckFragment.class, args, R.string.fragment_id_manu_check));

        // 메뉴판
        view.findViewById(R.id.btnCallMenu).setOnClickListener(v -> startFragment(MenuFragment.class, args, R.string.fragment_menu));

        // 메인 홈
        view.findViewById(R.id.btnOrderMenu).setOnClickListener(v->startFragment(ForOrderListFragment.class,args,R.string.fragment_id_for_order_list));
    }

    void editArgs(String isEditOrRegister) {

        DatabaseReference ref = database.getReference().child("market_info").child("hanium2020").child("numberOfItem");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                items = dataSnapshot.getValue().toString();
                String[] strings = new String[2];
                strings[0] = isEditOrRegister;

                if (isEditOrRegister == "E") {
                    Random random = new Random();
                    items = Integer.toString(random.nextInt(Integer.valueOf(items)) + 1);
                }

                else {
                    items = Integer.toString(Integer.valueOf(items) + 1);
                }
                strings[1] = items;

                args.putStringArray("key", strings);
                startFragment(MenuInfoFragment.class, args, R.string.fragment_id_register_item);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        startFragment(HomeFragment.class, args, R.string.fragment_id_home);
    }
}
