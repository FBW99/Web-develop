<?php
session_start();
require_once "../dbconnect.php";

if (!isset($_SESSION["role"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

$sql = "
SELECT orders.*, users.name, users.email
FROM orders
JOIN users ON orders.user_id = users.id
ORDER BY orders.order_date DESC
";
$result = $conn->query($sql);
?>

<!DOCTYPE html>
<html>

<head>
    <title>Admin Orders</title>

    <style>
        body {
            font-family: Arial;
            background: #f4f6f8;
            padding: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            border-radius: 10px;
        }

        th,
        td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }

        th {
            background: #ff6600;
            color: white;
        }

        .btn {
            padding: 6px 12px;
            border-radius: 6px;
            text-decoration: none;
            color: white;
            font-size: 14px;
        }

        .delivered {
            background: #28a745;
        }

        .completed {
            background: #555;
        }

        .details {
            text-align: left;
            background: #fafafa;
            padding: 10px;
            border-radius: 8px;
            font-size: 14px;
        }

         .back {
            display: inline-block;
            margin: 15px;
            text-decoration: none;
            background: #333;
            color: white;
            padding: 8px 12px;
            border-radius: 6px;
        }
    </style>
</head>

<body>

    <h2>📦 Customer Orders</h2>

    <table>
        <tr>
            <th>Order ID</th>
            <th>Customer</th>
            <th>Total</th>
            <th>Status</th>
            <th>Action</th>
        </tr>

        <?php while ($row = $result->fetch_assoc()): ?>

            <tr>
                <td>#<?= $row["id"] ?></td>
                <td>
                    <b><?= htmlspecialchars($row["name"]) ?></b><br>
                    <small><?= htmlspecialchars($row["email"]) ?></small>
                </td>
                <td><?= number_format($row["total_price"], 2) ?> Br</td>

                <td>
                    <?php if ($row["status"] == "Completed"): ?>
                        ✅ Completed
                    <?php elseif ($row["status"] == "Delivered"): ?>
                        🚚 Delivered
                    <?php else: ?>
                        ⏳ Pending
                    <?php endif; ?>
                </td>

                <td>
                    <?php if ($row["status"] == "Pending"): ?>
                        <a class="btn delivered" href="mark-delivered.php?id=<?= $row["id"] ?>">
                            Mark Delivered
                        </a>
                    <?php else: ?>
                        <span class="btn completed">Done</span>
                    <?php endif; ?>
                </td>
            </tr>

            <!-- 🔽 ORDER DETAILS -->
            <tr>
                <td colspan="5">
                    <div class="details">
                        <b> Delivery Information</b><br><br>

                        <?php
                        $addr_sql = $conn->prepare("
            SELECT full_name, phone, city, address
            FROM order_address
            WHERE order_id = ?
        ");
                        $addr_sql->bind_param("i", $row["id"]);
                        $addr_sql->execute();
                        $address = $addr_sql->get_result()->fetch_assoc();
                        ?>

                        <?php if ($address): ?>
                            <b>Name:</b> <?= htmlspecialchars($address["full_name"]) ?><br>
                            <b>Phone:</b> <?= htmlspecialchars($address["phone"]) ?><br>
                            <b>City:</b> <?= htmlspecialchars($address["city"]) ?><br>
                            <b>Address:</b> <?= htmlspecialchars($address["address"]) ?><br>
                        <?php else: ?>
                            <span style="color:#777;">No address found</span>
                        <?php endif; ?>

                    </div>
                </td>
            </tr>


        <?php endwhile; ?>
    </table>
    <a href="dashboard.php" class="back">⬅ Back to Products</a>
</body>

</html>