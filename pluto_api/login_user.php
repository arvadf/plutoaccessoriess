<?php
include 'db_connect.php';

// Ambil data dari request
$email    = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// Cek field kosong
if (empty($email) || empty($password)) {
    echo json_encode([
        "success" => 0,
        "message" => "Email dan password wajib diisi!"
    ]);
    exit;
}

// Hindari SQL Injection
$email = mysqli_real_escape_string($koneksi, $email);
$password = mysqli_real_escape_string($koneksi, $password);

// Cek user di database
// Catatan: di produksi, gunakan password hashing (password_hash + password_verify)
$query = "SELECT * FROM users WHERE email='$email' AND password='$password' LIMIT 1";
$result = mysqli_query($koneksi, $query);

if (!$result) {
    http_response_code(500);
    echo json_encode([
        "success" => 0,
        "message" => "Query gagal: " . mysqli_error($koneksi)
    ]);
    exit;
}

// Jika ditemukan user
if (mysqli_num_rows($result) > 0) {
    $user = mysqli_fetch_assoc($result);

    echo json_encode([
        "success" => 1,
        "message" => "Login berhasil!",
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
        "message" => "Email atau password salah!"
    ]);
}

mysqli_close($koneksi);
?>
