package pl.l3.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Student {

    private String studentID;
    private String name;
    private int age;
    private double grade;

    public void displayInfo() {
        System.out.println(
                "============== \n" +
                "Student's ID: " + this.studentID + "\n" +
                "Student's name: " + this.name + "\n" +
                "Student's age: " + this.age + "\n" +
                "Student's grade: " + this.grade + "\n" +
                "============== \n"
        );
    }
}
