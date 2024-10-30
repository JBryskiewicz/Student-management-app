package pl.l3;
import pl.l3.gui.MainPanel;
import pl.l3.service.StudentManagerImpl;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        StudentManagerImpl studentManager = new StudentManagerImpl();
        studentManager.createTable(); // Init on program start!

        SwingUtilities.invokeLater(() -> {
            MainPanel frame = new MainPanel();
            frame.setVisible(true);
        });
    }
}