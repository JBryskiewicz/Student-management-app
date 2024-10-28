package pl.l3.gui;

import pl.l3.domain.Student;
import pl.l3.service.StudentManagerImpl;

import javax.swing.*;
import java.util.List;
import java.util.UUID;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class MainPanel extends JFrame {

    private StudentManagerImpl studentManager = new StudentManagerImpl();

    // Sample Student Data
    private List<String> studentNames = List.of("Alice", "Bob", "Charlie", "Diana", "Evan");

    private JPanel main_panel;
    private JButton addStudentButton;
    private JButton avgGradesButton;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField gradeField;
    private JScrollPane studentScrollPane;
    private JLabel averageLabel;
    private JList studentList;
    private boolean displayAverage = false;


    public MainPanel() {
        setTitle("Student Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.initStudentList();
        ;
        this.initEventListeners();

        setContentPane(main_panel);
        pack();
    }

    private void initStudentList() {
        studentList = returnStudentList();
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentScrollPane.setViewportView(studentList);
    }

    private JList returnStudentList() {
        return new JList<>(
                this.studentManager
                        .displayAllStudents()
                        .stream()
                        .map(student -> {
                            return "Age: " + student.getAge() + " | Grade: " + student.getGrade() + " | Name: " + student.getName();
                        })
                        .toArray()
        );
    }

    private void initEventListeners() {
        this.addStudentButton.addActionListener(e -> saveNewStudent());
        this.avgGradesButton.addActionListener(e -> toggleAverage());
    }

    private void saveNewStudent() {
        if (this.nameField.getText().isEmpty() || this.ageField.getText().isEmpty() || this.gradeField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Input fields cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        String uuid = UUID.randomUUID().toString();
        String name = this.nameField.getText();
        int age = parseInt(this.ageField.getText());
        double grade = parseDouble(this.gradeField.getText());

        if (age < 0 || age > 120) {
            JOptionPane.showMessageDialog(
                    this,
                    "Age cannot be negative or above 120 years old.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
        return;
        }

        if (grade < 0.0 || grade > 100.0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Grade must be in range of 0 to 100.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        Student newStudent = new Student(uuid, name, age, grade);
        this.studentManager.addStudent(newStudent);
        this.initStudentList(); // Reinitialize student list for visual feedback
    }

    private void toggleAverage() {
        if (!this.displayAverage) {
            this.averageLabel.setText("Average: " + Double.toString(this.studentManager.calculateAverageGrade()));
            this.displayAverage = true;
        } else {
            this.averageLabel.setText("");
            this.displayAverage = false;
        }

    }
}
