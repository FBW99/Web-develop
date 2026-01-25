<?php
session_start();
require_once "../dbconnect.php";

// 🔒 Admin protection
if (!isset($_SESSION["user_id"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

$id = $_GET["id"] ?? 0;

$stmt = $conn->prepare("DELETE FROM products WHERE id=?");
$stmt->bind_param("i", $id);
$stmt->execute();

header("Location: products.php");
exit();
