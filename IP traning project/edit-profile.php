<?php
session_start();
require_once "dbconnect.php";

if (!isset($_SESSION["user_id"])) {
    header("Location: login.php");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $name   = $_POST["name"];
    $email  = $_POST["email"];
    $userId = $_SESSION["user_id"];

    // Keep old photo by default
    $photoName = $_SESSION["photo"] ?? "";

    // If new photo uploaded
    if (!empty($_FILES["photo"]["name"])) {

        $allowedTypes = ["jpg", "jpeg", "png", "gif"];
        $ext = strtolower(pathinfo($_FILES["photo"]["name"], PATHINFO_EXTENSION));

        if (in_array($ext, $allowedTypes)) {

            $photoName = time() . "_" . $_FILES["photo"]["name"];
            $target = "uploads/" . $photoName;

            move_uploaded_file($_FILES["photo"]["tmp_name"], $target);

            // Update name, email, profile photo
            $stmt = $conn->prepare(
                "UPDATE users SET name=?, email=?, photo=? WHERE id=?"
            );
            $stmt->bind_param("sssi", $name, $email, $photoName, $userId);

            $_SESSION["photo"] = $photoName;

        }

    } else {
        // Update name & email only
        $stmt = $conn->prepare(
            "UPDATE users SET name=?, email=? WHERE id=?"
        );
        $stmt->bind_param("ssi", $name, $email, $userId);
    }

    $stmt->execute();

    $_SESSION["name"]  = $name;
    $_SESSION["email"] = $email;

    header("Location: profile.php");
    exit();
}
?>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Profile</title>
    <style>
        body {
            font-family: Arial;
            background: #f4f6f8;
        }
        .box {
            max-width: 400px;
            margin: 80px auto;
            background: #fff;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }
        input, button {
            width: 100%;
            padding: 12px;
            margin: 10px 0;
            border-radius: 8px;
            border: 1px solid #ccc;
        }
        button {
            background: #ff6600;
            color: white;
            border: none;
            cursor: pointer;
        }

        button a {
            color: white;
            text-decoration: none;
        }

    </style>
</head>
<body>

<div class="box">
    <h2>Edit Profile</h2>
    <form method="POST" enctype="multipart/form-data">
        <input type="text" name="name" value="<?php echo $_SESSION["name"]; ?>" required>
        <input type="email" name="email" value="<?php echo $_SESSION["email"]; ?>" required>
        <input type="file" name="photo">
        <button>Update Profile</button>
       <button> <a href="profile.php" class="Back-btn">⬅Back </a></button>
    </form>
</div>

</body>
</html>
