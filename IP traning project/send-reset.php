<?php
require_once "dbconnect.php";

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

require 'phpmailer/PHPMailer.php';
require 'phpmailer/SMTP.php';
require 'phpmailer/Exception.php';

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $email = $_POST["email"];

    $token = bin2hex(random_bytes(32));
    $expiry = date("Y-m-d H:i:s", strtotime("+1 hour"));

    $stmt = $conn->prepare("
        UPDATE users 
        SET reset_token=?, token_expiry=?
        WHERE email=?
    ");
    $stmt->bind_param("sss", $token, $expiry, $email);
    $stmt->execute();

    if ($stmt->affected_rows > 0) {

        $reset_link = "http://abmfashionsqwe.infinityfreeapp.com/reset-password.php?token=$token";

        $mail = new PHPMailer(true);

        try {
            $mail->isSMTP();
            $mail->Host       = 'smtp.gmail.com';
            $mail->SMTPAuth   = true;
            $mail->Username   = 'etemporary738@gmail.com';
            $mail->Password   = 'nxzcczbfmjradhmr'; // Gmail App Password
            $mail->SMTPSecure = 'tls';
            $mail->Port       = 587;

            $mail->setFrom('yourgmail@gmail.com', 'ABM Shop');
            $mail->addAddress($email);

            $mail->isHTML(true);
            $mail->Subject = 'Password Reset';
            $mail->Body    = "
                <p>Click the link below to reset your password:</p>
                <a href='$reset_link'>$reset_link</a>
            ";

            $mail->send();
        } catch (Exception $e) {
            // silently fail (security)
        }
    }

    header("Location: forgot-password.php?sent=1");
    exit();
}
