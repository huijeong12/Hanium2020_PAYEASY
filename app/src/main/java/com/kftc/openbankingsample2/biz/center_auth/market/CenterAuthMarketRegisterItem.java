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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthHomeFragment;

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

    final static int TAKE_PICTURE = 1;
    final static int UPLOAD_PHOTO = 10;

    private String path;
    private Uri imgUri;

    // firebase storage reference
    private FirebaseStorage storage;
    private StorageReference storageRef;

    // firebase database reference
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    String items;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

        items = args.getString("items");

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
        Log.d("pay-easy", "items = " + items);
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

        imageView = (ImageView)view.findViewById(R.id.imgItem);

        view.findViewById(R.id.btnTakePicture).setOnClickListener(v -> takePictureIntent());

        view.findViewById(R.id.btnUploadPicture).setOnClickListener(v -> uploadPictureIntent());

        etItemName = view.findViewById(R.id.etItemName);
        etItemPrice = view.findViewById(R.id.etItemPrice);
        etItemMemo = view.findViewById(R.id.etItemMemo);

        view.findViewById(R.id.btnRegisterItem).setOnClickListener(v -> imageIntoStorage());

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> onBackPressed());

    }

    private File createFile() {
        String fileName = "menu" + items + ".jpg";
        File imageDir = context.getDir("profileImages", Context.MODE_PRIVATE);

        return new File(imageDir, fileName);
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
        path = image.getAbsolutePath();
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
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void imageIntoStorage() {

        String filename = "menu" + items + ".jpg";
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


        menuInfoIntoDatabase(filename);
    }

    private void menuInfoIntoDatabase(String filename) {

        DatabaseReference newItemRef = databaseRef.child("Menu").child("menu" + items);

        newItemRef.child("Name").setValue(etItemName.getText().toString());
        newItemRef.child("Photo").setValue(filename);
        newItemRef.child("Price").setValue(etItemPrice.getText().toString());
        newItemRef.child("Memo").setValue(etItemMemo.getText().toString());

        databaseRef.child("numberOfItem").setValue(items);

        goNext();
    }

    public void goNext() {
        showAlert("알림", "메뉴 등록 완료");
        startFragment(CenterAuthHomeFragment.class, args, R.string.fragment_id_center_auth);
    }
}
