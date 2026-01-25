<?php
$host = "localhost";       // Server
$user = "root";            // MySQL username
$pass = "";                // MySQL password
$dbname = "abmecommerce";  // Database name

$conn = new mysqli($host, $user, $pass, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
?>