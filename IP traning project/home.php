<?php
session_start();
require_once "dbconnect.php";
?>
<!DOCTYPE html>
<html lang="en">

<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <!-- fav-icon-logo link -->
  <link
    rel="shortcut icon"
    href="./image/ABM-logo2.jpg"
    type="image/x-icon" />
  <!-- FontAwesome Icons -->
  <link
    rel="stylesheet"
    href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" />
  <!-- main css -->
  <link rel="stylesheet" href="./css/main.css">
  <!-- product css -->
  <link rel="stylesheet" href="./css/product.css">
  <!-- media quary css -->
  <link rel="stylesheet" href="./css/media.css">
  <title>ABM Fashion</title>
</head>

<body>
  <!--========== Header section ========-->
  <header class="header">
    <!-- Logo Section -->
    <div class="logo">
      <a href="home.php"><img src="./image/ABM-logo.jpg" alt="logo" /></a>
      <a href="home.php">
        <h2>ABM Fashion</h2>
      </a>
    </div>

    <!-- Navigation Menu -->
    <nav class="nav-list" id="navlist">
      <a href="#home">Home</a>
      <a href="#aboutUs">About us</a>
      <a href="#product-section">Product</a>
      <a href="#contact">Contact</a>
    </nav>

    <div class="searchandicon">
      <!-- Search Bar -->
      <div class="search-box">
        <input type="text" placeholder="Search product..." />
        <button><i class="fa fa-search"></i></button>
      </div>
      <!-- Icons Section -->
      <div class="icons">

        <div class="profile-wrapper">

          <?php
          $photo = $_SESSION["photo"] ?? "";
          ?>

          <a href="profile.php" class="profile-link">

            <?php if (!empty($photo) && file_exists("uploads/$photo")): ?>
              <img src="uploads/<?php echo $photo; ?>" class="nav-profile-img">
            <?php else: ?>
              <i class="fa fa-user"></i>
            <?php endif; ?>

            <?php if (isset($_SESSION["name"])): ?>
              <span class="nav-username"><?php echo $_SESSION["name"]; ?></span>
            <?php endif; ?>

          </a>

          <!-- Dropdown Menu -->
          <div class="profile-menu">

            <?php if (isset($_SESSION["user_id"])): ?>
              <a href="profile.php">Profile</a>
              <a href="logout.php">Logout</a>
            <?php else: ?>
              <a href="login.php">Login</a>
              <a href="signup.php">Sign Up</a>
            <?php endif; ?>

          </div>

        </div>
        <?php
        $cartCount = 0;
        if (isset($_SESSION['user_id'])) {
          $uid = $_SESSION['user_id'];
          $q = $conn->query(
            "SELECT SUM(quantity) AS total FROM cart WHERE user_id=$uid"
          );
          $cartCount = $q->fetch_assoc()['total'] ?? 0;
        }
        ?>

        <a href="cart.php" class="cart-link">
          <i class="fa fa-shopping-cart"></i>
          <span class="cart-count"><?php echo $cartCount; ?></span>
        </a>

      </div>

      <!-- Menu and Close Icons -->
      <div class="menu-icon">
        <i class="fa-solid fa-bars" id="menuBtn" onclick="showMenu()"></i>
        <i class="fa-solid fa-xmark" id="closeBtn" onclick="closeMenu()"></i>
      </div>
  </header>

  <!-- ========= Home Page Section ========= -->
  <section class="home-wrap" id="home">
    <div class="home-img">
      <img src="./image/home-img.jpg" alt="home image" />
    </div>
    <div class="home-content">
      <h1>ABM</h1>
      <h1>MEN'S FASHION</h1>
      <p>Summer Collection 2025</p>
      <div class="home-btn">
        <a href="#product-section" class="shop-now">SHOP NOW</a>
        <a
          href="https://youtu.be/vG1yg-YWOCI?si=tau57LGxfxi48CIq"
          target="_self"
          class="show-video">VIDEO</a>
      </div>
    </div>
  </section>

  <!-- ======= About us Section ======= -->
  <section class="about" id="aboutUs">
    <div class="about-container">
      <div class="about-img">
        <img src="./image/ABM-logo2.jpg" alt="About ABM Fashion" />
      </div>

      <div class="about-content">
        <h2>About ABM Fashion</h2>
        <h3>Modern & Stylish Wear For Everyone</h3>
        <p>
          ABM Fashion delivers high-quality and trendy clothing for modern
          men. Our 2025 summer collection brings comfort, style, and premium
          designs for daily and special occasions.
        </p>

        <a href="learn-more.php" class="about-btn">Learn More</a>
      </div>
    </div>
  </section>

  <!--  ========= product section ======= -->
  <section class="product-section" id="product-section">
    <h2 class="section-title">Our Products</h2>

    <div class="products">

      <?php
      $sql = "SELECT * FROM products ORDER BY created_at DESC";
      $result = $conn->query($sql);

      if ($result->num_rows > 0):
        while ($row = $result->fetch_assoc()):
      ?>

          <div class="product-card">
            <img src="uploads/<?php echo $row['image']; ?>" alt="Product">
            <h2><?php echo htmlspecialchars($row['name']); ?></h2>
            <p><?php echo htmlspecialchars($row['description']); ?></p>
            <div class="price"><?php echo $row['price']; ?> Br</div>
            <form action="add_to_cart.php" method="POST">
              <input type="hidden" name="product_id" value="<?php echo $row['id']; ?>">
              <button type="submit" class="add-cart-btn">Add to Cart</button>
            </form>
          </div>

      <?php
        endwhile;
      else:
        echo "<p>No products found</p>";
      endif;
      ?>

    </div>
  </section>

  <!-- ================== Contact Section =============== -->
  <section class="contact" id="contact">
    <h2 class="section-title">Contact Us</h2>
    <p class="section-sub-title">
      Get in touch with us anytime—we are here to help you!
    </p>

    <div class="contact-wrapper">
      <!-- Contact Form -->
      <div class="contact-form">
        <h3>Send Us a Message</h3>
        <form action="send-message.php" method="POST">
          <input type="text" name="name" placeholder="Your Name" required />
          <input type="email" name="email" placeholder="Your Email" required />
          <textarea name="message" rows="5" placeholder="Your Message" required></textarea>
          <button type="submit" class="send-btn">Send Message</button>
        </form>

      </div>

      <!-- Contact Info -->
      <div class="contact-info">
        <h3>Contact Information</h3>

        <div class="info-box">
          <i class="fa-solid fa-phone"></i>
          <a href="tel:+251 912 345 678">+251 912 345 678</a>
        </div>

        <div class="info-box">
          <i class="fa-solid fa-envelope"></i>
          <a href="mailto:support@abmshop.com" target="_blank">support@abmshop.com</a>
        </div>

        <div class="info-box">
          <i class="fa-solid fa-location-dot"></i>
          <p>Addis Ababa, Ethiopia</p>
        </div>
      </div>
    </div>

  </section>

  <!-- ================= Footer ================= -->
  <footer class="footer">
    <div class="footer-container">
      <div class="footer-about">
        <h1>ABM Fashion</h1>
        <p>
          Your trusted fashion store for modern clothes, shoes & cultural
          wear.
        </p>
      </div>

      <div class="footer-links">
        <h2>Quick Links</h2>
        <ul>
          <li><a href="#home">Home</a></li>
          <li><a href="#aboutUs">About Us</a></li>
          <li><a href="#product-section">Product</a></li>
        </ul>
      </div>

      <div class="footer-social">
        <h2>Follow Us</h2>
        <div class="social-icons">
          <a href="#"><i class="fa-brands fa-facebook"></i></a>
          <a href="#"><i class="fa-brands fa-instagram"></i></a>
          <a href="#"><i class="fa-brands fa-telegram"></i></a>
          <a href="#"><i class="fa-brands fa-youtube"></i></a>
        </div>

      </div>
    </div>

    <p class="footer-copy">© 2025 ABM Fashion | All Rights Reserved</p>
  </footer>



  <!-- Scroll To Top Button -->
  <button id="scrollTopBtn"><i class="fa-solid fa-arrow-up"></i></button>

  <script src="./js/script.js"></script>
</body>

</html>