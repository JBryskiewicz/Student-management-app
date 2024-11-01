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
    private String currentlySelectedID; // Student id
    private int currentlySelectedIndex; // List option index
    private String mode; // Either "create" or "edit" mode for student manager


    /** Constructor. Sets initial state for main panel window. */
    public MainPanel() {
        setTitle("Student Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.initStudentList();
        this.initEventListeners();

        mode = "create";

        this.addStudentButton.setBackground(Color.LIGHT_GRAY);

        this.selectFirstStudent();

        setContentPane(main_panel);
        pack();
    }

    /** Setup all data to for this component to logically and visually select first student on the List */
    private void selectFirstStudent() {
        this.currentlySelectedID = this.studentManager
                .displayAllStudents()
                .stream()
                .findFirst()
                .map(Student::getStudentID)
                .orElse(null);
        this.currentlySelectedIndex = 0;
        this.studentList.setSelectedIndex(this.currentlySelectedIndex);
    }

    /** Initializes student list component on view with data taken from database. */
    private void initStudentList() {
        DefaultListModel<Student> model = new DefaultListModel<>();

        studentManager.displayAllStudents().forEach(model::addElement);
        studentList = new JList<>(model);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.setCellRenderer(new StudentCellRenderer());
        studentScrollPane.setViewportView(studentList);

        if (studentList.getModel().getSize() > 0) {
            studentList.setSelectedIndex(0); // Select the first item by default
        }
    }

    /** Refreshes student list whenever called by refilling view with students from database. */
    private void refreshStudentList() {
        DefaultListModel<Student> model = (DefaultListModel<Student>) studentList.getModel();
        model.clear();
        studentManager.displayAllStudents().forEach(model::addElement);
    }

    /** Initializes all event listeners on the main panel.
     *  For save button it chooses method to handle click based on current view's mode (create or edit student mode).
     *  MouseClicked event on studentList overrides default behaviour to allow student selection for certain operations
     *  like Delete or Edit.
     */
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
                int index = studentList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    Student student = (Student) studentList.getModel().getElementAt(index);
                    currentlySelectedID = student.getStudentID();
                    currentlySelectedIndex = index;
                    if (mode.equals("edit")) {
                        nameField.setText(student.getName());
                        ageField.setText(String.valueOf(student.getAge()));
                        gradeField.setText(String.valueOf(student.getGrade()));
                    }
                }
            }
        });
    }

    /** Handles saving new student to database with field validation, popout messages and auto selection. */
    private void saveNewStudent() {
        if (emptyFieldValidator()) {
            return;
        }

        String uuid = UUID.randomUUID().toString();
        String name = this.nameField.getText();
        String age = this.ageField.getText();
        String grade = this.gradeField.getText();

        if (ageFieldValidator(age)) {
            return;
        }

        if (gradeFieldValidator(grade)) {
            return;
        }

        Student newStudent = new Student(uuid, name, parseInt(age), parseDouble(grade));
        this.studentManager.addStudent(newStudent);

        this.refreshStudentList();

        studentList.setSelectedIndex(studentList.getModel().getSize() - 1);
        currentlySelectedIndex = studentList.getModel().getSize() - 1;
        currentlySelectedID = newStudent.getStudentID();

        this.nameField.setText("");
        this.ageField.setText("");
        this.gradeField.setText("");

        JOptionPane.showMessageDialog(
                this,
                "Student saved successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /** Handles student editing, saves result to database with field validation, popout messages and auto self-selection. */
    private void saveEditedStudent() {
        if (studentList.getModel().getSize() < 1) {
            JOptionPane.showMessageDialog(
                    this,
                    "No students to modify.",
                    "Notification",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        if (emptyFieldValidator()) {
            return;
        }

        String name = this.nameField.getText();
        String age = this.ageField.getText();
        String grade = this.gradeField.getText();

        if (ageFieldValidator(age)) {
            return;
        }

        if (gradeFieldValidator(grade)) {
            return;
        }

        Student studentToEdit = new Student(currentlySelectedID, name, parseInt(age), parseDouble(grade));
        studentManager.updateStudent(studentToEdit);

        this.refreshStudentList();
        studentList.setSelectedIndex(currentlySelectedIndex);

        JOptionPane.showMessageDialog(
                this,
                "Student modified successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /** Handles student deletion from database with popout messages and auto selection post delete. */
    private void deleteStudent() {
        if (studentList.getModel().getSize() < 1) {
            JOptionPane.showMessageDialog(
                    this,
                    "No students to delete.",
                    "Notification",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this student?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (response == JOptionPane.YES_OPTION) {
            studentManager.removeStudent(currentlySelectedID);

            this.refreshStudentList();

            this.selectFirstStudent();

            if (mode.equals("create")) {
                this.nameField.setText("");
                this.ageField.setText("");
                this.gradeField.setText("");
            } else {
                this.nameField.setText("");
                this.ageField.setText("");
                this.gradeField.setText("");
            }
            JOptionPane.showMessageDialog(
                    this,
                    "Student deleted successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /** Validates if input fields are not empty. Returns boolean value if validation passed. */
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

    /** Validates if age field is an integer within desired range. Returns boolean value if validation passed. */
    private boolean ageFieldValidator(String ageString) {
        try {
            int age = Integer.parseInt(ageString);
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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Age must be a valid number.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    /** Validates if grade field is a double within desired range. Returns boolean value if validation passed. */
    private boolean gradeFieldValidator(String gradeString) {
        try {
            double grade = Double.parseDouble(gradeString);
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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Grade must be a valid float number with '.' to separate fractional part from integer.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }

    /** Toggles view mode to "create" and sets up visual feedback to mark what mode is on. Additionally, clears input fields
     * to allow clean input of new user.
     */
    private void toggleCreationMode() {
        System.out.println("Toggling Creation Mode!");

        this.mode = "create";
        this.addStudentButton.setBackground(Color.LIGHT_GRAY);
        this.editModeButton.setBackground(UIManager.getColor("Button.background"));

        this.nameField.setText("");
        this.ageField.setText("");
        this.gradeField.setText("");

        saveButton.setText("Save");
    }

    /** Toggles view mode to "edit" and sets up visual feedback to mark what mode is on. Additionally, fills input fields
     * with currently selected student's data to allow easy modification.
     */
    private void toggleEditMode() {
        System.out.println("Toggling Edit Mode!");
        this.mode = "edit";
        this.addStudentButton.setBackground(UIManager.getColor("Button.background"));
        this.editModeButton.setBackground(Color.LIGHT_GRAY);

        Student student = this.studentManager.getStudentById(this.currentlySelectedID);

        saveButton.setText("Modify");

        if (student != null) {
            this.nameField.setText(student.getName());
            this.ageField.setText(String.valueOf(student.getAge()));
            this.gradeField.setText(String.valueOf(student.getGrade()));
        }
    }

    /** Toggles visibility for calculated average grade of every student on the list. */
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
