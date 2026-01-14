<?php
setcookie("login_user", "", time() - 3600, "/");
header("Location: login1.php");
?>
