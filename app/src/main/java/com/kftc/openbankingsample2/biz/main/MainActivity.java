package com.kftc.openbankingsample2.biz.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kftc.openbankingsample2.R;

/**
 * 메인 Activity. 모든 프래그먼트는 이 메인 activity를 안에서 구현된다.
 */
public class MainActivity extends AbstractMainActivity {

    // context
    private Context context;

    // data
    private Bundle args;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        args = getIntent().getExtras();
        if (args == null) args = new Bundle();

        initView();

        // firebase - realtime database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        // database write test
        myRef.setValue("Hello, World!");

        // database read test
        myRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("pay-easy", "Value is: " + value);
            }

            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("pay-easy", "Failed to read value.", error.toException());
            }
        });
    }

    private void initView() {
        initData();
    }

    private void initData() {
        goNext();
    }

    private void goNext() {
        startFragment(MainFragment.class, args, R.string.fragment_id_main);
    }
}
