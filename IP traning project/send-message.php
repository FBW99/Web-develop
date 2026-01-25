<?php
session_start();
require_once "dbconnect.php";

// user must login
if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $name    = $_POST["name"];
    $email   = $_POST["email"];
    $message = $_POST["message"];

    $stmt = $conn->prepare("
        INSERT INTO contact_messages (name, email, message)
        VALUES (?, ?, ?)
    ");
    $stmt->bind_param("sss", $name, $email, $message);
    $stmt->execute();

    // 🔴 FIX THIS PATH
    header("Location:home.php?sent=1");
    exit();
}

