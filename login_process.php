<?php
// Hard-coded username & password (for learning only)
$correct_username = "admin";
$correct_password = "1234";

$username = $_POST["username"];
$password = $_POST["password"];

if ($username === $correct_username && $password === $correct_password) {
    // Set cookie for 1 hour
    setcookie("login_user", $username, time() + 3600, "/");
    header("Location: dashboard.php");
} else {
    echo "Invalid username or password";
}
?>
