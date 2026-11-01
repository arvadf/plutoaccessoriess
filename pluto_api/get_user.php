<?php
include 'db_connect.php';

// Ambil semua user dari tabel users
$result = mysqli_query($koneksi, "SELECT id_user, nama, email, alamat, no_hp, role FROM users");

$users = [];
while ($row = mysqli_fetch_assoc($result)) {
    $users[] = [
        "id" => $row['id_user'],
        "nama" => $row['nama'],
        "email" => $row['email'],
        "alamat" => $row['alamat'],
        "no_hp" => $row['no_hp'],
        "role" => $row['role']
    ];
}

echo json_encode($users);
?>
