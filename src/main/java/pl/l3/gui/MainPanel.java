package pl.l3.gui;

import pl.l3.domain.Student;
import pl.l3.service.StudentManagerImpl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.UUID;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class MainPanel extends JFrame {

    private StudentManagerImpl studentManager = new StudentManagerImpl();

    private JPanel main_panel;
    private JButton addStudentButton;
    private JButton avgGradesButton;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField gradeField;
    private JScrollPane studentScrollPane;
    private JLabel averageLabel;
    private JButton deleteStudentButton;
    private JButton editModeButton;
    private JButton saveButton;
    private JList studentList;

    private boolean displayAverage = false;
    private String currentlySelected; // Only student id
    private String mode; // Either "create" or "edit"


    public MainPanel() {
        setTitle("Student Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.initStudentList();
        this.initEventListeners();

        this.mode = "create";
        this.addStudentButton.setBackground(Color.LIGHT_GRAY);

        this.currentlySelected = this.studentManager
                .displayAllStudents()
                .stream()
                .findFirst()
                .map(Student::getStudentID)
                .orElse(null);


        setContentPane(main_panel);
        pack();
    }

    private void initStudentList() {
        studentList = returnStudentList();
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setCellRenderer(new StudentCellRenderer());
        studentScrollPane.setViewportView(studentList);
    }

    private JList returnStudentList() {
        return new JList<>(
                this.studentManager
                        .displayAllStudents()
                        .toArray()
        );
    }

    private void initEventListeners() {
        saveButton.addActionListener(e -> {
            if (mode.equals("create")) {
                saveNewStudent();
            } else {
                saveEditedStudent();
            }
        });
        addStudentButton.addActionListener(e -> toggleCreationMode());
        editModeButton.addActionListener(e -> toggleEditMode());
        deleteStudentButton.addActionListener(e -> deleteStudent());
        avgGradesButton.addActionListener(e -> toggleAverage());

        studentList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mode.equals("create")) {
                    return;
                }

                int index = studentList.locationToIndex(e.getPoint());
                System.out.println(index);
                if (index >= 0) {
                    Student student = (Student) studentList.getModel().getElementAt(index);
                    currentlySelected = student.getStudentID();
                    nameField.setText(student.getName());
                    ageField.setText(String.valueOf(student.getAge()));
                    gradeField.setText(String.valueOf(student.getGrade()));
                }
            }
        });
    }

    private void saveNewStudent() {
        if (emptyFieldValidator()) {
            return;
        }

        String uuid = UUID.randomUUID().toString();
        String name = this.nameField.getText();
        int age = parseInt(this.ageField.getText());
        double grade = parseDouble(this.gradeField.getText());

        if (ageFieldValidator(age)) {
            return;
        }

        if (gradeFieldValidator(grade)) {
            return;
        }

        Student newStudent = new Student(uuid, name, age, grade);
        this.studentManager.addStudent(newStudent);
        this.initStudentList(); // Reinitialize student list for visual feedback
        this.nameField.setText(" ");
        this.ageField.setText(" ");
        this.gradeField.setText(" ");
    }

    private void saveEditedStudent() {
        if (emptyFieldValidator()) {
            return;
        }

        String name = this.nameField.getText();
        int age = parseInt(this.ageField.getText());
        double grade = parseDouble(this.gradeField.getText());

        if (ageFieldValidator(age)) {
            return;
        }

        if (gradeFieldValidator(grade)) {
            return;
        }

        Student studentToEdit = new Student(currentlySelected, name, age, grade);
        studentManager.updateStudent(studentToEdit);
        this.initStudentList(); // Reinitialize student list for visual feedback
    }

    private void deleteStudent() {
        studentManager.removeStudent(currentlySelected);
        this.initStudentList(); // Reinitialize student list for visual feedback
        this.nameField.setText(" ");
        this.ageField.setText(" ");
        this.gradeField.setText(" ");
    }

    private boolean emptyFieldValidator() {
        if (this.nameField.getText().isEmpty() || this.ageField.getText().isEmpty() || this.gradeField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Input fields cannot be empty.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return true;
        } else {
            return false;
        }
    }

    private boolean ageFieldValidator(int age) {
        if (age < 0 || age > 120) {
            JOptionPane.showMessageDialog(
                    this,
                    "Age cannot be negative or above 120 years old.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return true;
        } else {
            return false;
        }
    }

    private boolean gradeFieldValidator(double grade) {
        if (grade < 0.0 || grade > 100.0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Grade must be in range of 0 to 100.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return true;
        } else {
            return false;
        }
    }

    private void toggleCreationMode() {
        this.mode = "create";
        this.addStudentButton.setBackground(Color.LIGHT_GRAY);
        this.editModeButton.setBackground(UIManager.getColor("Button.background"));

        this.nameField.setText(" ");
        this.ageField.setText(" ");
        this.gradeField.setText(" ");
    }

    private void toggleEditMode() {
        this.mode = "edit";
        this.addStudentButton.setBackground(UIManager.getColor("Button.background"));
        this.editModeButton.setBackground(Color.LIGHT_GRAY);

        Student student = this.studentManager.getStudentById(this.currentlySelected);

        if (student != null) {
            this.nameField.setText(student.getName());
            this.ageField.setText(String.valueOf(student.getAge()));
            this.gradeField.setText(String.valueOf(student.getGrade()));
        }
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
