package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button btnInsert, btnUpload;
    private EditText etNama, etDeskripsi;
    private ImageView ImageView;
    private TextView tv_showAll;

    private Uri ImageUri;
    private int isComplete = 0;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask uploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnInsert = findViewById(R.id.btn_choose);
        btnUpload = findViewById(R.id.uploadbtn);
        etNama = findViewById(R.id.et_nama);
        etDeskripsi = findViewById(R.id.et_deskripsi);
        ImageView = findViewById(R.id.imageView);
        tv_showAll = findViewById(R.id.tvShowAll);

        storageReference = FirebaseStorage.getInstance().getReference("picture");
        databaseReference = FirebaseDatabase.getInstance().getReference("data-barang");

        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (isComplete == 1)  {
                   Toast.makeText(MainActivity.this, "Upload in Progress", Toast.LENGTH_SHORT).show();
               } else {
                   uploadFile();
               }
            }
        });

        tv_showAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllImage();
            }
        });
    }

    private void openAllImage() {
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

    //mengambil extensi dari image yang di insert
    private String getFileExtension(Uri uri) {
        ContentResolver Cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(Cr.getType(uri));
    }

    private void uploadFile() {
        if (ImageUri != null) {
            isComplete = 1;
            //Menyimpan gambar di Storage Firebase dengan unique name
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(ImageUri));
            uploadTask = fileReference.putFile(ImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(MainActivity.this, "Upload Success", Toast.LENGTH_LONG).show();
//                            Barang barang = new Barang(etNama.getText().toString(), etDeskripsi.getText().toString(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//
//                            //Memasukan Data baru ke Database dengan Unique Key
//                            String uploadId = databaseReference.push().getKey();
//                            databaseReference.child(uploadId).setValue(barang);
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                                Uri downloadUri = uriTask.getResult();


                            Barang barang = new Barang(etNama.getText().toString(), etDeskripsi.getText().toString(), downloadUri.toString());
                            String uploadId = databaseReference.push().getKey();
                            databaseReference.child(uploadId).setValue(barang);
                            if (uploadTask.isSuccessful()) {
                                etDeskripsi.setText("");
                                etNama.setText("");
                                ImageView.setImageResource(0);
                                isComplete = 0;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }

    }

    //membuka file untuk memilih gambar
    private void openFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            ImageUri = data.getData();

            Picasso.with(this).load(ImageUri).into(ImageView);
        }
    }
}