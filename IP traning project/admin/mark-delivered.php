<?php
session_start();
require_once "../dbconnect.php";

if (!isset($_SESSION["role"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

if (!isset($_GET["id"])) {
    header("Location: orders.php");
    exit();
}

$order_id = intval($_GET["id"]);

$sql = $conn->prepare("UPDATE orders SET status='Delivered' WHERE id=?");
$sql->bind_param("i", $order_id);
$sql->execute();

header("Location: orders.php");
exit();





