<?php
session_start();
require_once "../dbconnect.php";

// 🔒 Admin protection
if (!isset($_SESSION["user_id"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

$id = $_GET["id"] ?? 0;

$stmt = $conn->prepare("SELECT * FROM products WHERE id=?");
$stmt->bind_param("i", $id);
$stmt->execute();
$product = $stmt->get_result()->fetch_assoc();

if (!$product) {
    header("Location: products.php");
    exit();
}

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $name = $_POST["name"];
    $price = $_POST["price"];
    $description = $_POST["description"];
    $imageName = $product["image"];

    // New image upload
    if (!empty($_FILES["image"]["name"])) {
        $imageName = time() . "_" . $_FILES["image"]["name"];
        move_uploaded_file($_FILES["image"]["tmp_name"], "../uploads/" . $imageName);
    }

    $stmt = $conn->prepare(
        "UPDATE products SET name=?, price=?, description=?, image=? WHERE id=?"
    );
    $stmt->bind_param("sdssi", $name, $price, $description, $imageName, $id);
    $stmt->execute();

    header("Location: products.php");
    exit();
}
?>

<!DOCTYPE html>
<html>

<head>
    <title>Edit Product</title>
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
        }

        input,
        textarea,
        button {
            width: 100%;
            padding: 12px;
            margin: 10px 0;
        }

        img {
            width: 80px;
            display: block;
            margin-bottom: 10px;
        }

        button {
            background: #ff9800;
            color: white;
            border: none;
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
        <h2>Edit Product</h2>

        <form method="POST" enctype="multipart/form-data">
            <input type="text" name="name" value="<?php echo $product["name"]; ?>" required>
            <input type="number" step="0.01" name="price" value="<?php echo $product["price"]; ?>" required>
            <textarea name="description"><?php echo $product["description"]; ?></textarea>

            <?php if ($product["image"]): ?>
                <img src="../uploads/<?php echo $product["image"]; ?>">
            <?php endif; ?>

            <input type="file" name="image">
            <button>Update Product</button>
            <a href="products.php" class="back">⬅ Back to Products</a>
        </form>
    </div>

</body>

</html>