<?php
include 'db_connect.php';

$id_user = $_GET['id_user'] ?? '';

if (empty($id_user)) {
    echo json_encode([
        "success" => 0,
        "message" => "ID user tidak ditemukan!"
    ]);
    exit;
}

$query = "SELECT * FROM users WHERE id_user='$id_user' LIMIT 1";
$result = mysqli_query($koneksi, $query);

if (mysqli_num_rows($result) > 0) {
    $user = mysqli_fetch_assoc($result);

    echo json_encode([
        "success" => 1,
        "user" => [
            "id_user" => $user['id_user'],
            "nama" => $user['nama'],
            "email" => $user['email'],
            "alamat" => $user['alamat'],
            "no_hp" => $user['no_hp'],
            "role" => $user['role']
        ]
    ]);
} else {
    echo json_encode([
        "success" => 0,
        "message" => "Data user tidak ditemukan!"
    ]);
}
?>
