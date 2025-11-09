<?php
header('Content-Type: application/json');
require 'db_connect.php';

// Check if all required POST variables are set
if (!isset($_POST['id_user'], $_POST['total_harga'], $_POST['items'], $_POST['alamat'], $_POST['metode_pembayaran'], $_POST['opsi_pengiriman'])) {
    echo json_encode(['success' => false, 'message' => 'Data tidak lengkap.']);
    exit;
}

$id_user = $_POST['id_user'];
$total_harga = $_POST['total_harga'];
$items_json = $_POST['items'];
$alamat = $_POST['alamat'];
$metode_pembayaran = $_POST['metode_pembayaran'];
$opsi_pengiriman = $_POST['opsi_pengiriman'];

// Start a transaction
mysqli_begin_transaction($conn);

try {
    // Insert into orders table
    $sql = "INSERT INTO orders (id_user, total_harga, alamat, metode_pembayaran, opsi_pengiriman) VALUES (?, ?, ?, ?, ?)";
    $stmt = mysqli_prepare($conn, $sql);
    mysqli_stmt_bind_param($stmt, "idsss", $id_user, $total_harga, $alamat, $metode_pembayaran, $opsi_pengiriman);
    
    if (!mysqli_stmt_execute($stmt)) {
        throw new Exception("Database error: Gagal menyimpan pesanan.");
    }
    
    $order_id = mysqli_insert_id($conn);

    // Decode the JSON items
    $items = json_decode($items_json, true);
    if (json_last_error() !== JSON_ERROR_NONE) {
        throw new Exception("Data item tidak valid.");
    }

    // Insert into order_items table
    $sql_items = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
    $stmt_items = mysqli_prepare($conn, $sql_items);
    
    foreach ($items as $item) {
        $product_id = $item['idProduk'];
        $quantity = $item['jumlah'];
        $price = $item['harga'];
        
        mysqli_stmt_bind_param($stmt_items, "iiid", $order_id, $product_id, $quantity, $price);
        if (!mysqli_stmt_execute($stmt_items)) {
            throw new Exception("Database error: Gagal menyimpan item pesanan.");
        }
    }
    
    // Commit the transaction
    mysqli_commit($conn);
    
    echo json_encode(['success' => true, 'message' => 'Pesanan berhasil dibuat.']);

} catch (Exception $e) {
    // Rollback the transaction on error
    mysqli_rollback($conn);
    // Set HTTP response code to indicate an error, though client might not check it
    http_response_code(500); 
    echo json_encode(['success' => false, 'message' => $e->getMessage()]);
}

mysqli_close($conn);
?>