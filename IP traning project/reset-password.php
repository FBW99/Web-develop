<?php
require_once "dbconnect.php";

if(!isset($_GET["token"])){
    die("Invalid request");
}

$token = $_GET["token"];

$stmt = $conn->prepare("
    SELECT id FROM users 
    WHERE reset_token=? AND token_expiry > NOW()
");
$stmt->bind_param("s",$token);
$stmt->execute();
$result = $stmt->get_result();

if($result->num_rows==0){
    die("Token expired or invalid");
}
?>

<!DOCTYPE html>
<html>
<head>
<title>Reset Password</title>

<style>
body{
    font-family:Arial;
    background:#f4f6f8;
    display:flex;
    justify-content:center;
    align-items:center;
    height:100vh;
}
.box{
    background:#fff;
    padding:30px;
    border-radius:10px;
    width:100%;
    max-width:400px;
}
input,button{
    width:100%;
    padding:12px;
    margin-top:10px;
}
button{
    background:#28a745;
    color:white;
    border:none;
}
</style>
</head>

<body>
<div class="box">
<h2>Reset Password</h2>

<form action="update-password.php" method="POST">
    <input type="hidden" name="token" value="<?= $token ?>">
    <input type="password" name="password" placeholder="New Password" required>
    <input type="password" name="confirm" placeholder="Confirm Password" required>
    <button type="submit">Update Password</button>
</form>
</div>
</body>
</html>
