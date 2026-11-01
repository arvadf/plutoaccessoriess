<?php
// ==============================
// File: db_connect.php
// ==============================

// Set JSON header dan izinkan akses dari aplikasi (CORS)
if (!headers_sent()) {
    header('Content-Type: application/json; charset=utf-8');
    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: GET, POST, OPTIONS');
    header('Access-Control-Allow-Headers: Content-Type, Authorization');
}

// Konfigurasi koneksi database (bisa via ENV, fallback ke default XAMPP)
$host     = getenv('DB_HOST') ?: 'localhost';
$user     = getenv('DB_USER') ?: 'root';
$password = getenv('DB_PASS') ?: '';
$database = getenv('DB_NAME') ?: 'db_pluto_9';

// Membuat koneksi
$koneksi = mysqli_connect($host, $user, $password, $database);

// Cek koneksi
if (!$koneksi) {
    http_response_code(500);
    echo json_encode([
        'success' => 0,
        'message' => 'Koneksi ke database gagal: ' . mysqli_connect_error()
    ]);
    exit;
}

// Set karakter agar mendukung UTF-8
mysqli_set_charset($koneksi, 'utf8mb4');

// Tidak ada output HTML
?>
