<?php
session_start();
require_once "../dbconnect.php";

// 🔒 Admin protection
if (!isset($_SESSION["user_id"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

// Fetch all users EXCEPT admins
$result = $conn->query("SELECT id, name, email, role, photo FROM users WHERE role != 'admin'");?>

<!DOCTYPE html>
<html>
<head>
    <title>View Users</title>
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
        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: center;
        }
        th {
            background: #ff6600;
            color: white;
        }
        img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
        }
        .back {
            display: inline-block;
            margin-bottom: 15px;
            text-decoration: none;
            background: #333;
            color: white;
            padding: 8px 12px;
            border-radius: 6px;
        }

         .btn {
            text-decoration: none;
            padding: 8px 14px;
            border-radius: 6px;
            color: white;
        }

        .delete {
            background: #e53935;
        }
    </style>
</head>
<body>

<a href="dashboard.php" class="back">⬅ Back to Dashboard</a>

<h2><i class="fa fa-users"></i> All Users</h2>

<table>
    <tr>
        <th>ID</th>
        <th>Photo</th>
        <th>Name</th>
        <th>Email</th>
        <th>Role</th>
        <th>Action</th>
    </tr>

    <?php while ($row = $result->fetch_assoc()): ?>
        <tr>
            <td><?php echo $row["id"]; ?></td>

            <td>
                <?php if (!empty($row["photo"]) && file_exists("../uploads/" . $row["photo"])): ?>
                    <img src="../uploads/<?php echo $row["photo"]; ?>">
                <?php else: ?>
                    <i class="fa-solid fa-user"></i>
                <?php endif; ?>
            </td>

            <td><?php echo htmlspecialchars($row["name"]); ?></td>
            <td><?php echo htmlspecialchars($row["email"]); ?></td>
            <td><?php echo $row["role"]; ?></td>
            <td>
                <a href="delete-user.php?id=<?php echo $row["id"]; ?>"
                     class="btn delete"
                        onclick="return confirm('Delete this users?')">Delete</a>
            </td>
        </tr>
    <?php endwhile; ?>

</table>

</body>
</html>
