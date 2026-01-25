<!DOCTYPE html>
<html>

<head>
    <title>Forgot Password</title>

    <style>
        body {
            font-family: Arial;
            background: #f4f6f8;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .box {
            background: #fff;
            padding: 30px;
            border-radius: 10px;
            width: 100%;
            max-width: 400px;
            box-shadow: 0 10px 25px rgba(0, 0, 0, .1);
        }

        h2 {
            text-align: center;
        }

        input,
        button {
            width: 100%;
            padding: 12px;
            margin-top: 10px;
            border-radius: 6px;
            border: 1px solid #ddd;
        }

        button {
            background: #ff6600;
            color: white;
            border: none;
            cursor: pointer;
        }

        button:hover {
            background: #e55b00;
        }

        .success {
            color: green;
            text-align: center;
        }

        .error {
            color: red;
            text-align: center;
        }
    </style>
</head>

<body>

    <div class="box">
        <h2>Forgot Password</h2>

        <?php if (isset($_GET["sent"])): ?>
            <p class="success">Reset link sent to your email</p>
        <?php endif; ?>

        <form action="send-reset.php" method="POST">
            <input type="email" name="email" placeholder="Enter your email" required>
            <button type="submit">Send Reset Link</button>
        </form>

    </div>
</body>

</html>