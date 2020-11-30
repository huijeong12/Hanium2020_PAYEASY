package com.kftc.openbankingsample2.biz.center_auth.seller.menu_list;

import android.app.AlertDialog;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.seller.order_list.ForOrderListAdapter;
import com.kftc.openbankingsample2.biz.center_auth.seller.menu_info.MenuInfoFragment;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.seller.menu_list.MenuAdapter;
import com.kftc.openbankingsample2.biz.center_auth.seller.menu_list.menuList;
import com.kftc.openbankingsample2.biz.main.OnItemClick;

import java.util.ArrayList;
import java.util.Random;

public class MenuFragment extends AbstractCenterAuthMainFragment implements OnItemClick {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<menuList> arrayList;
    private ArrayList<Integer> selected;

    FirebaseDatabase database;
    String items;
    private Bundle args;

    private View view;

    public MenuFragment() {
    }

    @Override
    public void onClick(ArrayList<Integer> value) {
        this.selected = value;
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
        // recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // User 객체를 담을 어레이 리스트 (어댑터쪽으로)

        Button btn_delete=(Button) view.findViewById(R.id.button_delete);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("market_info");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("images");

        myRef.child("hanium2020").child("Menu").addValueEventListener(new ValueEventListener() {
            @Override
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

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pay-easy", "Failed to read value.", error.toException());
            }
        });
        // adapter = new MenuAdapter(arrayList, getContext(), this);
        //adapter = new MenuAdapter(arrayList, getContext());

        adapter = new MenuAdapter(arrayList, getContext(), this, new MenuAdapter.MyAdapterListener() {
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
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setTitle("메뉴 삭제");
                ad.setMessage("선택하신 메뉴를 삭제하시겠습니까?");

                ad.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int s : selected) {
                           Log.d("delete", "position " + s);
                            View item = recyclerView.getLayoutManager().findViewByPosition(s);
                            TextView tv_menu = item.findViewById(R.id.menuNameText);
                            String s_menu = tv_menu.getText().toString();
                            Log.d("s_menu", "value - "+s_menu);

                            myRef.child("hanium2020").child("Menu").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot data: snapshot.getChildren()) {
                                        String s = data.child("Name").getValue().toString();
                                        if (s == s_menu) {
                                            data.getRef().removeValue();
                                            storageRef.child(s_menu+".jpg").delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("storage", "delete success");
                                                    Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_LONG).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("storage", "delete fail");
                                                    Toast.makeText(getContext(), "삭제 실패", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                       }
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
            }
        });
        view.findViewById(R.id.button_add).setOnClickListener(v->editArgs("R",""));
        return view;
    }

    void editArgs(String isEditOrRegister, String positionName) {

        String[] strings = new String[2];
        strings[0] = isEditOrRegister;

        strings[1] = positionName;

        args.putStringArray("key", strings);
        startFragment(MenuInfoFragment.class, args, R.string.fragment_id_register_item);

//        DatabaseReference ref = database.getReference().child("market_info").child("hanium2020").child("numberOfItem");
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                items = dataSnapshot.getValue().toString();
//                String[] strings = new String[2];
//                strings[0] = isEditOrRegister;
//
//                if (isEditOrRegister == "E") {
//                    Random random = new Random();
//                    items = Integer.toString(random.nextInt(Integer.valueOf(items)) + 1);
//
//                }
//
//                else {
//                    items = Integer.toString(Integer.valueOf(items) + 1);
//                }
//                strings[1] = items;
//
//                args.putStringArray("key", strings);
//                startFragment(CenterAuthMarketRegisterItem.class, args, R.string.fragment_id_register_item);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
        };

}