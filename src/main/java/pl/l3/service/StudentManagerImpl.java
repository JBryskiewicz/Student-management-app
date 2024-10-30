package pl.l3.service;

import pl.l3.domain.Student;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;

public class StudentManagerImpl implements StudentManager {


    /** Query constants */
    private static final String URL = "jdbc:sqlite:students.db"; // Shouldn't be here
    private static final String ADD_STUDENT_QUERY = "INSERT INTO students(studentID, name, age, grade) VALUES(?, ?, ?, ?)";
    private static final String GET_ALL_STUDENTS_QUERY = "SELECT * FROM students";
    private static final String UPDATE_STUDENT_QUERY = "UPDATE students SET name = ?, age = ?, grade = ? WHERE studentID = ?";
    private static final String DELETE_STUDENT_QUERY = "DELETE FROM students WHERE studentID = ?";
    private static final String GET_STUDENT_BY_ID_QUERY = "SELECT * FROM students WHERE studentID = ?";

    /** Creates new SQL table if it doesn't exit */
    public void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS students (" +
                "studentID TEXT PRIMARY KEY," +
                "name TEXT NOT NULL," +
                "age INTEGER," +
                "grade REAL)";
        try (
                Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement()
        ) {
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace(); //Should ignore warning for now, stacktrace is enough.
        }
    }

    /** Connects to DB in order to save new student according to SQL query */
    @Override
    public void addStudent(Student student) {
        System.out.println("Adding Student");
        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(ADD_STUDENT_QUERY)
        ) {
            preparedStatement.setString(1, student.getStudentID());
            preparedStatement.setString(2, student.getName());
            preparedStatement.setInt(3, student.getAge());
            preparedStatement.setDouble(4, student.getGrade());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); //Should ignore warning for now, stacktrace is enough.
        }
    }

    /** Connects to DB in order to save modified student according to SQL query */
    @Override
    public void updateStudent(Student student) {
        System.out.println("Updating Student");

        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_STUDENT_QUERY)
        ) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.setInt(2, student.getAge());
            preparedStatement.setDouble(3, student.getGrade());
            preparedStatement.setString(4, student.getStudentID());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); //Should ignore warning for now, stacktrace is enough.
        }
    }

    /** Connects to DB in order to delete student by id according to SQL query */
    @Override
    public void removeStudent(String id) {
        System.out.println("Removing Student");
        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_STUDENT_QUERY)
        ) {
            preparedStatement.setString(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); //Should ignore warning for now, stacktrace is enough.
        }
    }

    /** Connects to DB in order to read student by id according to SQL query */
    @Override
    public Student getStudentById(String id) {
        Student student = null;
        try (
                Connection connection = DriverManager.getConnection(URL);
                PreparedStatement preparedStatement = connection.prepareStatement(GET_STUDENT_BY_ID_QUERY)
        ) {
            preparedStatement.setString(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    student = new Student(
                            resultSet.getString("studentID"),
                            resultSet.getString("name"),
                            resultSet.getInt("age"),
                            resultSet.getDouble("grade")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return student;
    }

    /** Connects to DB in order to read all student according to SQL query */
    @Override
    public ArrayList<Student> displayAllStudents() {
        System.out.println("displaying all");

        ArrayList<Student> students = new ArrayList<>();

        try (
                Connection connection = DriverManager.getConnection(URL);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(GET_ALL_STUDENTS_QUERY)
        ) {
            while (resultSet.next()) {
                Student student = new Student(
                        resultSet.getString("studentID"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getDouble("grade")
                );
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    /** Uses displayAllStudents() in order to get all student grades, calculate and return grade average */
    @Override
    public double calculateAverageGrade() {
       return BigDecimal.valueOf(this.displayAllStudents()
               .stream()
               .mapToDouble(Student::getGrade)
               .average()
               .orElse(0))
               .setScale(2, RoundingMode.HALF_UP)
               .doubleValue();
    }
}
