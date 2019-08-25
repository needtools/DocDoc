package com.example.docdoc.models;

import java.io.Serializable;
import java.util.List;

public class Person  implements Serializable {
    private String name;
    private String birthday;
    private String gender;
    private String phone;
    private String password;
    private String email;
    private List<String> zcontacts;// It is List in android side

    public Person(){

    }

    public Person(String name, String birthday, String gender, String phone, String email, String password, List<String> zcontacts) {
        this.name = name;
        this.birthday = birthday;
        this.gender = gender;
        this.phone = phone;
        this.password =  password;
        this.email =  email;
        this.zcontacts = zcontacts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getZcontacts() {
        return zcontacts;
    }

    public void setZcontacts(List<String> zcontacts) {
        this.zcontacts = zcontacts;
    }

}
