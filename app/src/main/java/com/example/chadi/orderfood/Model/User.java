package com.example.chadi.orderfood.Model;

public class User {

    private String name;
    private String password;
    private String Phone;
    private String IsStaff;
    private String secureCode;

    public User(String Name, String Password, String secureCode) {
        name = Name;
        password = Password;
        IsStaff = "false";
        this.secureCode = secureCode;
    }
    public User(){

    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }

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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }
}