<?php
header('Content-Type: application/json');
include 'db_connect.php';

// Check if id_user is provided
if (!isset($_GET['id_user']) || empty($_GET['id_user'])) {
    echo json_encode([]); // Return empty array if no user ID
    exit;
}

$id_user = mysqli_real_escape_string($koneksi, $_GET['id_user']);

// Fetch all transactions for the user, newest first
$query = "SELECT id_transaksi, tipe, jumlah, deskripsi, tanggal 
          FROM transactions 
          WHERE id_user = '$id_user' 
          ORDER BY tanggal DESC";
          
$result = mysqli_query($koneksi, $query);

$transactions = array();

if ($result) {
    while ($row = mysqli_fetch_assoc($result)) {
        $transactions[] = $row;
    }
}
// This will correctly return an empty array [] if there are no results
echo json_encode($transactions);

mysqli_close($koneksi);
?>