package com.example.hsr15.easyshopping.Model;

public class User {

    public String name, password, phone, image, address;


    public User(String name, String password, String phone, String image, String address) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.image = image;
        this.address = address;
    }

    public User(){

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
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
