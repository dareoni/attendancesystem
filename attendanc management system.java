import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

public class AttendanceManagementSystem extends JFrame {
    private JTabbedPane tabbedPane;
    private AdminPanel adminPanel;
    private StudentPanel studentPanel;
    private FacultyPanel facultyPanel;
    private HashMap<String, User> users;
    private ArrayList<Course> courses;

    public AttendanceManagementSystem() {
        setTitle("Attendance Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        users = new HashMap<>();
        courses = new ArrayList<>();

        tabbedPane = new JTabbedPane();
        adminPanel = new AdminPanel();
        studentPanel = new StudentPanel();
        facultyPanel = new FacultyPanel();

        tabbedPane.addTab("Admin", adminPanel);
        tabbedPane.addTab("Student", studentPanel);
        tabbedPane.addTab("Faculty", facultyPanel);

        add(tabbedPane);

        setVisible(true);
    }

    private class AdminPanel extends JPanel {
        private JTextField userIdField, nameField, passwordField;
        private JComboBox<String> roleComboBox;
        private JButton addUserButton, addCourseButton;

        public AdminPanel() {
            setLayout(new GridLayout(6, 2));

            add(new JLabel("User ID:"));
            userIdField = new JTextField();
            add(userIdField);

            add(new JLabel("Name:"));
            nameField = new JTextField();
            add(nameField);

            add(new JLabel("Password:"));
            passwordField = new JTextField();
            add(passwordField);

            add(new JLabel("Role:"));
            roleComboBox = new JComboBox<>(new String[]{"Student", "Faculty"});
            add(roleComboBox);

            addUserButton = new JButton("Add User");
            addUserButton.addActionListener(e -> addUser());
            add(addUserButton);

            addCourseButton = new JButton("Add Course");
            addCourseButton.addActionListener(e -> addCourse());
            add(addCourseButton);
        }

        private void addUser() {
            String userId = userIdField.getText();
            String name = nameField.getText();
            String password = passwordField.getText();
            String role = (String) roleComboBox.getSelectedItem();

            if (role.equals("Student")) {
                users.put(userId, new Student(userId, name, password));
            } else {
                users.put(userId, new Faculty(userId, name, password));
            }

            JOptionPane.showMessageDialog(this, "User added successfully!");
        }

        private void addCourse() {
            String courseId = JOptionPane.showInputDialog("Enter Course ID:");
            String courseName = JOptionPane.showInputDialog("Enter Course Name:");
            courses.add(new Course(courseId, courseName));
            JOptionPane.showMessageDialog(this, "Course added successfully!");
        }
    }

    private class StudentPanel extends JPanel {
        private JTextField studentIdField;
        private JButton viewAttendanceButton;

        public StudentPanel() {
            setLayout(new GridLayout(2, 2));

            add(new JLabel("Student ID:"));
            studentIdField = new JTextField();
            add(studentIdField);

            viewAttendanceButton = new JButton("View Attendance");
            viewAttendanceButton.addActionListener(e -> viewAttendance());
            add(viewAttendanceButton);
        }

        private void viewAttendance() {
            String studentId = studentIdField.getText();
            User user = users.get(studentId);
            if (user instanceof Student) {
                Student student = (Student) user;
                StringBuilder attendanceInfo = new StringBuilder("Attendance:\n");
                for (Course course : courses) {
                    attendanceInfo.append(course.getName()).append(": ")
                            .append(student.getAttendance(course)).append("%\n");
                }
                JOptionPane.showMessageDialog(this, attendanceInfo.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Student ID!");
            }
        }
    }

    private class FacultyPanel extends JPanel {
        private JTextField facultyIdField, studentIdField, courseIdField;
        private JButton markAttendanceButton;

        public FacultyPanel() {
            setLayout(new GridLayout(4, 2));

            add(new JLabel("Faculty ID:"));
            facultyIdField = new JTextField();
            add(facultyIdField);

            add(new JLabel("Student ID:"));
            studentIdField = new JTextField();
            add(studentIdField);

            add(new JLabel("Course ID:"));
            courseIdField = new JTextField();
            add(courseIdField);

            markAttendanceButton = new JButton("Mark Attendance");
            markAttendanceButton.addActionListener(e -> markAttendance());
            add(markAttendanceButton);
        }

        private void markAttendance() {
            String facultyId = facultyIdField.getText();
            String studentId = studentIdField.getText();
            String courseId = courseIdField.getText();

            User faculty = users.get(facultyId);
            User student = users.get(studentId);
            Course course = courses.stream().filter(c -> c.getId().equals(courseId)).findFirst().orElse(null);

            if (faculty instanceof Faculty && student instanceof Student && course != null) {
                ((Student) student).markAttendance(course);
                JOptionPane.showMessageDialog(this, "Attendance marked successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid input!");
            }
        }
    }

    private abstract class User {
        protected String id;
        protected String name;
        protected String password;

        public User(String id, String name, String password) {
            this.id = id;
            this.name = name;
            this.password = password;
        }
    }

    private class Student extends User {
        private HashMap<Course, Integer> attendance;

        public Student(String id, String name, String password) {
            super(id, name, password);
            attendance = new HashMap<>();
        }

        public void markAttendance(Course course) {
            attendance.put(course, attendance.getOrDefault(course, 0) + 1);
        }

        public int getAttendance(Course course) {
            return attendance.getOrDefault(course, 0);
        }
    }

    private class Faculty extends User {
        public Faculty(String id, String name, String password) {
            super(id, name, password);
        }
    }

    private class Course {
        private String id;
        private String name;

        public Course(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AttendanceManagementSystem::new);
    }
}
