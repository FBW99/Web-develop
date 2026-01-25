<?php
require_once "dbconnect.php";

$message = "";
$messageSuccess = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $name = $_POST["name"];
    $email = $_POST["email"];
    $password = $_POST["password"];
    $confirm = $_POST["confirm"];

    if ($password !== $confirm) {
        $message = "❌ Passwords do not match!";
    } else {
        // Check if email exists
        $check = $conn->prepare("SELECT * FROM users WHERE email = ?");
        $check->bind_param("s", $email);
        $check->execute();
        $result = $check->get_result();

        if ($result->num_rows > 0) {
            $message = "❌ Email already registered!";
        } else {
            $hashed = password_hash($password, PASSWORD_DEFAULT);

            $insert = $conn->prepare("INSERT INTO users(name, email, password) VALUES (?, ?, ?)");
            $insert->bind_param("sss", $name, $email, $hashed);

            if ($insert->execute()) {
                $messageSuccess = "✅ Signup Successful!";
                header("refresh:2; url=login.php");
            } else {
                $message = "❌ Something went wrong!";
            }
        }
    }
}
?>

<!DOCTYPE html>
<html>

<head>
    <title>Sign Up</title>
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
            margin-bottom: 20px;
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

        .messageSucc {
            color: green;
            margin-bottom: 10px;
        }

        .message {
            color: red;
            margin-bottom: 10px;
        }

        .login-link {
            margin-top: 15px;
            text-align: center;
            font-size: 14px;
        }

        .login-link a {
            color: #ff6600;
            text-decoration: none;
            font-weight: bold;
        }

        .login-link a:hover {
            text-decoration: underline;
        }


        /* Responsive styles for tablets */
        @media(max-width: 768px) {
            .container {
                max-width: 90%;
                padding: 25px;
            }

            h2 {
                font-size: 22px;
            }

            input {
                padding: 10px;
                margin-bottom: 15px;
            }

            .btn {
                padding: 10px;
                font-size: 15px;
            }
        }

        /* Responsive styles for mobile phones */
        @media(max-width: 480px) {
            body {
                padding: 15px;
                align-items: flex-start;
                padding-top: 30px;
            }

            .container {
                padding: 20px;
                max-width: 100%;
                border-radius: 8px;
            }

            h2 {
                font-size: 20px;
                margin-bottom: 15px;
            }

            input {
                padding: 12px;
                font-size: 16px;
                /* Better for mobile touch */
                margin-bottom: 15px;
            }

            .btn {
                padding: 12px;
                font-size: 16px;
            }

            .login-link {
                font-size: 13px;
            }
        }
    </style>
</head>

<body>

    <div class="container">
        <h2>Create Account</h2>

        <p class="messageSucc"><?= $messageSuccess ?></p>
        <p class="message"><?= $message ?></p>

        <form method="POST">

            <input type="text" name="name" placeholder="Full Name" required>

            <input type="email" name="email" placeholder="Email Address" required>

            <input type="password" name="password" placeholder="Password" required>

            <input type="password" name="confirm" placeholder="Confirm Password" required>

            <button class="btn">Sign Up</button>
        </form>

        <div class="login-link">
            Already have an account? <a href="login.php">Login</a>
        </div>
    </div>

</body>

</html>