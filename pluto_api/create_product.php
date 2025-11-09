<?php
include 'db_connect.php';

$nama_produk = $_POST['nama_produk'] ?? '';
$deskripsi = $_POST['deskripsi'] ?? '';
$harga = $_POST['harga'] ?? 0;
$stok = $_POST['stok'] ?? 0;
$kategori = $_POST['kategori'] ?? '';
$gambar = $_POST['gambar'] ?? ''; 

if (empty($nama_produk) || empty($deskripsi) || $harga <= 0) {
    echo json_encode(['success' => 0, 'message' => 'Data tidak lengkap']);
    exit;
}

$query = "INSERT INTO products (nama_produk, deskripsi, harga, stok, kategori, gambar) VALUES ('$nama_produk', '$deskripsi', '$harga', '$stok', '$kategori', '$gambar')";

if (mysqli_query($koneksi, $query)) {
    echo json_encode(['success' => 1, 'message' => 'Produk berhasil ditambahkan']);
} else {
    echo json_encode(['success' => 0, 'message' => 'Gagal menambahkan produk: ' . mysqli_error($koneksi)]);
}

mysqli_close($koneksi);
?>