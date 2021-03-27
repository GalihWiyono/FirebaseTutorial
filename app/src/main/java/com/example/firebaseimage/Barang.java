package com.example.firebaseimage;

public class Barang {
    private String nama,deskripsi, imageUrl;

    public Barang() {
    }

    public Barang(String nama, String deskripsi, String imageUrl) {
        if (nama.trim().equals("")) {
            nama = "No Name";
        }
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.imageUrl = imageUrl;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
