<?php
include 'db_connect.php';

$id_produk = $_POST['id_produk'] ?? '';

if (empty($id_produk)) {
    echo json_encode(['success' => 0, 'message' => 'ID Produk tidak ada']);
    exit;
}

$query = "DELETE FROM products WHERE id_produk='$id_produk'";

if (mysqli_query($koneksi, $query)) {
    echo json_encode(['success' => 1, 'message' => 'Produk berhasil dihapus']);
} else {
    echo json_encode(['success' => 0, 'message' => 'Gagal menghapus produk: ' . mysqli_error($koneksi)]);
}

mysqli_close($koneksi);
?>