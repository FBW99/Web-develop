<?php
session_start();
require_once "../dbconnect.php";

// 🔒 Admin protection
if (!isset($_SESSION["user_id"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

$message = "";

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $name        = $_POST["name"];
    $price       = $_POST["price"];
    $description = $_POST["description"];
    $imageName   = "";

    // 🖼 Image upload
    if (!empty($_FILES["image"]["name"])) {

        $allowed = ["jpg", "jpeg", "png", "gif"];
        $ext = strtolower(pathinfo($_FILES["image"]["name"], PATHINFO_EXTENSION));

        if (in_array($ext, $allowed)) {

            $imageName = time() . "_" . $_FILES["image"]["name"];
            $target = "../uploads/" . $imageName;
            move_uploaded_file($_FILES["image"]["tmp_name"], $target);

        } else {
            $message = "❌ Invalid image type!";
        }
    }

    if ($message === "") {

        $stmt = $conn->prepare(
            "INSERT INTO products (name, price, description, image) 
             VALUES (?, ?, ?, ?)"
        );
        $stmt->bind_param("sdss", $name, $price, $description, $imageName);
        $stmt->execute();

        header("Location: products.php");
        exit();
    }
}
?>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>
    <style>
        body {
            font-family: Arial;
            background: #f4f6f8;
        }
        .box {
            max-width: 420px;
            margin: 80px auto;
            background: white;
            padding: 25px;
            border-radius: 15px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }
        h2 {
            text-align: center;
        }
        input, textarea, button {
            width: 100%;
            padding: 12px;
            margin: 10px 0;
            border-radius: 8px;
            border: 1px solid #ccc;
        }
        textarea {
            resize: vertical;
            height: 100px;
        }
        button {
            background: #28a745;
            color: white;
            border: none;
            cursor: pointer;
        }
        .error {
            color: red;
            text-align: center;
        }
        .back {
            display: block;
            text-align: center;
            margin-top: 10px;
            text-decoration: none;
            color: #333;
        }
    </style>
</head>
<body>

<div class="box">
    <h2>➕ Add Product</h2>

    <?php if ($message): ?>
        <p class="error"><?php echo $message; ?></p>
    <?php endif; ?>

    <form method="POST" enctype="multipart/form-data">
        <input type="text" name="name" placeholder="Product name" required>
        <input type="number" step="0.01" name="price" placeholder="Price" required>
        <textarea name="description" placeholder="Product description" required></textarea>
        <input type="file" name="image">
        <button>Add Product</button>
    </form>

    <a href="products.php" class="back">⬅ Back to Products</a>
</div>

</body>
</html>
