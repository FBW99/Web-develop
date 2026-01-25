<?php
session_start();
require_once "../dbconnect.php";

if (!isset($_SESSION["role"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

$result = $conn->query("
    SELECT * FROM contact_messages
    ORDER BY created_at DESC
");
?>

<!DOCTYPE html>
<html>

<head>
    <title>Contact Messages</title>
    <style>
        body {
            font-family: Arial;
            background: #f4f6f8;
            padding: 20px;
        }

        table {
            width: 100%;
            background: #fff;
            border-collapse: collapse;
        }

        th,
        td {
            padding: 12px;
            border-bottom: 1px solid #ddd;
        }

        th {
            background: #ff6600;
            color: #fff;
        }

        .btn {
            padding: 6px 10px;
            border-radius: 5px;
            color: #fff;
            text-decoration: none;
        }

        .reply {
            background: #007bff;
        }

        .done {
            background: #28a745;
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

    <h2>📨 Contact Messages</h2>

    <table>
        <tr>
            <th>Name</th>
            <th>Email</th>
            <th>Message</th>
            <th>Status</th>
            <th>Action</th>
        </tr>

        <?php while ($row = $result->fetch_assoc()): ?>
            <tr>
                <td><?= htmlspecialchars($row["name"]) ?></td>
                <td><?= htmlspecialchars($row["email"]) ?></td>
                <td><?= nl2br(htmlspecialchars($row["message"])) ?></td>
                <td><?= $row["status"] ?></td>
                <td>
                    <?php if ($row["status"] == "Pending"): ?>
                        <a class="btn reply" href="reply-message.php?id=<?= $row["id"] ?>">
                            Reply
                        </a>
                    <?php else: ?>
                        <span class="btn done">Replied</span>
                    <?php endif; ?>
                </td>
            </tr>
        <?php endwhile; ?>
    </table>
    <a href="dashboard.php" class="back">⬅ Back to Dashboard</a>
</body>

</html>