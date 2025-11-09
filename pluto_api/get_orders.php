<?php
header('Content-Type: application/json');
require 'db_connect.php';

$response = [];

if (!isset($_GET['id_user'])) {
    echo json_encode([]); 
    exit;
}

$id_user = $_GET['id_user'];

$sql = "SELECT id, tanggal, total_harga FROM orders WHERE id_user = ?";
$stmt = mysqli_prepare($conn, $sql);

if ($stmt) {
    mysqli_stmt_bind_param($stmt, "i", $id_user);
    
    if (mysqli_stmt_execute($stmt)) {
        $result = mysqli_stmt_get_result($stmt);
        $orders = [];
        while ($row = mysqli_fetch_assoc($result)) {
            $orders[] = $row;
        }
        $response = $orders;
    }
    mysqli_stmt_close($stmt);
}

echo json_encode($response);

mysqli_close($conn);
?>