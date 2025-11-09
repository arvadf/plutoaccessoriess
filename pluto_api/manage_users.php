<?php
include "db_connect.php";

$result = mysqli_query($koneksi, "SELECT id_user, nama, email, role FROM users");
$users = [];

while ($row = mysqli_fetch_assoc($result)) {
    $users[] = $row;
}

echo json_encode($users);
?>
