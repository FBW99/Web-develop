<?php
session_start();
require_once "dbconnect.php";

if (!isset($_SESSION["user_id"])) {
    header("Location: login.php");
    exit();
}

if (!isset($_GET["order_id"])) {
    header("Location: profile.php");
    exit();
}

$order_id = intval($_GET["order_id"]);
$user_id  = $_SESSION["user_id"];

$sql = $conn->prepare("
    UPDATE orders 
    SET status='Completed' 
    WHERE id=? AND user_id=?
");
$sql->bind_param("ii", $order_id, $user_id);
$sql->execute();

header("Location: profile.php");
exit();
