<?php
include "db_connect.php";

$id_produk = $_GET['id_produk'] ?? '';
// Sanitasi sederhana untuk mencegah injection pada nilai numerik/id
$id_produk = mysqli_real_escape_string($koneksi, $id_produk);

$result = mysqli_query($koneksi, "SELECT * FROM products WHERE id_produk='$id_produk'");
$data = mysqli_fetch_assoc($result);

if ($data) {
    echo json_encode($data);
} else {
    http_response_code(404);
    echo json_encode(["success" => 0, "message" => "Produk tidak ditemukan"]);
}
?>
