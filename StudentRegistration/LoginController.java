package Controller;

import Controller.AdminController;
import Model.DbConnection;
import Model.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.scene.control.DialogPane;
import java.net.URL;

public class LoginController {
    
    @FXML
    private TextField studentIdField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button signInButton;
    
    @FXML
    private Button signUpButton;
    
    @FXML
    private Button closeButton;
    
    @FXML
    private CheckBox showPasswordCheckbox;
    
    @FXML
    private Button forgotButton;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private TextField visiblePasswordField;
    
    private double xOffset = 0;
    private double yOffset = 0;
    
    @FXML
    public void initialize() {
        System.out.println("LoginController initialized!");
        
        // Initialize visible password field (hidden by default)
        if (visiblePasswordField != null) {
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
        
        setupEventHandlers();
        
        // Make window draggable
        setupDraggableWindow();
        
        // Debug: Check available resources
        debugResourceFiles();
        
        // Add some sample test credentials for demonstration
        // studentIdField.setText("nsr018716");
        // passwordField.setText("admin123");
    }
    
    private void setupEventHandlers() {
        // Close button handler
        if (closeButton != null) {
            closeButton.setOnAction(event -> {
                Stage stage = (Stage) closeButton.getScene().getWindow();
                stage.close();
            });
            
            // Add hover effect
            closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"));
            closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d;"));
        }
        
        // Sign in button handler
        if (signInButton != null) {
            signInButton.setOnAction(event -> handleSignIn());
            
            // Add hover effect
            signInButton.setOnMouseEntered(e -> {
                signInButton.setStyle(
                    "-fx-background-color: #2980b9;" +
                    "-fx-text-fill: white;" +
                    "-fx-effect: dropshadow(gaussian, rgba(41, 128, 185, 0.4), 10, 0, 0, 0);"
                );
            });
            
            signInButton.setOnMouseExited(e -> {
                signInButton.setStyle(
                    "-fx-background-color: #3498db;" +
                    "-fx-text-fill: white;" +
                    "-fx-effect: dropshadow(gaussian, rgba(52, 152, 219, 0.3), 10, 0, 0, 0);"
                );
            });
        }
        
        // Sign up button handler
        if (signUpButton != null) {
            signUpButton.setOnAction(event -> Signup());
            
            // Add hover effect
            signUpButton.setOnMouseEntered(e -> {
                signUpButton.setStyle(
                    "-fx-background-color: #27ae60;" +
                    "-fx-text-fill: white;" +
                    "-fx-effect: dropshadow(gaussian, rgba(39, 174, 96, 0.4), 10, 0, 0, 0);"
                );
            });
            
            signUpButton.setOnMouseExited(e -> {
                signUpButton.setStyle(
                    "-fx-background-color: #2ecc71;" +
                    "-fx-text-fill: white;" +
                    "-fx-effect: dropshadow(gaussian, rgba(46, 204, 113, 0.3), 10, 0, 0, 0);"
                );
            });
        }
        
        // Forgot password button handler
        if (forgotButton != null) {
            forgotButton.setOnAction(event -> handleForgotPassword());
            
            // Add hover effect
            forgotButton.setOnMouseEntered(e -> forgotButton.setStyle("-fx-text-fill: #3498db; -fx-underline: true;"));
            forgotButton.setOnMouseExited(e -> forgotButton.setStyle("-fx-text-fill: #7f8c8d; -fx-underline: false;"));
        }
        
        // Show password checkbox handler
        if (showPasswordCheckbox != null && visiblePasswordField != null) {
            showPasswordCheckbox.setOnAction(event -> togglePasswordVisibility());
        }
        
        // Add Enter key support for login
        if (studentIdField != null) {
            studentIdField.setOnAction(event -> passwordField.requestFocus());
        }
        
        if (passwordField != null) {
            passwordField.setOnAction(event -> handleSignIn());
        }
    }
    
    private void setupDraggableWindow() {
        // Make the main card draggable
        if (studentIdField != null && studentIdField.getScene() != null) {
            Parent root = studentIdField.getScene().getRoot();
            
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            
            root.setOnMouseDragged(event -> {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });
        }
    }
    
    @FXML
    private void handleSignIn() {
        String studentId = studentIdField.getText().trim();
        String password = passwordField.getText().trim();
        
        System.out.println("\n=== Login Attempt ===");
        System.out.println("StudentID: " + studentId);
        
        // Update status
        if (statusLabel != null) {
            statusLabel.setText("Authenticating...");
            statusLabel.setStyle("-fx-text-fill: #f39c12;");
        }
        
        // Basic validation
        if (studentId.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter both Student ID and Password");
            if (statusLabel != null) {
                statusLabel.setText("Please enter credentials");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
            return;
        }
        
        // Validate student ID format (optional)
        if (!studentId.matches("^[A-Za-z0-9]+$")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid Student ID format");
            if (statusLabel != null) {
                statusLabel.setText("Invalid ID format");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
            return;
        }
        
        // Use the Account class to validate credentials against database
        String role = Account.login(studentId, password);
        
        if (role != null) {
            System.out.println("✓ Login successful! Role: " + role);
            if (statusLabel != null) {
                statusLabel.setText("Login successful!");
                statusLabel.setStyle("-fx-text-fill: #27ae60;");
            }
            
            // Add a small delay for better UX
            new Thread(() -> {
                try {
                    Thread.sleep(500); // 0.5 second delay
                    javafx.application.Platform.runLater(() -> {
                        // Show success message
                        showAlert(Alert.AlertType.INFORMATION, "Success", 
                                 "Login successful!\nWelcome, " + studentId + 
                                 "\nRole: " + role.toUpperCase());
                        
                        // After successful login, navigate to the appropriate panel
                        loadMainApplication(studentId, role);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            
        } else {
            System.out.println("✗ Invalid credentials");
            if (statusLabel != null) {
                statusLabel.setText("Invalid credentials");
                statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            }
            
            // Shake animation for wrong credentials
            shakeTextField(studentIdField);
            shakeTextField(passwordField);
            
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Invalid Student ID or Password\n\nPlease check your credentials and try again.");
        }
    }
    
   // signUpButton
// Corrected Signup method
private void Signup() {
    try {
        // Try to load the signup FXML
        URL fxmlUrl = getClass().getResource("/View/signup.fxml");
        if (fxmlUrl == null) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Signup form not found. Please make sure 'signup.fxml' exists in the student package.");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 500, 400));
        stage.setTitle("Create Account");
        stage.setResizable(false);
        
        // Apply CSS if available
        try {
            URL cssUrl = getClass().getResource("/View/login.css");
            if (cssUrl != null) {
                root.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.out.println("CSS not found for signup form: " + e.getMessage());
        }
        
        stage.show();
        
    } catch (Exception e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Error", 
                 "Could not load signup form.\n\n" +
                 "The signup.fxml file has an error at line 13.\n" +
                 "Please check the file for invalid type declarations.\n\n" +
                 "Error: " + e.getMessage());
    }
}   
    
    @FXML
    private void handleForgotPassword() {
        try {
            // Load forgot password FXML if exists
            FXMLLoader loader = new FXMLLoader(getClass().getResource("forgot-password.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 500, 400));
            
            // Apply CSS
            try {
                root.getStylesheets().add(getClass().getResource("/View/login.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS not found: " + e.getMessage());
            }
            
            stage.setTitle("Reset Password");
            stage.setResizable(false);
            stage.show();
            
        } catch (IOException e) {
            // Fallback if forgot password form doesn't exist
            showAlert(Alert.AlertType.INFORMATION, "Forgot Password", 
                     "Password Recovery System\n\n" +
                     "1. Contact your system administrator\n" +
                     "2. Visit: IT Help Desk (Room 101)\n" +
                     "3. Email: support@university.edu\n" +
                     "4. Phone: (123) 456-7890\n\n" +
                     "Please have your Student ID ready for verification.");
        }
    }
    
    private void togglePasswordVisibility() {
        if (showPasswordCheckbox.isSelected()) {
            // Show password
            visiblePasswordField.setText(passwordField.getText());
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            
            // Move focus to visible field
            visiblePasswordField.requestFocus();
            visiblePasswordField.positionCaret(visiblePasswordField.getText().length());
        } else {
            // Hide password
            passwordField.setText(visiblePasswordField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            
            // Move focus to password field
            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }
    
    private void loadMainApplication(String userId, String role) {
        try {
            String fxmlFile;
            String windowTitle;
            int windowWidth, windowHeight;
            
            // Use the FXML files that actually exist
            if ("admin".equalsIgnoreCase(role)) {
                fxmlFile = "/View/adminPanel.fxml";  // This should match your actual FXML file name
                windowTitle = "Admin panel - Student Management System";
                windowWidth = 1200;
                windowHeight = 700;
            } else {
                fxmlFile = "/View/Userpanel.fxml";  // This should match your actual FXML file name
                windowTitle = "Student Portal - " + userId;
                windowWidth = 1000;
                windowHeight = 600;
            }
            
            System.out.println("Attempting to load FXML: " + fxmlFile);
            System.out.println("For role: " + role);
            
            // Check if FXML file exists
            URL resourceUrl = getClass().getResource(fxmlFile);
            if (resourceUrl == null) {
                System.err.println("ERROR: FXML file not found: " + fxmlFile);
                System.err.println("Current directory: " + System.getProperty("user.dir"));
                System.err.println("Class location: " + getClass().getProtectionDomain().getCodeSource().getLocation());
                
                // List available resources
                System.out.println("\nAvailable FXML files:");
                String[] files = {"login.fxml", "adminDashboard.fxml", "userDashboard.fxml", 
                                 "admin-panel.fxml", "user-panel.fxml", "Admin.fxml", "User.fxml"};
                for (String file : files) {
                    URL url = getClass().getResource(file);
                    System.out.println(file + " -> " + (url != null ? "FOUND" : "NOT FOUND"));
                }
                
                showAlert(Alert.AlertType.ERROR, "Resource Error", 
                         "FXML file '" + fxmlFile + "' not found!\n\n" +
                         "Please make sure the file exists in the same directory as LoginController.java\n" +
                         "Expected location: student/" + fxmlFile);
                return;
            }
            
            System.out.println("✓ FXML file found at: " + resourceUrl);
            
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();
            
            // Get user full name from database
            String fullName = getUserFullName(userId);
            
            // Set user data in controller
            if ("admin".equalsIgnoreCase(role)) {
                AdminController controller = loader.getController();
                if (controller != null) {
                    controller.setAdminData(userId);
                    System.out.println("✓ AdminController initialized");
                }
            } else {
                UserController controller = loader.getController();
                if (controller != null) {
                    controller.setUserData(userId, fullName != null ? fullName : "Student");
                    System.out.println("✓ UserController initialized");
                }
            }
            
            // Create scene
            Scene scene = new Scene(root, windowWidth, windowHeight);
            
            // Apply CSS if available
            if ("admin".equalsIgnoreCase(role)) {
                try {
                    URL cssUrl = getClass().getResource("admin-styles.css");
                    if (cssUrl != null) {
                        scene.getStylesheets().add(cssUrl.toExternalForm());
                        System.out.println("✓ Admin CSS loaded");
                    } else {
                        System.out.println("⚠ Admin CSS not found");
                    }
                } catch (Exception e) {
                    System.out.println("⚠ Could not load admin CSS: " + e.getMessage());
                }
            } else {
                try {
                    URL cssUrl = getClass().getResource("user-styles.css");
                    if (cssUrl != null) {
                        scene.getStylesheets().add(cssUrl.toExternalForm());
                        System.out.println("✓ User CSS loaded");
                    } else {
                        System.out.println("⚠ User CSS not found");
                    }
                } catch (Exception e) {
                    System.out.println("⚠ Could not load user CSS: " + e.getMessage());
                }
            }
            
            // Create and show stage
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(windowTitle);
            stage.setResizable(true);
            
            // Center the window
            stage.centerOnScreen();
            
            // Show window
            stage.show();
            
            // Maximize window for better experience
            stage.setMaximized(true);
            
            // Close login window
            Stage loginStage = (Stage) signInButton.getScene().getWindow();
            loginStage.close();
            
            System.out.println("✓ " + role + " dashboard loaded successfully!");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("✗ Error loading FXML: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Loading Error", 
                     "Failed to load " + ("admin".equalsIgnoreCase(role) ? "Admin" : "User") + 
                     " Panel!\n\nError: " + e.getMessage() +
                     "\n\nMake sure the FXML file exists and is properly formatted.");
            
            // Try to load fallback window
            loadFallbackWindow(userId, role);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("✗ Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Unexpected Error", 
                     "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    private String getUserFullName(String userId) {
        try (Connection conn = new DbConnection().connectDb()) {
            if (conn != null) {
                // Try to get from students table first
                String sql = "SELECT full_name FROM students WHERE studentID = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, userId);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    return rs.getString("full_name");
                }
                
                // If not in students table, try account table
                sql = "SELECT username FROM account WHERE studentID = ?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, userId);
                rs = ps.executeQuery();
                
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching user full name: " + e.getMessage());
        }
        return null;
    }
    
    private void loadFallbackWindow(String userId, String role) {
        try {
            Stage stage = new Stage();
            javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(20);
            root.setPadding(new javafx.geometry.Insets(20));
            root.setStyle("-fx-background-color: #f8f9fa; -fx-alignment: center;");
            
            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label(
                role.equalsIgnoreCase("admin") ? "Admin Panel" : "Student Panel"
            );
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            javafx.scene.control.Label userLabel = new javafx.scene.control.Label("Welcome, " + userId);
            userLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #3498db;");
            
            javafx.scene.control.Label roleLabel = new javafx.scene.control.Label("Role: " + role.toUpperCase());
            roleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
            
            javafx.scene.control.Label infoLabel = new javafx.scene.control.Label(
                "The " + (role.equalsIgnoreCase("admin") ? "Admin" : "User") + 
                " Panel interface could not be loaded.\nPossible reasons:\n" +
                "1. FXML file is missing\n" +
                "2. FXML file has errors\n" +
                "3. Controller class mismatch"
            );
            infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c; -fx-text-alignment: center;");
            infoLabel.setWrapText(true);
            
            javafx.scene.control.Button logoutButton = new javafx.scene.control.Button("Logout");
            logoutButton.setStyle(
                "-fx-background-color: #e74c3c; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10 30; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;"
            );
            
            logoutButton.setOnAction(e -> {
                stage.close();
                // Reload login window
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
                    Parent loginRoot = loader.load();
                    Stage loginStage = new Stage();
                    loginStage.setScene(new Scene(loginRoot, 900, 550));
                    loginStage.setTitle("Student Registration System - Login");
                    loginStage.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            
            root.getChildren().addAll(titleLabel, userLabel, roleLabel, infoLabel, logoutButton);
            
            Scene scene = new Scene(root, 500, 400);
            stage.setScene(scene);
            stage.setTitle("Fallback Window");
            stage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Customize alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #ecf0f1; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 5px; " +
            "-fx-background-radius: 5px;"
        );
        
        // Set graphic based on alert type
        if (alertType == Alert.AlertType.ERROR) {
            dialogPane.setGraphic(new javafx.scene.control.Label("❌"));
        } else if (alertType == Alert.AlertType.INFORMATION) {
            dialogPane.setGraphic(new javafx.scene.control.Label("ℹ️"));
        } else if (alertType == Alert.AlertType.WARNING) {
            dialogPane.setGraphic(new javafx.scene.control.Label("⚠️"));
        }
        
        alert.showAndWait();
    }
    
    private void shakeTextField(javafx.scene.Node node) {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline();
        
        timeline.getKeyFrames().addAll(
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(0), 
                new javafx.animation.KeyValue(node.translateXProperty(), 0)),
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(100), 
                new javafx.animation.KeyValue(node.translateXProperty(), -10)),
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(200), 
                new javafx.animation.KeyValue(node.translateXProperty(), 10)),
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(300), 
                new javafx.animation.KeyValue(node.translateXProperty(), -10)),
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(400), 
                new javafx.animation.KeyValue(node.translateXProperty(), 10)),
            new javafx.animation.KeyFrame(javafx.util.Duration.millis(500), 
                new javafx.animation.KeyValue(node.translateXProperty(), 0))
        );
        
        timeline.play();
    }
    
    private void debugResourceFiles() {
        System.out.println("\n=== DEBUG: Available FXML Resources ===");
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        System.out.println("Class location: " + getClass().getProtectionDomain().getCodeSource().getLocation());
        
        String[] filesToCheck = {
            "login.fxml",
            "adminDashboard.fxml",
            "userDashboard.fxml",
            "admin-panel.fxml",
            "user-panel.fxml",
            "Admin.fxml",
            "User.fxml",
            "register.fxml",
            "forgot-password.fxml"
        };
        
        for (String file : filesToCheck) {
            URL url = getClass().getResource(file);
            System.out.println(file + " -> " + (url != null ? "✓ FOUND" : "✗ NOT FOUND"));
        }
        System.out.println("=== END DEBUG ===\n");
    }
    
    // Method to clear the form
    public void clearForm() {
        studentIdField.clear();
        passwordField.clear();
        if (visiblePasswordField != null) {
            visiblePasswordField.clear();
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
        }
        if (showPasswordCheckbox != null) {
            showPasswordCheckbox.setSelected(false);
        }
        if (statusLabel != null) {
            statusLabel.setText("");
        }
        
        // Reset focus
        studentIdField.requestFocus();
    }
    
    // Method to simulate login (for testing)
    public void simulateLogin(String studentId, String password) {
        studentIdField.setText(studentId);
        passwordField.setText(password);
        handleSignIn();
    }
    
    // Getter methods for testing
    public String getStudentId() {
        return studentIdField.getText();
    }
    
    public String getPassword() {
        return passwordField.getText();
    }
    
    // Event handlers for additional interactions
    @FXML
    private void handleMouseEnterSignIn() {
        if (signInButton != null) {
            signInButton.setStyle(
                "-fx-background-color: #2980b9;" +
                "-fx-text-fill: white;" +
                "-fx-effect: dropshadow(gaussian, rgba(41, 128, 185, 0.4), 10, 0, 0, 0);"
            );
        }
    }
    
    @FXML
    private void handleMouseExitSignIn() {
        if (signInButton != null) {
            signInButton.setStyle(
                "-fx-background-color: #3498db;" +
                "-fx-text-fill: white;" +
                "-fx-effect: dropshadow(gaussian, rgba(52, 152, 219, 0.3), 10, 0, 0, 0);"
            );
        }
    }
    
    @FXML
    private void handleHelpRequest() {
        showAlert(Alert.AlertType.INFORMATION, "Help & Support", 
                 "Student Registration System Help\n\n" +
                 "1. Login Issues:\n" +
                 "   • Ensure you're using correct Student ID\n" +
                 "   • Check Caps Lock is off\n" +
                 "   • Contact IT if password reset needed\n\n" +
                 "2. Technical Support:\n" +
                 "   • Email: it-support@university.edu\n" +
                 "   • Phone: Ext. 1234\n" +
                 "   • Office: Building A, Room 205\n\n" +
                 "3. Hours:\n" +
                 "   • Monday-Friday: 8 AM - 6 PM\n" +
                 "   • Saturday: 9 AM - 1 PM");
    }
}