<?php
session_start();
require_once "dbconnect.php";

$message = "";
$messageSucc = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $email = trim($_POST["email"]);
    $password = $_POST["password"];

    // prepare query
    $sql = $conn->prepare("SELECT * FROM users WHERE email = ? LIMIT 1");
    $sql->bind_param("s", $email);
    $sql->execute();
    $result = $sql->get_result();

    // user found?
    if ($result->num_rows === 0) {

        $message = "❌ No account found!";

    } else {

        $user = $result->fetch_assoc();

        // verify password
        if (password_verify($password, $user["password"])) {

            // ✅ STORE DATA IN SESSION
            $_SESSION["user_id"] = $user["id"];
            $_SESSION["name"]    = $user["name"];
            $_SESSION["email"]   = $user["email"];
            $_SESSION["photo"]   = $user["photo"];
            $_SESSION["role"]    = $user["role"];   // 🔥 IMPORTANT LINE

            $messageSucc = "✅ Login Successful! Redirecting...";

            // 🔀 REDIRECT BASED ON ROLE
            if ($user["role"] === "admin") {
                header("Location: admin/dashboard.php");
            } else {
                header("Location: home.php");
            }

            exit();

        } else {
            $message = "❌ Incorrect password!";
        }
    }
}
?>



<!DOCTYPE html>
<html>

<head>
    <title>Login</title>
    <style>
        body {
            background: #f4f4f4;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            width: 380px;
            background: #fff;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #222;
        }

        input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 13px;
            outline: none;
            margin-bottom: 15px;
        }

        input:focus {
            border-color: #ff6600;
        }

        .btn {
            width: 100%;
            padding: 12px;
            background-color: #ff6600;
            border: none;
            color: #fff;
            font-size: 16px;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn:hover {
            background: #222;
        }

        p {
            text-align: center;
            margin-top: 15px;
        }

        a {
            text-decoration: none;
            color: #ff6600;
            font-weight: bold;
        }

        p a:hover {
            text-decoration: underline;
        }
        .message {
            color: red;
            margin-bottom: 10px;
        }

        .messageSucc {
            color: green;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>

    <div class="container">
        <h2>Login</h2>

        <p class="message"><?= $message ?></p>
        <p class="messageSucc"><?= $messageSucc ?></p>

        <form method="POST">

            <input type="email" name="email" placeholder="Email Address" required>

            <input type="password" name="password" placeholder="Password" required>

            <button class="btn">Login</button>
        </form>

        <p style="margin-top:10px;">
        <a href="forgot-password.php">Forgot Password?</a>
    </p>

        <p style="margin-top:10px;">Don’t have an account? <a href="signup.php">Sign Up</a></p>
    </div>

</body>

</html>