package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    private RecyclerView rvView;
    private ImageAdapter adapter;

    private DatabaseReference databaseReference;
    private List<Barang> lbrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        rvView = findViewById(R.id.recycle_view);
        rvView.setHasFixedSize(true);
        rvView.setLayoutManager(new LinearLayoutManager(this));

        lbrg = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference("data-barang");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot item : snapshot.getChildren()) {
                   Barang brg = item.getValue(Barang.class);
                   lbrg.add(brg);
               }

               adapter = new ImageAdapter(ImagesActivity.this, lbrg);
               rvView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImagesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}