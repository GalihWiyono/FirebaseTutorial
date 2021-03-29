package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.google.android.gms.tasks.OnFailureListener;
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
        databaseReference = FirebaseDatabase.getInstance().getReference("data-barang");

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

                AlertDialog builder = new AlertDialog.Builder(UpdateBarang.this)
                        .setMessage("Delete Data?")
                        .setPositiveButton("Yes", null)
                        .setNegativeButton("No", null)
                        .show();
                Button positiveBtn = builder.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteData(url, id.getText().toString());
                        builder.dismiss();
                    }
                });
                Button negativBtn = builder.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
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

    //get extension
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
                    //delete previous img
                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                    reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //start update entry to db
                            Toast.makeText(UpdateBarang.this, "Image Updated", Toast.LENGTH_SHORT).show();
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            Uri downloadUri = uriTask.getResult();
                            updateInfo(downloadUri.toString(), idBarang);
                        }
                    });
                }
            });
        } else {
            updateInfo(url, idBarang);
        }
    }

    private void updateInfo(String urlGambar, String idBarang) {
        databaseReference.child(idBarang);
        Barang brg = new Barang(idBarang, nama.getText().toString(), deskripsi.getText().toString(), urlGambar);
        databaseReference.setValue(brg).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateBarang.this, "Update Success", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteData(String url, String id) {
        databaseReference.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UpdateBarang.this, "Delete Data Success", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UpdateBarang.this, ImagesActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateBarang.this, "Delete Data Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}