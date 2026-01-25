<?php
session_start();
require_once "dbconnect.php";

if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit();
}

$user_id = $_SESSION['user_id'];

/* ===== FETCH CART ITEMS ===== */
$sql = "
SELECT cart.quantity, products.name, products.price
FROM cart
JOIN products ON cart.product_id = products.id
WHERE cart.user_id = ?
";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows == 0) {
    header("Location: cart.php");
    exit();
}

/* ===== CALCULATE TOTAL ===== */
$total_price = 0;
$items = [];

while ($row = $result->fetch_assoc()) {
    $subtotal = $row['price'] * $row['quantity'];
    $total_price += $subtotal;
    $items[] = $row;
}

/* ===== PLACE ORDER ===== */
if (isset($_POST['place_order'])) {

    $name    = $_POST['full_name'];
    $phone   = $_POST['phone'];
    $city    = $_POST['city'];
    $address = $_POST['address'];

    /* ===== INSERT ORDER ===== */
    $stmt = $conn->prepare(
        "INSERT INTO orders (user_id, total_price, order_date)
         VALUES (?, ?, NOW())"
    );
    $stmt->bind_param("id", $user_id, $total_price);
    $stmt->execute();

    // Get order ID
    $order_id = $stmt->insert_id;

    /* ===== INSERT ADDRESS ===== */
    $stmt = $conn->prepare(
        "INSERT INTO order_address (order_id, full_name, phone, city, address)
         VALUES (?, ?, ?, ?, ?)"
    );
    $stmt->bind_param("issss", $order_id, $name, $phone, $city, $address);
    $stmt->execute();

    /* ===== INSERT ORDER ITEMS ===== */
    $sql = "
        SELECT product_id, quantity
        FROM cart
        WHERE user_id = ?
    ";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $cart_items = $stmt->get_result();

    while ($item = $cart_items->fetch_assoc()) {

        // get product price
        $p = $conn->prepare("SELECT price FROM products WHERE id = ?");
        $p->bind_param("i", $item['product_id']);
        $p->execute();
        $price = $p->get_result()->fetch_assoc()['price'];

        // insert order item
        $insert = $conn->prepare(
            "INSERT INTO order_items (order_id, product_id, quantity, price)
             VALUES (?, ?, ?, ?)"
        );
        $insert->bind_param(
            "iiid",
            $order_id,
            $item['product_id'],
            $item['quantity'],
            $price
        );
        $insert->execute();
    }

    /* ===== CLEAR CART ===== */
    $conn->query("DELETE FROM cart WHERE user_id = $user_id");

    header("Location: success.php");
    exit();
}

?>

<!DOCTYPE html>
<html>
<head>
<title>Checkout</title>

<style>
body {
    background: #f4f6f8;
    font-family: Arial, sans-serif;
    padding: 20px;
}

.checkout-container {
    max-width: 650px;
    background: white;
    margin: auto;
    padding: 25px;
    border-radius: 12px;
    box-shadow: 0 6px 18px rgba(0,0,0,0.1);
}

h2 {
    text-align: center;
    margin-bottom: 20px;
}

.item, .total {
    display: flex;
    justify-content: space-between;
    margin-bottom: 10px;
}

.total {
    font-weight: bold;
    font-size: 18px;
}

input, textarea {
    width: 100%;
    padding: 10px;
    margin: 8px 0 15px;
    border: 1px solid #ccc;
    border-radius: 6px;
}

button {
    width: 100%;
    padding: 14px;
    background: #ff6600;
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 16px;
    cursor: pointer;
}

button:hover {
    background: #e55b00;
}
</style>
</head>

<body>

<div class="checkout-container">

<h2>Checkout</h2>

<?php foreach ($items as $item): ?>
<div class="item">
    <span><?= htmlspecialchars($item['name']) ?> × <?= $item['quantity'] ?></span>
    <span><?= number_format($item['price'] * $item['quantity'], 2) ?> Br</span>
</div>
<?php endforeach; ?>

<div class="total">
    <span>Total</span>
    <span><?= number_format($total_price, 2) ?> Br</span>
</div>

<form method="POST">

    <h3>Shipping Details</h3>

    <input type="text" name="full_name" placeholder="Full Name" required>
    <input type="text" name="phone" placeholder="Phone Number" required>
    <input type="text" name="city" placeholder="City" required>
    <textarea name="address" placeholder="Full Address" required></textarea>

    <button name="place_order">Place Order</button>
</form>

<a href="cart.php">⬅ Back to Cart</a>

</div>

</body>
</html>
