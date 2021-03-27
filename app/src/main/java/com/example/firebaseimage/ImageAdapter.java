package com.example.firebaseimage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {

    private Context ctx;
    private List<Barang> lbarang;

    public ImageAdapter(Context context, List<Barang> brg) {
        ctx = context;
        lbarang = brg;
    }

    public class ImageHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvDekripsi;
        public ImageView imageView;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tv_name);
            tvDekripsi = itemView.findViewById(R.id.tv_deskripsi);
            imageView = itemView.findViewById(R.id.img_view);
        }
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.images_item, parent, false);
        return new ImageHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        Barang brang = lbarang.get(position);
        holder.tvName.setText(brang.getNama());
        holder.tvDekripsi.setText(brang.getDeskripsi());
        Picasso.with(ctx).load(brang.getImageUrl()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, DetailBarang.class);
                intent.putExtra("nama", brang.getNama());
                intent.putExtra("deksripsi", brang.getDeskripsi());
                intent.putExtra("url", brang.getImageUrl());
                ctx.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lbarang.size();
    }


}
