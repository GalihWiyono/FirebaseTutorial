package com.example.firebaseimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartHolder> {

    private Context ctx;
    private List<Cart> lcart;

    public CartAdapter(Context ctx, List<Cart> lcart) {
        this.ctx = ctx;
        this.lcart = lcart;
    }

    @NonNull
    @Override
    public CartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.card_cart, parent, false);
        return new CartHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartHolder holder, int position) {
        Cart cart = lcart.get(position);
        holder.nama.setText(cart.getNama());
        holder.quantity.setText(cart.getQuantity());
        holder.deskripsi.setText(cart.getDeskripsi());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "Edit Button Per Cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return lcart.size();
    }

    public class CartHolder extends RecyclerView.ViewHolder {

        public TextView nama,deskripsi,quantity,edit;

        public CartHolder(@NonNull View itemView) {
            super(itemView);

            nama = itemView.findViewById(R.id.txtNama);
            quantity = itemView.findViewById(R.id.txtQuantity);
            edit = itemView.findViewById(R.id.EditTxt);
            deskripsi = itemView.findViewById(R.id.txtDeskripsi);
        }
    }
}