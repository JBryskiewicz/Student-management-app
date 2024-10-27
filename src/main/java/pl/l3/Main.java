package pl.l3;
import pl.l3.service.StudentManagerImpl;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        StudentManagerImpl studentService = new StudentManagerImpl();
        studentService.createTable(); // Init on program start

        studentService.displayAllStudents().forEach(student -> {
            System.out.println(student.getName() + " with grade: " + student.getGrade());
        });

        System.out.println(studentService.calculateAverageGrade());
    }
}