package com.example.k1590;

public class UserBean {
    String name;
    String password;
    String tou;
    String role;
    String school;
    String college;
    String speciality;
    String grade;
    String skill;
    String contact;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTou() {
        return tou;
    }

    public void setTou(String tou) {
        this.tou = tou;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", tou='" + tou + '\'' +
                ", role='" + role + '\'' +
                ", school='" + school + '\'' +
                ", college='" + college + '\'' +
                ", speciality='" + speciality + '\'' +
                ", grade='" + grade + '\'' +
                ", skill='" + skill + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}
