<?php
session_start();
require_once "../dbconnect.php";

if (!isset($_SESSION["role"]) || $_SESSION["role"] !== "admin") {
    header("Location: ../login.php");
    exit();
}

$id = $_GET["id"];

$msg = $conn->query("
    SELECT * FROM contact_messages WHERE id = $id
")->fetch_assoc();

if (isset($_POST["send_reply"])) {
    $reply = $_POST["reply"];

    // send email
    mail(
        $msg["email"],
        "Reply from ABM Shop",
        $reply,
        "From: fashionabm825@gmail.com"
    );

    // update DB
    $stmt = $conn->prepare("
        UPDATE contact_messages
        SET reply=?, status='Replied'
        WHERE id=?
    ");
    $stmt->bind_param("si", $reply, $id);
    $stmt->execute();

    header("Location: messages.php");
    exit();
}
?>

<!DOCTYPE html>
<html>

<head>
    <title>Reply Message</title>
    <style>
        body {
            font-family: Arial;
            background: #f4f6f8;
            padding: 20px;
        }

        .box {
            max-width: 600px;
            background: #fff;
            padding: 20px;
            border-radius: 10px;
            margin: auto;
        }

        textarea {
            width: 100%;
            padding: 10px;
            margin-top: 10px;
        }

        button {
            margin-top: 10px;
            padding: 10px;
            background: #ff6600;
            color: #fff;
            border: none;
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

    <div class="box">
        <h3>Reply to <?= htmlspecialchars($msg["name"]) ?></h3>

        <p><b>Message:</b><br><?= nl2br(htmlspecialchars($msg["message"])) ?></p>

        <form method="POST">
            <textarea name="reply" rows="5" required></textarea>
            <button name="send_reply">Send Reply</button>
        </form>
    </div>

     <a href="messages.php" class="back">⬅ Back to Messages</a>
    
</body>

</html>