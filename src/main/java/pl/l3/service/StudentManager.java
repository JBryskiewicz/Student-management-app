package pl.l3.service;

import pl.l3.domain.Student;

import java.util.ArrayList;

public interface StudentManager {
    default void addStudent(Student student) {
    }

    default void updateStudent(Student student) {
    }

    default void removeStudent(String id) {
    }

    default Student getStudentById(String id) {
        return null;
    }

    default ArrayList<Student> displayAllStudents() {
        return null;
    }

    default double calculateAverageGrade() {
        return 0;
    }
}
