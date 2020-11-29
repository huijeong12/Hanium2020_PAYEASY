package com.kftc.openbankingsample2.biz.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;

import java.util.ArrayList;

public class MenuFragment extends AbstractCenterAuthMainFragment implements OnItemClick{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<menuList> arrayList;
    private ArrayList<Integer> selected;

    private View view;

    public MenuFragment() {
    }

    @Override
    public void onClick(ArrayList<Integer> value) {
        this.selected = value;
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
                    Log.d("where", "in addvalue");

                    menuList menus = new menuList();

                    String price = snapshot.child("Price").getValue().toString();
                    String name = snapshot.child("Name").getValue().toString();
                    String photo = snapshot.child("Photo").getValue().toString();

                    StorageReference imgRef = storageRef.child(photo);

                    menus.setMenuName(name);
                    menus.setPrice(price);
                    menus.setProfile(imgRef);

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
        adapter = new MenuAdapter(arrayList, getContext(), this);
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
                            TextView tv_menu = item.findViewById(R.id.menuName);
                            String s_menu = tv_menu.getText().toString();

                            myRef.child("hanium2020").child("Menu").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot data: snapshot.getChildren()) {
                                        String s = data.child("Name").getValue().toString();
                                        if (s == s_menu) {
                                            data.getRef().removeValue();
                                            selected.remove(s);
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
        return view;
    }

}