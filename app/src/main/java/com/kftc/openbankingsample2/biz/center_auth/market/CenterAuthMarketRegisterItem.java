package com.kftc.openbankingsample2.biz.center_auth.market;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthHomeFragment;
import com.kftc.openbankingsample2.biz.main.MenuFragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class CenterAuthMarketRegisterItem extends AbstractCenterAuthMainFragment {
    // context
    private Context context;

    // data from previous page
    private Bundle args;

    View view;

    ImageView imageView;
    Bitmap bp;

    EditText etItemName;
    EditText etItemPrice;
    EditText etItemMemo;

    Button btn;

    final static int TAKE_PICTURE = 1;
    final static int UPLOAD_PHOTO = 10;

    private Uri imgUri;

    // firebase storage reference
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // firebase database reference
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private String[] strings;
    private String[] itemInfo;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

        strings = args.getStringArray("key");
        Log.d("strings args", strings[1]);

        // 스토리지 레퍼런스 초기화
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("images");

        // 데이터베이스 레퍼런스 초기화
        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference().child("market_info").child("hanium2020");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_center_auth_market_register_item, container, false);
        setCameraPermission();
        initView();
        return view;
    }

    private void setCameraPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };

        TedPermission.with(context)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA)
                .check();
    }

    void initView() {
        if (strings[0].equals("E")) {
            getItemPicture();
            getItemInfo();
        }

        imageView = (ImageView)view.findViewById(R.id.imgItem);
        registerForContextMenu(view);

        btn = (Button)view.findViewById(R.id.pictureOption);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.showContextMenu();
            }
        });

        etItemName = view.findViewById(R.id.etItemName);
        etItemPrice = view.findViewById(R.id.etItemPrice);
        etItemMemo = view.findViewById(R.id.etItemMemo);

        view.findViewById(R.id.btnRegisterItem).setOnClickListener(v -> imageIntoStorage());

//        view.findViewById(R.id.btnCancel).setOnClickListener(v -> onBackPressed());
        view.findViewById(R.id.btnCancel).setOnClickListener(v -> {
            startFragment(MenuFragment.class, args, R.string.fragment_menu);
        });

    }

    void getItemPicture() {
        StorageReference imgRef = storageRef.child(strings[1]+".jpg");

        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity().getApplicationContext())
                        .load(uri)
                        .into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void getItemInfo() {
        DatabaseReference itemRef = databaseRef.child("Menu").child(strings[1]);

        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("pay-easy", etItemName.getText().toString()+ " info:");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("pay-easy",  snapshot.getKey() + " = " + snapshot.getValue().toString());

                    if (snapshot.getKey().equals("Name"))
                        etItemName.setText(snapshot.getValue().toString());

                    if (snapshot.getKey().equals("Price"))
                        etItemPrice.setText(snapshot.getValue().toString());

                    if (snapshot.getKey().equals("Memo"))
                        etItemMemo.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.menu_camera_file, menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull  MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.chooseCamera) {
            takePictureIntent();
            return true;
        }
        else if (itemId == R.id.chooseFile) {
            uploadPictureIntent();
            return true;
        }

        return false;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String path = image.getAbsolutePath();
        return image;
    }


    private void takePictureIntent() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                imgUri = FileProvider.getUriForFile(context,
                        "com.kftc.openbankingsample2",
                        photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
                startActivityForResult(cameraIntent, TAKE_PICTURE);
            }
        }
    }

    private void uploadPictureIntent() {
        Intent intent = new Intent();
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, UPLOAD_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    bp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgUri);
                    imageView.setImageBitmap(bp);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        else if (requestCode == UPLOAD_PHOTO) {
            if (resultCode == RESULT_OK) {
                try {
                    imgUri = data.getData();
                    bp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imgUri);
                    imageView.setImageBitmap(bp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void imageIntoStorage() {

        if (imgUri != null) {
            //String filename = "menu" + strings[1] + ".jpg";
            String filename = etItemName.getText().toString() + ".jpg";
            StorageReference newImgStorageRef = storageRef.child(filename);

            UploadTask uploadTask = newImgStorageRef.putFile(imgUri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showAlert("실패", "스토리지 업로드 실패");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    showAlert("성공", "스토리지 업로드 성공");
                }
            });

        }
        else {
            String oldfilename = strings[1]+".jpg";
            String filename = etItemName.getText().toString() + ".jpg";
            StorageReference oldImgStorageRef = storageRef.child(oldfilename);
            StorageReference newImgStorageRef = storageRef.child(filename);

            oldImgStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Uri oldimgUri = uri;
                    Log.d("in old uri", oldimgUri.toString());
                    UploadTask uploadTask = newImgStorageRef.putFile(oldimgUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        showAlert("실패", "스토리지 업로드 실패");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        showAlert("성공", "스토리지 업로드 성공");
                    }});
                }
            });

        }

        menuInfoIntoDatabase(etItemName.getText().toString());

    }

    private void menuInfoIntoDatabase(String filename) {


        DatabaseReference newItemRef = databaseRef.child("Menu").child(filename);

        newItemRef.child("Name").setValue(etItemName.getText().toString());
        newItemRef.child("Photo").setValue(filename+".jpg");
        newItemRef.child("Price").setValue(etItemPrice.getText().toString());
        newItemRef.child("Memo").setValue(etItemMemo.getText().toString());

        //databaseRef.child("numberOfItem").setValue(strings[1]);

        goNext();
    }

    public void goNext() {

        if (strings[0].equals("E")) {
            if (!strings[1].equals(etItemName.getText().toString())){
                Log.d("delete!!", strings[1]+","+ etItemName.getText().toString());
                databaseRef.child("Menu").child(strings[1]).removeValue();
            }
        }

        showAlert("알림", "메뉴 등록 완료");
        startFragment(CenterAuthHomeFragment.class, args, R.string.fragment_id_center_auth);
    }
}
