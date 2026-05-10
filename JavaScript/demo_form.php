<!DOCTYPE html>
<html>
<head>
    <title>Form Submission Result</title>
</head>
<body>

<h2>Form Submitted Successfully</h2>

<?php
// Check if form data is received using POST
if ($_SERVER["REQUEST_METHOD"] == "POST") {

    // Get the first name safely
    $fname = $_POST["fname"];

    // Display the submitted value
    echo "<p><strong>First Name:</strong> " . htmlspecialchars($fname) . "</p>";
}
?>

</body>
</html>
