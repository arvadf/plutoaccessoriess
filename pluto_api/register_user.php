<?php
include 'db_connect.php';

// Ambil data dari request (POST)
$nama     = $_POST['nama'] ?? '';
$email    = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';
$alamat   = $_POST['alamat'] ?? '';
$no_hp    = $_POST['no_hp'] ?? '';
$role     = 'user'; // default user biasa

// Cek data kosong
if (empty($nama) || empty($email) || empty($password)) {
    echo json_encode([
        "success" => 0,
        "message" => "Semua field wajib diisi!"
    ]);
    exit;
}

// Cek apakah email sudah digunakan
$email = mysqli_real_escape_string($koneksi, $email);
$nama = mysqli_real_escape_string($koneksi, $nama);
$password = mysqli_real_escape_string($koneksi, $password);
$alamat = mysqli_real_escape_string($koneksi, $alamat);
$no_hp = mysqli_real_escape_string($koneksi, $no_hp);

$cek = mysqli_query($koneksi, "SELECT * FROM users WHERE email = '$email'");
if (mysqli_num_rows($cek) > 0) {
    echo json_encode([
        "success" => 0,
        "message" => "Email sudah terdaftar!"
    ]);
    exit;
}

// Simpan user baru
$query = "INSERT INTO users (nama, email, password, alamat, no_hp, role) 
          VALUES ('$nama', '$email', '$password', '$alamat', '$no_hp', '$role')";

if (mysqli_query($koneksi, $query)) {
    echo json_encode([
        "success" => 1,
        "message" => "Registrasi berhasil!"
    ]);
} else {
    http_response_code(500);
    echo json_encode([
        "success" => 0,
        "message" => "Gagal registrasi: " . mysqli_error($koneksi)
    ]);
}
?>
