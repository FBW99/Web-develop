<?php
session_start();
require_once "dbconnect.php";

if (!isset($_SESSION['user_id'], $_GET['id'], $_GET['action'])) {
    header("Location: cart.php");
    exit();
}

$cart_id = intval($_GET['id']);
$action = $_GET['action'];

if ($action === "plus") {
    $sql = "UPDATE cart SET quantity = quantity + 1 WHERE id = ?";
}

if ($action === "minus") {
    // decrease quantity
    $sql = "UPDATE cart SET quantity = quantity - 1 WHERE id = ? AND quantity > 1";
}

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $cart_id);
$stmt->execute();

header("Location: cart.php");
exit();
