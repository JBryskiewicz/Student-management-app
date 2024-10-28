package pl.l3;
import pl.l3.gui.MainPanel;
import pl.l3.service.StudentManagerImpl;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        StudentManagerImpl studentManager = new StudentManagerImpl();
        studentManager.createTable(); // Init on program start

        studentManager.displayAllStudents().forEach(student -> {
            System.out.println(student.getName() + " with grade: " + student.getGrade());
        });

        System.out.println(studentManager.calculateAverageGrade());

        SwingUtilities.invokeLater(() -> {
            MainPanel frame = new MainPanel();
            frame.setVisible(true);
        });
    }
}