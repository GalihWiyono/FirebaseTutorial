package com.example.firebaseimage;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateBarang extends AppCompatActivity {

    private EditText id, nama, deskripsi;
    private ImageView img;
    private Button btnDelete, btnUpdate;

    private static final int PICK_IMAGE_REQUEST = 1;

    private DatabaseReference databaseReference;

    private String url;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_barang);

        id = findViewById(R.id.editid);
        nama = findViewById(R.id.editNama);
        deskripsi = findViewById(R.id.editDeskripsi);
        img = findViewById(R.id.imgView);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);

        Intent intent = getIntent();
        id.setText(intent.getStringExtra("id"));
        nama.setText(intent.getStringExtra("nama"));
        deskripsi.setText(intent.getStringExtra("deksripsi"));
        url = intent.getStringExtra("url");

        Picasso.with(this).load(url).into(img);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(id.getText().toString());
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFile();
            }
        });
    }

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
            uri = data.getData();
        }

        Picasso.with(this).load(uri).into(img);
    }

    private String getUriImage(Uri uri) {
        ContentResolver Cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(Cr.getType(uri));
    }

    private void updateData(String idBarang) {
        if (!uri.equals(url)) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("picture").child(System.currentTimeMillis() + "." + getUriImage(uri));
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(UpdateBarang.this, "Image Updated", Toast.LENGTH_SHORT).show();
                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    Uri downloadUri = uriTask.getResult();
                    updateInfo(downloadUri.toString(), idBarang);
                }
            });
        } else {
            updateInfo(url, idBarang);
        }
    }

    private void updateInfo(String urlGambar, String idBarang) {
        databaseReference = FirebaseDatabase.getInstance().getReference("data-barang").child(idBarang);
        Barang brg = new Barang(idBarang, nama.getText().toString(), deskripsi.getText().toString(), urlGambar);
        databaseReference.setValue(brg).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateBarang.this, "Update Sukses", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteData() {
    }

}