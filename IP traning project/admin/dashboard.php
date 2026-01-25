<?php
session_start();
require_once "../dbconnect.php"; // adjust if your db file name is different

// 🔒 Admin protection
if (!isset($_SESSION["user_id"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

// 📊 Count users
$user_q = mysqli_query($conn, "SELECT COUNT(*) AS total_users FROM users");
$user_data = mysqli_fetch_assoc($user_q);

// 📦 Count products
$product_q = mysqli_query($conn, "SELECT COUNT(*) AS total_products FROM products");
$product_data = mysqli_fetch_assoc($product_q);

// 📦 Count orders
$order_q = mysqli_query($conn, "SELECT COUNT(*) AS total_orders FROM orders");
$order_data = mysqli_fetch_assoc($order_q);

// 📩 Count contact messages
$msg_q = mysqli_query($conn, "
    SELECT COUNT(*) AS total_messages 
    FROM contact_messages 
    WHERE status = 'Pending'
");
$msg_data = mysqli_fetch_assoc($msg_q);

?>




<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="./css/admin.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>

<body>

    <!-- ===== Admin Header ===== -->
    <header class="admin-header">
        <h2>Admin Dashboard</h2>
        <div>
            Welcome, <b><?php echo $_SESSION["name"]; ?></b> |
            <a href="./logout.php">Logout</a>
        </div>
    </header>

    <!-- ===== Dashboard Cards ===== -->
    <div class="dashboard">

        <div class="card">
            <i class="fa fa-users"></i>
            <h3><?php echo $user_data["total_users"]; ?></h3>
            <p>Total Users</p>
            <a href="users.php">View Users</a>
        </div>

        <div class="card">
            <i class="fa fa-box"></i>
            <h3><?php echo $product_data["total_products"]; ?></h3>
            <p>Total Products</p>
            <a href="products.php">View Products</a>
        </div>

        <div class="card">
            <i class="fa fa-plus"></i>
            <h3>Add</h3>
            <p>New Product</p>
            <a href="add-product.php">Add Product</a>
        </div>

        <div class="card">
            <i class="fa fa-truck"></i>
            <h3>Orders</h3>
            <p>Manage Orders</p>
            <a href="orders.php">View Orders</a>
        </div>

        <div class="card">
            <i class="fa fa-envelope"></i>
            <h3><?php echo $msg_data["total_messages"]; ?></h3>
            <p>New Messages</p>
            <a href="messages.php">View Messages</a>
        </div>


    </div>

</body>

</html>