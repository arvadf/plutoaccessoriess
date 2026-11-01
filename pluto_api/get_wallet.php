<?php
header('Content-Type: application/json');
include 'db_connect.php';

// Check if id_user is provided
if (!isset($_GET['id_user']) || empty($_GET['id_user'])) {
    echo json_encode(['success' => 0, 'message' => 'User ID is required.']);
    exit;
}

$id_user = mysqli_real_escape_string($koneksi, $_GET['id_user']);

// Look for the wallet
$query = "SELECT saldo FROM wallet WHERE id_user = '$id_user'";
$result = mysqli_query($koneksi, $query);

if ($result) {
    if (mysqli_num_rows($result) > 0) {
        // Wallet found, return saldo
        $row = mysqli_fetch_assoc($result);
        echo json_encode(['success' => 1, 'saldo' => $row['saldo']]);
    } else {
        // Wallet not found for this user, create one
        $insert_query = "INSERT INTO wallet (id_user, saldo) VALUES ('$id_user', 0.00)";
        if (mysqli_query($koneksi, $insert_query)) {
            // Return the new balance of 0
            echo json_encode(['success' => 1, 'saldo' => 0.00]);
        } else {
            echo json_encode(['success' => 0, 'message' => 'Failed to create a new wallet.']);
        }
    }
} else {
    echo json_encode(['success' => 0, 'message' => 'Database query failed.']);
}

mysqli_close($koneksi);
?>