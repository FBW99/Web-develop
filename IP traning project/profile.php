<?php
session_start();
if (!isset($_SESSION["user_id"])) {
    header("Location: login.php");
    exit();
}
// Profile photo (if exists)
$photo = $_SESSION["photo"] ?? "";

/* ===== DB CONNECTION ===== */
$conn = new mysqli("localhost", "root", "", "abmecommerce");
if ($conn->connect_error) {
    die("Database connection failed");
}

$user_id = $_SESSION["user_id"];

/* ===== FETCH USER ORDERS ===== */
$order_sql = "
SELECT 
    o.id AS order_id,
    o.total_price,
    o.order_date,
    o.status,
    GROUP_CONCAT(
        CONCAT(p.name, ' (', oi.quantity, ' × ', oi.price, ' Br)')
        SEPARATOR '<br>'
    ) AS products
FROM orders o
JOIN order_items oi ON o.id = oi.order_id
JOIN products p ON oi.product_id = p.id
WHERE o.user_id = ?
GROUP BY o.id
ORDER BY o.order_date DESC
";

$stmt = $conn->prepare($order_sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$orders = $stmt->get_result();
?>

<!DOCTYPE html>
<html>

<head>
    <title>My Profile</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f4f6f8;
            margin: 0;
            padding: 0;
        }

        /* ===== Profile ===== */
        .profile-box {
            max-width: 420px;
            margin: 30px auto;
            background: #fff;
            border-radius: 15px;
            padding: 25px;
            text-align: center;
            box-shadow: 0 10px 25px rgba(0, 0, 0, .1);
        }

        .profile-img,
        .profile-icon {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            margin: 0 auto 15px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 3px solid #ff6600;
        }

        .profile-icon i {
            font-size: 60px;
            color: #555;
        }

        h2 {
            margin: 10px 0 5px;
        }

        p {
            color: #555;
        }

        .btn {
            display: block;
            width: 100%;
            padding: 12px;
            margin-top: 10px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 16px;
            color: #fff;
        }

        .home-btn {
            background: #007bff;
        }

        .edit-btn {
            background: #ff6600;
        }

        .logout-btn {
            background: #333;
        }

        /* ===== Orders ===== */
        .orders-section {
            max-width: 1000px;
            margin: 40px auto;
            background: #fff;
            border-radius: 15px;
            padding: 25px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, .1);
        }

        .orders-section h3 {
            text-align: center;
            margin-bottom: 20px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th,
        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #ddd;
        }

        th {
            background: #ff6600;
            color: #fff;
        }

        /* Mobile */
        .order-card {
            display: none;
            border: 1px solid #ddd;
            border-radius: 10px;
            padding: 15px;
            margin-bottom: 15px;
        }

        .order-card div {
            margin-bottom: 8px;
        }

        .label {
            font-weight: bold;
            color: #555;
        }

        @media (max-width: 768px) {
            table {
                display: none;
            }

            .order-card {
                display: block;
            }
        }
    </style>
</head>

<body>

    <!-- PROFILE -->
    <div class="profile-box">
        <?php if (!empty($photo) && file_exists("uploads/$photo")): ?>
            <img src="uploads/<?php echo $photo; ?>" class="profile-img">
        <?php else: ?>
            <div class="profile-icon">
                <i class="fa-solid fa-user"></i>
            </div>
        <?php endif; ?>

        <h2><?= htmlspecialchars($_SESSION["name"]) ?></h2>
        <p><?= htmlspecialchars($_SESSION["email"]) ?></p>

        <a href="home.php" class="btn home-btn">⬅ Back to Home</a>
        <a href="edit-profile.php" class="btn edit-btn">✏️ Edit Profile</a>
        <a href="logout.php" class="btn logout-btn">🚪 Logout</a>
    </div>

    <!-- ORDER HISTORY -->
    <div class="orders-section">
        <h3>🛒 My Order History</h3>

        <?php if ($orders->num_rows > 0): ?>

            <!-- Desktop Table -->
            <table>
                <tr>
                    <th>Order ID</th>
                    <th>Products</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Date</th>
                </tr>

                <?php while ($order = $orders->fetch_assoc()): ?>
                    <tr>
                        <td>#<?= $order["order_id"] ?></td>
                        <td><?= $order["products"] ?></td>
                        <td><?= number_format($order["total_price"], 2) ?> Br</td>
                        <td>
                            <?php if ($order["status"] == "Delivered"): ?>
                                <span style="color:green;font-weight:bold;">Delivered</span><br><br>

                                <a href="confirm-received.php?order_id=<?= $order["order_id"] ?>"
                                    style="background:#007bff;color:#fff;padding:6px 12px;
                                     border-radius:6px;text-decoration:none;font-size:14px;">
                                    ✔ Confirm Received
                                </a>

                            <?php elseif ($order["status"] == "Completed"): ?>
                                <span style="color:#555;font-weight:bold;">Completed</span>

                            <?php else: ?>
                                <span style="color:#ff6600;">Pending</span>
                            <?php endif; ?>
                        </td>
                        <td><?= $order["order_date"] ?></td>
                    </tr>
                <?php endwhile; ?>
            </table>



            <!-- Mobile Cards -->
            <?php
            $orders->data_seek(0);
            while ($order = $orders->fetch_assoc()):
            ?>
                <div class="order-card">
                    <div><span class="label">Order ID:</span> #<?= $order["order_id"] ?></div>
                    <div><span class="label">Products:</span><br><?= $order["products"] ?></div>
                    <div><span class="label">Total:</span> <?= number_format($order["total_price"], 2) ?> Br</div>
                    <div>
                        <span class="label">Status:</span>
                        <?php if ($order["status"] == "Delivered"): ?>
                            <span style="color:green;">Delivered</span><br><br>

                            <a href="confirm-received.php?order_id=<?= $order["order_id"] ?>"
                                style="background:#007bff;color:#fff;padding:6px 12px;
                  border-radius:6px;text-decoration:none;font-size:14px;">
                                ✔ Confirm Received
                            </a>

                        <?php elseif ($order["status"] == "Completed"): ?>
                            <span style="color:#555;">Completed</span>

                        <?php else: ?>
                            <span style="color:#ff6600;">Pending</span>
                        <?php endif; ?>
                    </div>

                    <div><span class="label">Date:</span> <?= $order["order_date"] ?></div>
                </div>

            <?php endwhile; ?>


        <?php else: ?>
            <p style="text-align:center;color:#777;">No orders found.</p>
        <?php endif; ?>
    </div>

</body>

</html>