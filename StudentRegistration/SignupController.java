package Controller;

import Model.Account;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class SignupController implements Initializable {

    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirmPassword;

    @FXML
    private Button signUpButton;

    @FXML
    private Button loginbtn;

    @FXML
    private void signup() {
        String user = username.getText().trim();
        String pass = password.getText();
        String confirm = confirmPassword.getText();

        if (user.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required");
            return;
        }

        if (!pass.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Password Error", "Passwords do not match");
            return;
        }

        if (pass.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Weak Password", "Password must be at least 6 characters");
            return;
        }

        Account account = new Account();

        if (account.userExists(user)) {
            showAlert(Alert.AlertType.ERROR, "Signup Failed", "Username already exists");
            return;
        }

        boolean success = account.register(user, pass, "user");

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Signup Success", "Account created successfully");
            openLogin();
        } else {
            showAlert(Alert.AlertType.ERROR, "Signup Failed", "Could not create account");
        }
    }

    @FXML
    private void openLogin() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 900, 550));
            stage.setTitle("Student Registration System - Login");
            stage.show();
            
            // Close signup window
            Stage currentStage = (Stage) loginbtn.getScene().getWindow();
            currentStage.close();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not load login screen");
        }
    }
    
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) username.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Add event handlers for better UX
        if (signUpButton != null) {
            signUpButton.setOnMouseEntered(e -> 
                signUpButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;"));
            signUpButton.setOnMouseExited(e -> 
                signUpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;"));
        }
        
        if (loginbtn != null) {
            loginbtn.setOnMouseEntered(e -> 
                loginbtn.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;"));
            loginbtn.setOnMouseExited(e -> 
                loginbtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;"));
        }
    }
}