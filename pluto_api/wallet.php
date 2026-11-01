<?php
include "db_connect.php";

$action = $_POST['action'] ?? '';
$id_user = $_POST['id_user'] ?? '';

if ($action == 'get') {
    $result = mysqli_query($koneksi, "SELECT * FROM wallet WHERE id_user='$id_user'");
    $data = mysqli_fetch_assoc($result);
    echo json_encode($data);
}

if ($action == 'topup') {
    $jumlah = $_POST['jumlah'];
    mysqli_query($koneksi, "UPDATE wallet SET saldo = saldo + $jumlah, last_update = NOW() WHERE id_user='$id_user'");
    echo json_encode(["success" => true, "message" => "Top-up berhasil"]);
}
?>
