<?php
include "db_connect.php";

$action = $_POST['action'] ?? '';

if ($action == 'add') {
    $nama = $_POST['nama_produk'];
    $desc = $_POST['deskripsi'];
    $harga = $_POST['harga'];
    $stok = $_POST['stok'];
    $bahan = $_POST['bahan'];
    $merk = $_POST['merk'];
    $gambar = $_POST['gambar'];
    mysqli_query($koneksi, "INSERT INTO products (nama_produk, deskripsi, harga, stok, bahan, merk, gambar)
                            VALUES ('$nama','$desc','$harga','$stok','$bahan','$merk','$gambar')");
    echo json_encode(["success" => true]);
}

if ($action == 'delete') {
    $id = $_POST['id_produk'];
    mysqli_query($koneksi, "DELETE FROM products WHERE id_produk='$id'");
    echo json_encode(["success" => true]);
}
?>
