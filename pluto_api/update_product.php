<?php
include 'db_connect.php';

$id_produk = $_POST['id_produk'] ?? '';
$nama_produk = $_POST['nama_produk'] ?? '';
$deskripsi = $_POST['deskripsi'] ?? '';
$harga = $_POST['harga'] ?? 0;
$stok = $_POST['stok'] ?? 0;
$kategori = $_POST['kategori'] ?? '';
$gambar = $_POST['gambar'] ?? '';

if (empty($id_produk) || empty($nama_produk) || empty($deskripsi) || $harga <= 0) {
    echo json_encode(['success' => 0, 'message' => 'Data tidak lengkap']);
    exit;
}

$query = "UPDATE products SET nama_produk='$nama_produk', deskripsi='$deskripsi', harga='$harga', stok='$stok', kategori='$kategori', gambar='$gambar' WHERE id_produk='$id_produk'";

if (mysqli_query($koneksi, $query)) {
    echo json_encode(['success' => 1, 'message' => 'Produk berhasil diperbarui']);
} else {
    echo json_encode(['success' => 0, 'message' => 'Gagal memperbarui produk: ' . mysqli_error($koneksi)]);
}

mysqli_close($koneksi);
?>