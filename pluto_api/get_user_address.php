<?php
header('Content-Type: application/json');
include 'db_connect.php';

if (!isset($_GET['id_user']) || empty($_GET['id_user'])) {
    echo json_encode(['success' => 0, 'message' => 'User ID is required.']);
    exit;
}

$id_user = mysqli_real_escape_string($koneksi, $_GET['id_user']);

$query = "SELECT alamat FROM users WHERE id_user = '$id_user'";
$result = mysqli_query($koneksi, $query);

if ($result && mysqli_num_rows($result) > 0) {
    $row = mysqli_fetch_assoc($result);
    echo json_encode(['success' => 1, 'alamat' => $row['alamat']]);
} else {
    echo json_encode(['success' => 0, 'message' => 'Address not found.']);
}

mysqli_close($koneksi);
?>