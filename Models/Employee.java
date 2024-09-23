/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Assignment.Models;

import java.io.Serializable;

/**
 *
 * @author Duy Nguyen
 */
public class Employee implements Serializable {

    private String code, name, email;
    private int age;
    private double salary;

    public Employee() {
    }

    public Employee(String code, String name, int age, String email, double salary) {
        this.code = code;
        this.name = name;
        this.age = age;
        this.email = email;
        this.salary = salary;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean containsKeyword(String keyword) {
        return code.toLowerCase().contains(keyword.toLowerCase())
                || name.toLowerCase().contains(keyword.toLowerCase())
                || String.valueOf(age).toLowerCase().contains(keyword.toLowerCase())
                || email.toLowerCase().contains(keyword.toLowerCase())
                || String.valueOf(salary).toLowerCase().contains(keyword.toLowerCase());
    }
}
