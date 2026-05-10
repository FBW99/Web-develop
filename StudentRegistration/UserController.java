package Controller;

import Model.DbConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    
    @FXML private Label userNameLabel;
    @FXML private Label userIdLabel;
    @FXML private Label userRoleLabel;
    @FXML private Label enrolledCoursesLabel;
    @FXML private Label gpaLabel;
    @FXML private Label attendanceLabel;
    
    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseCodeCol;
    @FXML private TableColumn<Course, String> courseNameCol;
    @FXML private TableColumn<Course, String> instructorCol;
    @FXML private TableColumn<Course, String> gradeCol;
    @FXML private TableColumn<Course, String> semesterCol;
    @FXML private TableColumn<Course, String> creditsCol;
    
    @FXML private Button dashboardBtn;
    @FXML private Button coursesBtn;
    @FXML private Button gradesBtn;
    @FXML private Button profileBtn;
    @FXML private Button logoutBtn;
    @FXML private Button refreshBtn;
    @FXML private Button enrollCourseBtn;
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> semesterFilter;
    
    private String studentId;
    private String studentName;
    private ObservableList<Course> coursesData = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        System.out.println("User Controller initialized!");
        
        // Initialize table columns
        initializeTableColumns();
        
        // Initialize semester filter
        initializeSemesterFilter();
        
        // Setup event handlers
        setupEventHandlers();
        
        // Setup search functionality
        setupSearchFilter();
    }
    
    private void initializeTableColumns() {
        if (coursesTable != null) {
            courseCodeCol.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
            courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
            instructorCol.setCellValueFactory(new PropertyValueFactory<>("instructor"));
            gradeCol.setCellValueFactory(new PropertyValueFactory<>("grade"));
            semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));
            creditsCol.setCellValueFactory(new PropertyValueFactory<>("credits"));
            
            coursesTable.setItems(coursesData);
        }
    }
    
    private void initializeSemesterFilter() {
        if (semesterFilter != null) {
            semesterFilter.getItems().addAll("All", "Fall 2024", "Spring 2024", "Summer 2024", "Fall 2023");
            semesterFilter.setValue("All");
            
            semesterFilter.setOnAction(e -> filterCourses());
        }
    }
    
    private void setupEventHandlers() {
        if (dashboardBtn != null) {
            dashboardBtn.setOnAction(event -> loadDashboard());
        }
        
        if (coursesBtn != null) {
            coursesBtn.setOnAction(event -> loadCourses());
        }
        

        if (logoutBtn != null) {
            logoutBtn.setOnAction(event -> handleLogout());
        }
        
        if (refreshBtn != null) {
            refreshBtn.setOnAction(event -> refreshData());
        }
        
        if (enrollCourseBtn != null) {
            enrollCourseBtn.setOnAction(event -> enrollNewCourse());
        }
    }
    
    private void setupSearchFilter() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterCourses();
            });
        }
    }
    
    public void setUserData(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
        
        if (userNameLabel != null) {
            userNameLabel.setText(studentName != null ? studentName : "Student");
        }
        
        if (userIdLabel != null) {
            userIdLabel.setText("ID: " + studentId);
        }
        
        if (userRoleLabel != null) {
            userRoleLabel.setText("Student");
        }
        
        loadStudentData();
        loadCoursesData();
        calculateStatistics();
    }
    
    private void loadStudentData() {
        try (Connection conn = new DbConnection().connectDb()) {
            if (conn != null) {
                String sql = "SELECT * FROM students WHERE student_id = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    String email = rs.getString("email");
                    String phone = rs.getString("phone");
                    String department = rs.getString("department");
                    
                    System.out.println("Student loaded: " + studentName);
                    System.out.println("Email: " + email);
                    System.out.println("Department: " + department);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load student data: " + e.getMessage());
        }
    }
    
    private void loadCoursesData() {
        coursesData.clear();
        
        try (Connection conn = new DbConnection().connectDb()) {
            if (conn != null) {
                String sql = """
                    SELECT c.course_code, c.course_name, c.instructor, 
                           c.semester, c.credits, e.grade
                    FROM courses c
                    JOIN enrollments e ON c.course_id = e.course_id
                    WHERE e.student_id = ? AND e.status = 'active'
                    ORDER BY c.semester DESC
                    """;
                
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                
                int courseCount = 0;
                while (rs.next()) {
                    Course course = new Course(
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getString("instructor"),
                        rs.getString("grade"),
                        rs.getString("semester"),
                        rs.getInt("credits")
                    );
                    coursesData.add(course);
                    courseCount++;
                }
                
                System.out.println("Loaded " + courseCount + " courses for student " + studentId);
                
                // Update enrolled courses count
                if (enrolledCoursesLabel != null && courseCount > 0) {
                    enrolledCoursesLabel.setText(String.valueOf(courseCount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load courses: " + e.getMessage());
        }
    }
    
    private void calculateStatistics() {
        try (Connection conn = new DbConnection().connectDb()) {
            if (conn != null) {
                // Calculate GPA
                String gpaSql = """
                    SELECT AVG(
                        CASE grade
                            WHEN 'A' THEN 4.0
                            WHEN 'A-' THEN 3.7
                            WHEN 'B+' THEN 3.3
                            WHEN 'B' THEN 3.0
                            WHEN 'B-' THEN 2.7
                            WHEN 'C+' THEN 2.3
                            WHEN 'C' THEN 2.0
                            WHEN 'D' THEN 1.0
                            ELSE 0.0
                        END
                    ) as gpa
                    FROM enrollments 
                    WHERE student_id = ? AND grade IS NOT NULL
                    """;
                
                PreparedStatement gpaPs = conn.prepareStatement(gpaSql);
                gpaPs.setString(1, studentId);
                ResultSet gpaRs = gpaPs.executeQuery();
                
                if (gpaRs.next()) {
                    double gpa = gpaRs.getDouble("gpa");
                    if (gpaLabel != null) {
                        gpaLabel.setText(String.format("%.2f", gpa));
                    }
                }
                
                // Calculate attendance percentage
                String attendanceSql = """
                    SELECT 
                        (SUM(CASE WHEN status = 'present' THEN 1 ELSE 0 END) * 100.0 / 
                        COUNT(*)) as attendance_percentage
                    FROM attendance 
                    WHERE student_id = ?
                    """;
                
                PreparedStatement attPs = conn.prepareStatement(attendanceSql);
                attPs.setString(1, studentId);
                ResultSet attRs = attPs.executeQuery();
                
                if (attRs.next()) {
                    double attendance = attRs.getDouble("attendance_percentage");
                    if (attendanceLabel != null) {
                        attendanceLabel.setText(String.format("%.1f%%", attendance));
                    }
                }
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void filterCourses() {
        String searchText = searchField.getText().toLowerCase();
        String selectedSemester = semesterFilter.getValue();
        
        ObservableList<Course> filteredList = FXCollections.observableArrayList();
        
        for (Course course : coursesData) {
            boolean matchesSearch = searchText.isEmpty() ||
                course.getCourseCode().toLowerCase().contains(searchText) ||
                course.getCourseName().toLowerCase().contains(searchText) ||
                course.getInstructor().toLowerCase().contains(searchText);
            
            boolean matchesSemester = selectedSemester.equals("All") ||
                course.getSemester().equals(selectedSemester);
            
            if (matchesSearch && matchesSemester) {
                filteredList.add(course);
            }
        }
        
        coursesTable.setItems(filteredList);
    }
    
    @FXML
    private void loadDashboard() {
        System.out.println("Dashboard loaded");
        // Dashboard is already the main view
        refreshData();
    }
    
    @FXML
    private void loadCourses() {
        // Show all courses available for enrollment
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/courseCatalog.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 1000, 700));
            stage.setTitle("Course Catalog - " + studentName);
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load course catalog: " + e.getMessage());
        }
    }
    

    
    @FXML
    private void enrollNewCourse() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Enroll in Course");
        dialog.setHeaderText("Enter Course Code");
        dialog.setContentText("Course Code:");
        
        dialog.showAndWait().ifPresent(courseCode -> {
            try (Connection conn = new DbConnection().connectDb()) {
                // Check if course exists
                String checkSql = "SELECT course_id FROM courses WHERE course_code = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkSql);
                checkPs.setString(1, courseCode);
                ResultSet rs = checkPs.executeQuery();
                
                if (rs.next()) {
                    String courseId = rs.getString("course_id");
                    
                    // Check if already enrolled
                    String enrollCheckSql = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
                    PreparedStatement enrollCheckPs = conn.prepareStatement(enrollCheckSql);
                    enrollCheckPs.setString(1, studentId);
                    enrollCheckPs.setString(2, courseId);
                    ResultSet enrollRs = enrollCheckPs.executeQuery();
                    
                    if (enrollRs.next()) {
                        showAlert("Already Enrolled", "You are already enrolled in this course.");
                    } else {
                        // Enroll student
                        String enrollSql = "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, 'active')";
                        PreparedStatement enrollPs = conn.prepareStatement(enrollSql);
                        enrollPs.setString(1, studentId);
                        enrollPs.setString(2, courseId);
                        enrollPs.executeUpdate();
                        
                        showAlert("Success", "Successfully enrolled in course: " + courseCode);
                        refreshData();
                    }
                } else {
                    showAlert("Course Not Found", "Course with code '" + courseCode + "' not found.");
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Database Error", "Failed to enroll: " + e.getMessage());
            }
        });
    }
    
    @FXML
    private void refreshData() {
        loadCoursesData();
        calculateStatistics();
        showAlert("Refreshed", "Data has been refreshed successfully.");
    }
    
    @FXML
    private void handleLogout() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Logout");
        confirmAlert.setHeaderText("Are you sure you want to logout?");
        confirmAlert.setContentText("You will be redirected to the login page.");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/login.fxml"));
                Parent root = loader.load();
                
                Stage stage = new Stage();
                stage.setScene(new Scene(root, 900, 550));
                stage.setTitle("Student Registration System - Login");
                stage.show();
                
                Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
                currentStage.close();
                
                System.out.println("✓ User logged out: " + studentName);
                
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to load login screen: " + e.getMessage());
            }
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Course model class
    public static class Course {
        private final String courseCode;
        private final String courseName;
        private final String instructor;
        private final String grade;
        private final String semester;
        private final int credits;
        
        public Course(String courseCode, String courseName, String instructor, 
                     String grade, String semester, int credits) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.instructor = instructor;
            this.grade = grade;
            this.semester = semester;
            this.credits = credits;
        }
        
        public String getCourseCode() { return courseCode; }
        public String getCourseName() { return courseName; }
        public String getInstructor() { return instructor; }
        public String getGrade() { return grade; }
        public String getSemester() { return semester; }
        public int getCredits() { return credits; }
    }
    
    // Getters for testing
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public ObservableList<Course> getCoursesData() { return coursesData; }
}
