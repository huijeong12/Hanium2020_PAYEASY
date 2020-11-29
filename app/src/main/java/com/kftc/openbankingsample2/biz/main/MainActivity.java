package com.kftc.openbankingsample2.biz.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

        // firebase - cloud storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        // read image from storage
        StorageReference imagesRef = storageReference.child("images");

        String fileName = "test.png";
        StorageReference testRef = imagesRef.child(fileName);

        if (testRef != null) {
            String path = testRef.getPath();
            String name = testRef.getName();


            Log.d("pay-easy", "File path is: " + path + " \tFile name is: " + name);
        }

        // write image into storage

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
