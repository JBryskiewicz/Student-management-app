package pl.l3.gui;

import pl.l3.domain.Student;

import javax.swing.*;
import java.awt.*;

public class StudentCellRenderer extends JPanel implements ListCellRenderer<Student> {
    private JLabel nameLabel;
    private JLabel ageLabel;
    private JLabel gradeLabel;

    public StudentCellRenderer() {
        setLayout(new GridLayout(1, 3));
        nameLabel = new JLabel();
        ageLabel = new JLabel();
        gradeLabel = new JLabel();

        nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        ageLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        gradeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        add(nameLabel);
        add(gradeLabel);
        add(ageLabel);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Student> list, Student student, int index, boolean isSelected, boolean cellHasFocus) {
        nameLabel.setText(student.getName());
        ageLabel.setText("Age: " + student.getAge());
        gradeLabel.setText("Grade: " + student.getGrade());

        if (isSelected) {
            setBackground(Color.LIGHT_GRAY);
            setForeground(Color.BLACK);
        } else {
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
        }

        setOpaque(true);

        return this;
    }
}
