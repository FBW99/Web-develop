<?php
session_start();
session_destroy();
header("Location: home.php");
exit();
?>




//session_start(); 

/* Remove all session data */
// session_unset();

/* Destroy session */
// session_destroy();

/* Redirect to login page */
// header("Location: ../login.php");
// exit(); 