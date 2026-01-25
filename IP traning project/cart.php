<?php
session_start();
require_once "dbconnect.php";

if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}

$user_id = $_SESSION['user_id'];

$sql = "
SELECT 
  cart.id AS cart_id,
  cart.quantity,
  products.name,
  products.price,
  products.image
FROM cart
JOIN products ON cart.product_id = products.id
WHERE cart.user_id = ?
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$total = 0;
?>

<!DOCTYPE html>
<html>
<head>
<title>My Cart</title>

<style>
body {
    font-family: Arial, sans-serif;
    background: #f4f6f8;
    margin: 0;
}

/* Container */
.cart-container {
    max-width: 900px;
    margin: 80px auto;
    padding: 20px;
}

.cart-item {
    display: grid;
    grid-template-columns: 80px 1fr 120px;
    gap: 15px;
    background: #fff;
    padding: 15px;
    border-radius: 10px;
    margin-bottom: 15px;
    align-items: center;
}

/* Image */
.cart-item img {
    width: 80px;
    height: 80px;
    object-fit: cover;
    border-radius: 8px;
}

/* Details */
.cart-details h4 {
    margin: 0 0 6px;
}

.cart-details p {
    margin: 4px 0;
    font-size: 14px;
}

.cart-details span {
    color: #ff6600;
    font-weight: bold;
}

/* Quantity box */
.qty-box {
    display: flex;
    align-items: center;
    gap: 8px;
}

.qty-box a {
    width: 28px;
    height: 28px;
    background: #ff6600;
    color: white;
    text-decoration: none;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    font-weight: bold;
}

.qty-box a:hover {
    background: #e55b00;
}

.qty-number {
    font-weight: bold;
    min-width: 20px;
    text-align: center;
}

/* Remove */
.remove-btn {
    background: #ff3b3b;
    color: white;
    padding: 8px 10px;
    border-radius: 6px;
    text-decoration: none;
    font-size: 14px;
}

.remove-btn:hover {
    background: #e60000;
}

/* Total */
.cart-total {
    background: white;
    padding: 20px;
    border-radius: 10px;
    text-align: right;
}

.checkout-btn {
    display: inline-block;
    margin-top: 10px;
    background: #ff6600;
    color: white;
    padding: 12px 25px;
    border-radius: 8px;
    text-decoration: none;
}

.back-home {
    text-decoration: none;
    background-color: #ff6600;
    color: white;
    padding: 5px 15px;
    border-radius: 10px;
}

/* Mobile */
@media (max-width: 600px) {
    .cart-item {
        grid-template-columns: 1fr;
        text-align: center;
    }
    .qty-box {
        justify-content: center;
    }
    .cart-total {
        text-align: center;
    }
}
</style>
</head>

<body>

<div class="cart-container">
    
<h2>Your Cart</h2>

<?php if ($result->num_rows == 0): ?>
    <p>Your cart is empty</p>
<?php else: ?>

<?php while ($row = $result->fetch_assoc()):
    $subtotal = $row['price'] * $row['quantity'];
    $total += $subtotal;
?>

<div class="cart-item">

    <img src="uploads/<?php echo $row['image']; ?>">

    <div class="cart-details">
        <h4><?php echo htmlspecialchars($row['name']); ?></h4>

        <p>Price: <span><?php echo $row['price']; ?> Br</span></p>

        <div class="qty-box">
            <a href="update_cart.php?id=<?php echo $row['cart_id']; ?>&action=minus">−</a>
            <div class="qty-number"><?php echo $row['quantity']; ?></div>
            <a href="update_cart.php?id=<?php echo $row['cart_id']; ?>&action=plus">+</a>
        </div>

        <p>Subtotal: <span><?php echo $subtotal; ?> Br</span></p>
    </div>

    <a class="remove-btn" href="remove_cart.php?id=<?php echo $row['cart_id']; ?>">Remove</a>

</div>

<?php endwhile; ?>

<div class="cart-total">
    <h3>Total: <?php echo $total; ?> Br</h3>
    <a href="checkout.php" class="checkout-btn">Checkout</a>
</div>


<?php endif; ?>
<a href="home.php#product-section" class="back-home">⬅ Back to Home</a>
</div>

</body>
</html>
