<?php
include "db_connect.php";

$id_order = $_POST['id_order'];
$status   = $_POST['status'];

if (mysqli_query($koneksi, "UPDATE orders SET status='$status' WHERE id_order='$id_order'")) {
    echo json_encode(["success" => true, "message" => "Status diperbarui"]);
} else {
    echo json_encode(["success" => false, "message" => "Gagal memperbarui status"]);
}
?>
