package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaParser;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailBarang extends AppCompatActivity {

    TextView tvnama, tvdeskripsi, tvquantity, showall;
    ImageButton incre, decre;
    ImageView imgView;
    Button btnSubmit;

    int quantity;
    private String uri;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_barang);

        databaseReference = FirebaseDatabase.getInstance().getReference("cart");

        tvnama = findViewById(R.id.namaDetail);
        tvdeskripsi = findViewById(R.id.deskripsiDetail);
        tvquantity = findViewById(R.id.quantity);
        incre = findViewById(R.id.increment);
        decre = findViewById(R.id.decrement);
        imgView = findViewById(R.id.imgDetail);
        btnSubmit = findViewById(R.id.submit);
        showall = findViewById(R.id.alltrans);

        tvquantity.setText("0");
        quantity = Integer.parseInt(tvquantity.getText().toString());


        Intent intent = getIntent();
        uri = intent.getStringExtra("url");
        tvnama.setText(intent.getStringExtra("nama"));
        tvdeskripsi.setText(intent.getStringExtra("deksripsi"));
        Picasso.with(this).load(uri).into(imgView);

        incre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity++;
                tvquantity.setText(String.valueOf(quantity));
            }
        });

        decre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity == 0) {
                    Toast.makeText(DetailBarang.this, "Tidak Bisa Kurang Dari 0", Toast.LENGTH_SHORT).show();
                } else {
                    quantity--;
                    tvquantity.setText(String.valueOf(quantity));
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest();
            }
        });

        showall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

    }

    private void submitRequest() {
        Cart cart = new Cart(tvnama.getText().toString(), tvdeskripsi.getText().toString(), tvquantity.getText().toString());
        String uploadId = databaseReference.push().getKey();
        databaseReference.child(uploadId).setValue(cart);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Toast.makeText(DetailBarang.this, "Barang berhasil ditambah", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DetailBarang.this, "Barang gagal ditambah", Toast.LENGTH_SHORT).show();
            }
        });


    }
}