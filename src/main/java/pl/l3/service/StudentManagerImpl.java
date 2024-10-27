package pl.l3.service;

import pl.l3.domain.Student;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;

public class StudentManagerImpl implements StudentManager {

    private static final String URL = "jdbc:sqlite:students.db";
    private String ADD_STUDENT_QUERY = "INSERT INTO students(studentID, name, age, grade) VALUES(?, ?, ?, ?)";
    private String GET_ALL_STUDENTS_QUERY = "SELECT * FROM students";
    private String UPDATE_STUDENT_QUERY = "UPDATE students SET name = ?, age = ?, grade = ? WHERE studentID = ?";
    private String DELETE_STUDENT_QUERY = "DELETE FROM students WHERE studentID = ?";

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
