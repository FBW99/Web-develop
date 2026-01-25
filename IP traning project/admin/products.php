<?php
session_start();
require_once "../dbconnect.php";

// 🔒 Admin protection
if (!isset($_SESSION["user_id"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

// Fetch products
$result = $conn->query("SELECT * FROM products");
?>

<!DOCTYPE html>
<html>

<head>
    <title>View Products</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <style>
        body {
            font-family: Arial;
            background: #f4f6f8;
            padding: 30px;
        }

        h2 {
            text-align: center;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        th,
        td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background: #ff6600;
            color: white;
        }

        img {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 6px;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
        }

        .btn {
            text-decoration: none;
            padding: 8px 14px;
            border-radius: 6px;
            color: white;
        }

        .add {
            background: #28a745;
        }

        .back {
            background: #333;
        }

        .edit {
            background: #ff9800;
        }

        .delete {
            background: #e53935;
        }
    </style>
</head>

<body>

    <div class="top-bar">
        <a href="dashboard.php" class="btn back">⬅ Back</a>
        <a href="add-product.php" class="btn add"><i class="fa fa-plus"></i> Add Product</a>
    </div>

    <h2><i class="fa fa-box"></i> All Products</h2>

    <table>
        <tr>
            <th>ID</th>
            <th>Image</th>
            <th>Name</th>
            <th>Price</th>
            <th>description</th>
            <th>Actions</th>
        </tr>

        <?php while ($row = $result->fetch_assoc()): ?>
            <tr>
                <td><?php echo $row["id"]; ?></td>

                <td>
                    <?php if (!empty($row["image"]) && file_exists("../uploads/" . $row["image"])): ?>
                        <img src="../uploads/<?php echo $row["image"]; ?>">
                    <?php else: ?>
                        ❌
                    <?php endif; ?>
                </td>

                <td><?php echo htmlspecialchars($row["name"]); ?></td>
                <td>$<?php echo $row["price"]; ?></td>
                <td><?php echo $row["description"]; ?></td>
                <td>
                    <a href="edit-products.php?id=<?php echo $row["id"]; ?>" class="btn edit">Edit</a>
                    <a href="delete-product.php?id=<?php echo $row["id"]; ?>"
                        class="btn delete"
                        onclick="return confirm('Delete this product?')">Delete</a>
                </td>
            </tr>
        <?php endwhile; ?>

    </table>

</body>

</html>