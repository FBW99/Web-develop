<?php
session_start();
require_once "dbconnect.php";

// user must login
if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}

// product id check
if (!isset($_POST['product_id'])) {
    header("Location: home.php");
    exit();
}

$user_id    = $_SESSION['user_id'];
$product_id = (int)$_POST['product_id'];

// check if product exists
$checkProduct = $conn->prepare("SELECT id FROM products WHERE id=?");
$checkProduct->bind_param("i", $product_id);
$checkProduct->execute();
$resProduct = $checkProduct->get_result();

if ($resProduct->num_rows == 0) {
    header("Location: home.php");
    exit();
}

// check if already in cart
$checkCart = $conn->prepare(
    "SELECT id, quantity FROM cart WHERE user_id=? AND product_id=?"
);
$checkCart->bind_param("ii", $user_id, $product_id);
$checkCart->execute();
$resCart = $checkCart->get_result();

if ($resCart->num_rows > 0) {
    // update quantity
    $row = $resCart->fetch_assoc();
    $newQty = $row['quantity'] + 1;

    $update = $conn->prepare(
        "UPDATE cart SET quantity=? WHERE id=?"
    );
    $update->bind_param("ii", $newQty, $row['id']);
    $update->execute();
} else {
    // insert new item
    $insert = $conn->prepare(
        "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, 1)"
    );
    $insert->bind_param("ii", $user_id, $product_id);
    $insert->execute();
}

// back to home
header("Location: #product-section");
exit();
