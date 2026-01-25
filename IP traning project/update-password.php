<?php
require_once "dbconnect.php";

if($_SERVER["REQUEST_METHOD"]=="POST"){
    $token = $_POST["token"];
    $password = $_POST["password"];
    $confirm = $_POST["confirm"];

    if($password !== $confirm){
        die("Passwords do not match");
    }

    $hashed = password_hash($password,PASSWORD_DEFAULT);

    $stmt = $conn->prepare("
        UPDATE users 
        SET password=?, reset_token=NULL, token_expiry=NULL
        WHERE reset_token=?
    ");
    $stmt->bind_param("ss",$hashed,$token);
    $stmt->execute();

    header("Location: login.php?reset=success");
    exit();
}
