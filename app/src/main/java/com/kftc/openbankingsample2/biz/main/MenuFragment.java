package com.kftc.openbankingsample2.biz.main;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.ForOrderListAdapter;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.market.CenterAuthMarketRegisterItem;

import java.util.ArrayList;
import java.util.Random;

public class MenuFragment extends AbstractCenterAuthMainFragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<menuList> arrayList;

    FirebaseDatabase database;
    String items;
    private Bundle args;

    private View view;


    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        args = getArguments();

        if (args == null) args = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.list_menu);
        //recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)

        Button btn_delete=(Button) view.findViewById(R.id.button_delete);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("market_info");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        myRef.child("hanium2020").child("Menu").addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    menuList menus = new menuList();
                    String price = "";
                    String photo = "";
                    if (snapshot.child("Price").getValue() != null) {
                        price = snapshot.child("Price").getValue().toString();
                    }
                    String name = snapshot.child("Name").getValue().toString();
                    if (snapshot.child("Photo").getValue() != null) {
                        photo = snapshot.child("Photo").getValue().toString();
                    }

                    menus.setMenuName(name);
                    menus.setPrice(price);
                    menus.setProfile(photo);

                    arrayList.add(menus);

                    Object value = snapshot.getValue();
                    Log.d("price value: ", value.toString());
                }
                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침해야 반영이 됨
            }

            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pay-easy", "Failed to read value.", error.toException());
            }
        });
        //adapter = new MenuAdapter(arrayList, getContext());

        adapter = new MenuAdapter(arrayList, getContext(), new MenuAdapter.MyAdapterListener() {
            @Override
            public void textViewOnclick(View v, int position) {
                Log.d("ming", Integer.toString(position) + "textView on clicked");
                TextView textView;
                View menuList = recyclerView.getLayoutManager().findViewByPosition(position);
                textView = menuList.findViewById(R.id.menuNameText);
                String menuTextName = textView.getText().toString();
                Log.d("menuNameText", menuTextName);
                editArgs("E", menuTextName);
                //startFragment();
            }

        });
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어댑터 연결

        btn_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        view.findViewById(R.id.button_add).setOnClickListener(v->editArgs("R", ""));

        return view;
    }


    void editArgs(String isEditOrRegister, String positionName) {

        String[] strings = new String[2];
        strings[0] = isEditOrRegister;

        strings[1] = positionName;

        args.putStringArray("key", strings);
        startFragment(CenterAuthMarketRegisterItem.class, args, R.string.fragment_id_register_item);

        };

}