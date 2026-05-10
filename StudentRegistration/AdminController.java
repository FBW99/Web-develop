package Controller;

import Model.DbConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.stage.StageStyle;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminController implements Initializable {
    
    @FXML private Label adminNameLabel;
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalCoursesLabel;
    @FXML private Label activeUsersLabel;
    
    @FXML private Button logoutButton;
    @FXML private Button refreshAllBtn;
    @FXML private Button addStudentBtn;
    @FXML private Button editStudentBtn;
    @FXML private Button deleteStudentButton;
    @FXML private Button addCourseButton;
    @FXML private Button editCourseButton;
    @FXML private Button deleteCourseButton;
    @FXML private Button generateReportBtn;
    @FXML private Button systemSettingsBtn;
    @FXML private Button backupBtn;
    @FXML private Button userActivityBtn;
    
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, String> studentIdColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;
    @FXML private TableColumn<Student, String> programColumn;
    
    @FXML private TabPane adminTabs;
    @FXML private TextField searchStudentField;
    
    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseInstructorColumn;
    @FXML private TableColumn<Course, Integer> courseCapacityColumn;
    
    private ObservableList<Student> studentList = FXCollections.observableArrayList();
    private ObservableList<Course> courseList = FXCollections.observableArrayList();
    private String adminId;
    private DbConnection dbConnection = new DbConnection();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("AdminController initialized!");
        
        // Initialize student table columns
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        programColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        
        studentsTable.setItems(studentList);
        
        // Initialize course table columns
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseInstructorColumn.setCellValueFactory(new PropertyValueFactory<>("instructor"));
        courseCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        
        coursesTable.setItems(courseList);
        
        // Set up button actions
        setupButtonActions();
        
        // Show database structure for debugging
        showDatabaseStructure();
    }
    
    private void showDatabaseStructure() {
        try (Connection conn = dbConnection.connectDb()) {
            if (conn != null) {
                System.out.println("\n=== DATABASE STRUCTURE DEBUG ===");
                
                // Show all tables
                System.out.println("Available tables:");
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
                while (tables.next()) {
                    System.out.println("Table: " + tables.getString("TABLE_NAME"));
                }
                
                // Show student_table columns in detail
                System.out.println("\nColumns in 'student_table' table:");
                ResultSet columns = meta.getColumns(null, null, "student_table", "%");
                while (columns.next()) {
                    String colName = columns.getString("COLUMN_NAME");
                    String colType = columns.getString("TYPE_NAME");
                    int colSize = columns.getInt("COLUMN_SIZE");
                    int nullable = columns.getInt("NULLABLE");
                    String isNullable = (nullable == 1) ? "NULL" : "NOT NULL";
                    System.out.println("  " + colName + " (" + colType + "(" + colSize + ")) " + isNullable);
                }
                
                // Check for any constraints
                System.out.println("\nPrimary keys in 'student_table' table:");
                ResultSet primaryKeys = meta.getPrimaryKeys(null, null, "student_table");
                while (primaryKeys.next()) {
                    System.out.println("  PK: " + primaryKeys.getString("COLUMN_NAME"));
                }
                
                System.out.println("=== END DEBUG ===\n");
            }
        } catch (SQLException e) {
            System.out.println("Error checking database structure: " + e.getMessage());
        }
    }
    
    private void setupButtonActions() {
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> logout());
        }
        
        if (refreshAllBtn != null) {
            refreshAllBtn.setOnAction(e -> refreshData());
        }
        
        if (addStudentBtn != null) {
            addStudentBtn.setOnAction(e -> showAddStudentDialog());
        }
        
        if (editStudentBtn != null) {
            editStudentBtn.setOnAction(e -> editStudent());
        }
        
        if (deleteStudentButton != null) {
            deleteStudentButton.setOnAction(e -> deleteStudent());
        }
        
        if (addCourseButton != null) {
            addCourseButton.setOnAction(e -> showAddCourseDialog());
        }
        
        if (editCourseButton != null) {
            editCourseButton.setOnAction(e -> editCourse());
        }
        
        if (deleteCourseButton != null) {
            deleteCourseButton.setOnAction(e -> deleteCourse());
        }
        
        if (generateReportBtn != null) {
            generateReportBtn.setOnAction(e -> generateReport());
        }
        
        if (systemSettingsBtn != null) {
            systemSettingsBtn.setOnAction(e -> systemSettings());
        }
        
        if (backupBtn != null) {
            backupBtn.setOnAction(e -> backupDatabase());
        }
        
        if (userActivityBtn != null) {
            userActivityBtn.setOnAction(e -> userActivityLog());
        }
    }
    
    // Method to set admin data from login
    public void setAdminData(String adminId) {
        this.adminId = adminId;
        if (adminNameLabel != null) {
            adminNameLabel.setText("Welcome, Admin " + adminId);
        }
        loadStatistics();
        loadStudentsFromDatabase();
    }
    // for easy to use, we use db connection in Admin Controller
    private void loadStatistics() {
        try (Connection conn = dbConnection.connectDb()) {
            if (conn != null) {
                // Count total students
                String sql = "SELECT COUNT(*) as total FROM student_table";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                if (rs.next()) {
                    totalStudentsLabel.setText(String.valueOf(rs.getInt("total")));
                }
                
                // Count active users (all students for now)
                activeUsersLabel.setText(totalStudentsLabel.getText());
                
                // Try to count courses if table exists
                try {
                    sql = "SELECT COUNT(*) as total FROM courses";
                    rs = stmt.executeQuery(sql);
                    if (rs.next()) {
                        totalCoursesLabel.setText(String.valueOf(rs.getInt("total")));
                    } else {
                        totalCoursesLabel.setText("0");
                    }
                } catch (SQLException e) {
                    totalCoursesLabel.setText("0");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            totalStudentsLabel.setText("0");
            totalCoursesLabel.setText("0");
            activeUsersLabel.setText("0");
        }
    }
    
    private void loadStudentsFromDatabase() {
        studentList.clear();
        try (Connection conn = dbConnection.connectDb()) {
            if (conn != null) {
                // Try different column name combinations
                String sql;
                try {
                    // Try with fullname first
                    sql = "SELECT studentID, fullname, email, phone, address, enrollment_date, major, status FROM student_table";
                } catch (Exception e) {
                    // If that fails, try with full_name
                    sql = "SELECT studentID, full_name, email, phone, address, enrollment_date, major, status FROM student_table";
                }
                
                System.out.println("Loading students with query: " + sql);
                
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                
                while (rs.next()) {
                    Student student = new Student(
                        rs.getString("studentID"),
                        getColumnValue(rs, "fullname", "full_name"), // Try both column names
                        rs.getString("email"),
                        rs.getString("major"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("enrollment_date"),
                        rs.getString("status")
                    );
                    studentList.add(student);
                }
                
                System.out.println("Loaded " + studentList.size() + " students from database");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load students: " + e.getMessage());
        }
    }
    
    private String getColumnValue(ResultSet rs, String... columnNames) throws SQLException {
        for (String columnName : columnNames) {
            try {
                String value = rs.getString(columnName);
                if (value != null) {
                    return value;
                }
            } catch (SQLException e) {
                // Try next column name
            }
        }
        return "";
    }
    
    private void refreshData() {
        loadStudentsFromDatabase();
        loadStatistics();
        showAlert("Success", "Data refreshed successfully!");
    }
    
    private void showAddStudentDialog() {
        // First get the actual column names and constraints
        List<ColumnInfo> columnInfoList = getStudentTableColumnsInfo();
        
        // Create a custom dialog
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Register New Student");
        dialog.setHeaderText("Enter student details");
        dialog.initStyle(StageStyle.UTILITY);
        
        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Create form fields
        TextField studentIdField = new TextField();
        studentIdField.setPromptText("Student ID (e.g., STU001)");
        
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Full Name");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        
        TextField majorField = new TextField();
        majorField.setPromptText("Major");
        
        TextField enrollmentDateField = new TextField();
        enrollmentDateField.setPromptText("YYYY-MM-DD");
        enrollmentDateField.setText(LocalDate.now().toString());
        
        // Add fields to grid
        int row = 0;
        
        // Find which column is actually for full name
        String fullNameColumn = findFullNameColumn(columnInfoList);
        
        grid.add(new Label("Student ID*:"), 0, row);
        grid.add(studentIdField, 1, row++);
        
        grid.add(new Label("Full Name*:"), 0, row);
        grid.add(fullNameField, 1, row++);
        
        grid.add(new Label("Email*:"), 0, row);
        grid.add(emailField, 1, row++);
        
        grid.add(new Label("Phone:"), 0, row);
        grid.add(phoneField, 1, row++);
        
        grid.add(new Label("Address:"), 0, row);
        grid.add(addressField, 1, row++);
        
        grid.add(new Label("Major:"), 0, row);
        grid.add(majorField, 1, row++);
        
        grid.add(new Label("Enrollment Date:"), 0, row);
        grid.add(enrollmentDateField, 1, row++);
        
        dialog.getDialogPane().setContent(grid);
        
        // Enable/Disable add button
        javafx.scene.Node addButton = dialog.getDialogPane().lookupButton(addButtonType);
        addButton.setDisable(true);
        
        // Validate required fields
        Runnable validateFields = () -> {
            boolean valid = !studentIdField.getText().trim().isEmpty() &&
                           !fullNameField.getText().trim().isEmpty() &&
                           !emailField.getText().trim().isEmpty();
            addButton.setDisable(!valid);
        };
        
        studentIdField.textProperty().addListener((obs, old, newVal) -> validateFields.run());
        fullNameField.textProperty().addListener((obs, old, newVal) -> validateFields.run());
        emailField.textProperty().addListener((obs, old, newVal) -> validateFields.run());
        
        // Convert the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Student(
                    studentIdField.getText().trim(),
                    fullNameField.getText().trim(),
                    emailField.getText().trim(),
                    majorField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    enrollmentDateField.getText().trim(),
                    "Active"
                );
            }
            return null;
        });
        
        // Show the dialog
        dialog.showAndWait().ifPresent(student -> {
            if (addStudentToDatabase(student, columnInfoList)) {
                studentList.add(student);
                loadStatistics();
                showAlert("Success", "Student added successfully!\nID: " + student.getStudentId());
            }
        });
    }
    
    private String findFullNameColumn(List<ColumnInfo> columnInfoList) {
        for (ColumnInfo info : columnInfoList) {
            if (info.name.equalsIgnoreCase("fullname") || info.name.equalsIgnoreCase("full_name")) {
                return info.name;
            }
        }
        return "fullname"; // Default
    }
    
    private List<ColumnInfo> getStudentTableColumnsInfo() {
        List<ColumnInfo> columns = new ArrayList<>();
        try (Connection conn = dbConnection.connectDb()) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet rs = meta.getColumns(null, null, "student_table", "%");
                while (rs.next()) {
                    ColumnInfo info = new ColumnInfo();
                    info.name = rs.getString("COLUMN_NAME");
                    info.type = rs.getString("TYPE_NAME");
                    info.nullable = rs.getInt("NULLABLE") == 1;
                    info.defaultValue = rs.getString("COLUMN_DEF");
                    columns.add(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }
    
    class ColumnInfo {
        String name;
        String type;
        boolean nullable;
        String defaultValue;
    }
    
    private boolean addStudentToDatabase(Student student, List<ColumnInfo> columnInfoList) {
        try (Connection conn = dbConnection.connectDb()) {
            if (conn != null) {
                // Check if student already exists
                String checkSql = "SELECT COUNT(*) FROM student_table WHERE studentID = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, student.getStudentId());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    showAlert("Error", "Student with ID " + student.getStudentId() + " already exists!");
                    return false;
                }
                
                // Build INSERT query based on actual column names
                StringBuilder sqlBuilder = new StringBuilder("INSERT INTO student_table (");
                StringBuilder valuesBuilder = new StringBuilder(" VALUES (");
                List<Object> params = new ArrayList<>();
                
                // Map student fields to actual column names
                for (ColumnInfo column : columnInfoList) {
                    if (column.name.equalsIgnoreCase("studentID")) {
                        sqlBuilder.append("studentID, ");
                        valuesBuilder.append("?, ");
                        params.add(student.getStudentId());
                    } 
                    else if (column.name.equalsIgnoreCase("fullname") || column.name.equalsIgnoreCase("full_name")) {
                        sqlBuilder.append(column.name).append(", ");
                        valuesBuilder.append("?, ");
                        params.add(student.getFullName());
                    }
                    else if (column.name.equalsIgnoreCase("email")) {
                        sqlBuilder.append("email, ");
                        valuesBuilder.append("?, ");
                        params.add(student.getEmail());
                    }
                    else if (column.name.equalsIgnoreCase("phone")) {
                        sqlBuilder.append("phone, ");
                        valuesBuilder.append("?, ");
                        params.add(student.getPhone());
                    }
                    else if (column.name.equalsIgnoreCase("address")) {
                        sqlBuilder.append("address, ");
                        valuesBuilder.append("?, ");
                        params.add(student.getAddress());
                    }
                    else if (column.name.equalsIgnoreCase("enrollment_date")) {
                        sqlBuilder.append("enrollment_date, ");
                        valuesBuilder.append("?, ");
                        params.add(student.getEnrollmentDate());
                    }
                    else if (column.name.equalsIgnoreCase("major")) {
                        sqlBuilder.append("major, ");
                        valuesBuilder.append("?, ");
                        params.add(student.getMajor());
                    }
                    else if (column.name.equalsIgnoreCase("status")) {
                        sqlBuilder.append("status, ");
                        valuesBuilder.append("?, ");
                        params.add(student.getStatus());
                    }
                }
                
                // Remove trailing comma and space
                String sql = sqlBuilder.toString();
                if (sql.endsWith(", ")) {
                    sql = sql.substring(0, sql.length() - 2);
                }
                sql += ")";
                
                String values = valuesBuilder.toString();
                if (values.endsWith(", ")) {
                    values = values.substring(0, values.length() - 2);
                }
                values += ")";
                
                sql += values;
                
                System.out.println("Insert SQL: " + sql);
                System.out.println("Params: " + params);
                
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for (int i = 0; i < params.size(); i++) {
                    Object param = params.get(i);
                    if (param == null) {
                        pstmt.setNull(i + 1, Types.VARCHAR);
                    } else {
                        pstmt.setObject(i + 1, param);
                    }
                }
                
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    createStudentAccount(student);
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to add student:\n" + e.getMessage() + 
                     "\n\nSQL State: " + e.getSQLState() +
                     "\nError Code: " + e.getErrorCode());
        }
        return false;
    }
    
    private void createStudentAccount(Student student) {
        try (Connection conn = dbConnection.connectDb()) {
            if (conn != null) {
                // Check if account table exists
                DatabaseMetaData meta = conn.getMetaData();
                ResultSet tables = meta.getTables(null, null, "account", new String[]{"TABLE"});
                
                if (tables.next()) {
                    String defaultPassword = student.getStudentId() + "123";
                    
                    // Check if account already exists
                    String checkSql = "SELECT COUNT(*) FROM account WHERE studentID = ?";
                    PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                    checkStmt.setString(1, student.getStudentId());
                    ResultSet rs = checkStmt.executeQuery();
                    
                    if (rs.next() && rs.getInt(1) == 0) {
                        String sql = "INSERT INTO account (studentID, username, password, role) VALUES (?, ?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, student.getStudentId());
                        pstmt.setString(2, student.getFullName());
                        pstmt.setString(3, defaultPassword);
                        pstmt.setString(4, "user");
                        
                        pstmt.executeUpdate();
                        System.out.println("Account created for student: " + student.getStudentId());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Could not create account: " + e.getMessage());
        }
    }
    
    private void editStudent() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            showEditStudentDialog(selected);
        } else {
            showAlert("Warning", "Please select a student to edit");
        }
    }
    
    private void showEditStudentDialog(Student student) {
        // Create edit dialog
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("Edit Student");
        dialog.setHeaderText("Edit student details");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        // Display student ID (read-only)
        Label studentIdLabel = new Label(student.getStudentId());
        studentIdLabel.setStyle("-fx-font-weight: bold;");
        
        // Editable fields
        TextField fullNameField = new TextField(student.getFullName());
        TextField emailField = new TextField(student.getEmail());
        TextField phoneField = new TextField(student.getPhone());
        TextField addressField = new TextField(student.getAddress());
        TextField majorField = new TextField(student.getMajor());
        
        int row = 0;
        grid.add(new Label("Student ID:"), 0, row);
        grid.add(studentIdLabel, 1, row++);
        
        grid.add(new Label("Full Name*:"), 0, row);
        grid.add(fullNameField, 1, row++);
        
        grid.add(new Label("Email*:"), 0, row);
        grid.add(emailField, 1, row++);
        
        grid.add(new Label("Phone:"), 0, row);
        grid.add(phoneField, 1, row++);
        
        grid.add(new Label("Address:"), 0, row);
        grid.add(addressField, 1, row++);
        
        grid.add(new Label("Major:"), 0, row);
        grid.add(majorField, 1, row++);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Student(
                    student.getStudentId(),
                    fullNameField.getText().trim(),
                    emailField.getText().trim(),
                    majorField.getText().trim(),
                    phoneField.getText().trim(),
                    addressField.getText().trim(),
                    student.getEnrollmentDate(),
                    student.getStatus()
                );
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(updatedStudent -> {
            if (updateStudentInDatabase(updatedStudent)) {
                int index = studentList.indexOf(student);
                studentList.set(index, updatedStudent);
                showAlert("Success", "Student updated successfully!");
            }
        });
    }
    
    private boolean updateStudentInDatabase(Student student) {
        try (Connection conn = dbConnection.connectDb()) {
            if (conn != null) {
                // Try with fullname first, then fall back to full_name
                String fullNameColumn;
                try {
                    // Check which column exists
                    DatabaseMetaData meta = conn.getMetaData();
                    ResultSet columns = meta.getColumns(null, null, "student_table", "fullname");
                    if (columns.next()) {
                        fullNameColumn = "fullname";
                    } else {
                        fullNameColumn = "full_name";
                    }
                } catch (Exception e) {
                    fullNameColumn = "fullname";
                }
                
                String sql = "UPDATE student_table SET " + fullNameColumn + " = ?, email = ?, phone = ?, address = ?, major = ? WHERE studentID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, student.getFullName());
                pstmt.setString(2, student.getEmail());
                pstmt.setString(3, student.getPhone());
                pstmt.setString(4, student.getAddress());
                pstmt.setString(5, student.getMajor());
                pstmt.setString(6, student.getStudentId());
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to update student: " + e.getMessage());
        }
        return false;
    }
    
    private void deleteStudent() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Delete");
            confirmAlert.setHeaderText("Delete Student");
            confirmAlert.setContentText("Are you sure you want to delete student: " + selected.getFullName() + "?");
            
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                if (deleteStudentFromDatabase(selected.getStudentId())) {
                    studentList.remove(selected);
                    loadStatistics();
                    showAlert("Success", "Student deleted successfully!");
                }
            }
        } else {
            showAlert("Warning", "Please select a student to delete");
        }
    }
    
    private boolean deleteStudentFromDatabase(String studentId) {
        try (Connection conn = dbConnection.connectDb()) {
            if (conn != null) {
                String sql = "DELETE FROM student_table WHERE studentID = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, studentId);
                
                int rowsAffected = pstmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to delete student: " + e.getMessage());
        }
        return false;
    }
    
    private void logout() {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) logoutButton.getScene().getWindow();
            stage.close();
            
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("login.fxml"));
            javafx.scene.Parent root = loader.load();
            
            javafx.stage.Stage loginStage = new javafx.stage.Stage();
            loginStage.setScene(new javafx.scene.Scene(root, 900, 550));
            loginStage.setTitle("Student Management System - Login");
            loginStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }
    
    private void showAddCourseDialog() {
        showAlert("Info", "Add Course functionality to be implemented");
    }
    
    private void editCourse() {
        showAlert("Info", "Edit Course functionality to be implemented");
    }
    
    private void deleteCourse() {
        showAlert("Info", "Delete Course functionality to be implemented");
    }
    
    private void generateReport() {
        showAlert("Info", "Generate Report functionality to be implemented");
    }
    
    private void systemSettings() {
        showAlert("Info", "System Settings functionality to be implemented");
    }
    
    private void backupDatabase() {
        showAlert("Info", "Backup Database functionality to be implemented");
    }
    
    private void userActivityLog() {
        showAlert("Info", "User Activity Log functionality to be implemented");
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Student class
    public static class Student {
        private String studentId;
        private String fullName;
        private String email;
        private String major;
        private String phone;
        private String address;
        private String enrollmentDate;
        private String status;
        
        public Student(String studentId, String fullName, String email, String major) {
            this(studentId, fullName, email, major, "", "", "", "Active");
        }
        
        public Student(String studentId, String fullName, String email, String major, 
                      String phone, String address, String enrollmentDate, String status) {
            this.studentId = studentId;
            this.fullName = fullName;
            this.email = email;
            this.major = major;
            this.phone = phone;
            this.address = address;
            this.enrollmentDate = enrollmentDate;
            this.status = status;
        }
        
        // Getters
        public String getStudentId() { return studentId; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getMajor() { return major; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getEnrollmentDate() { return enrollmentDate; }
        public String getStatus() { return status; }
        
        // Setters
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public void setEmail(String email) { this.email = email; }
        public void setMajor(String major) { this.major = major; }
        public void setPhone(String phone) { this.phone = phone; }
        public void setAddress(String address) { this.address = address; }
        public void setEnrollmentDate(String enrollmentDate) { this.enrollmentDate = enrollmentDate; }
        public void setStatus(String status) { this.status = status; }
    }
    
    // Course class
    public static class Course {
        private String courseCode;
        private String courseName;
        private String instructor;
        private int capacity;
        
        public Course(String courseCode, String courseName, String instructor, int capacity) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.instructor = instructor;
            this.capacity = capacity;
        }
        
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getInstructor() { return instructor; }
        public int getCapacity() { return capacity; }
        
        public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
        public void setCourseName(String courseName) { this.courseName = courseName; }
        public void setInstructor(String instructor) { this.instructor = instructor; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
    }
}