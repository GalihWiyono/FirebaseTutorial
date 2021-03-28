package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity {

    private RecyclerView rvView;
    private ImageAdapter adapter;

    private ImageButton btnSearch;
    private EditText txtSearch;

    private DatabaseReference databaseReference;
    private List<Barang> lbrg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        btnSearch = findViewById(R.id.btnSearch);
        txtSearch = findViewById(R.id.txtSearch);

        rvView = findViewById(R.id.recycle_view);
        rvView.setHasFixedSize(true);
        rvView.setLayoutManager(new LinearLayoutManager(this));

        lbrg = new ArrayList<>();
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchData(txtSearch.getText().toString());
            }
        });
    }

    private void showAll() {
        databaseReference = FirebaseDatabase.getInstance().getReference("data-barang");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Barang brg = item.getValue(Barang.class);
                    lbrg.add(brg);
                }
                viewData(lbrg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImagesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SearchData(String textSearch) {
        lbrg.clear();
        Toast.makeText(this, "Data Dicari", Toast.LENGTH_SHORT).show();
        Query query = databaseReference.orderByChild("nama").startAt(textSearch).endAt(textSearch + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    Barang brg = item.getValue(Barang.class);
                    lbrg.add(brg);
                }
                viewData(lbrg);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ImagesActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void viewData(List list) {
        adapter = new ImageAdapter(ImagesActivity.this, list);
        rvView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lbrg.clear();
        showAll();
    }
}