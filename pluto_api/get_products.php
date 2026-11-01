<?php
include 'db_connect.php';

$query = "SELECT * FROM products ORDER BY id_produk DESC";
$result = mysqli_query($koneksi, $query);

$products = array();
while ($row = mysqli_fetch_assoc($result)) {
    $products[] = $row;
}

echo json_encode($products);

mysqli_close($koneksi);
?>