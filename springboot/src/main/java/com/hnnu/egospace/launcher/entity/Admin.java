package com.hnnu.egospace.launcher.entity;

import javax.persistence.Table;

import javax.persistence.Id;

@Table(name = "admin_info")
public class Admin {

    @Id
    private String ID;
//   camelCase
    private String realName;
//  camelCase
    private String phone;
    private String address;
    private String email;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
}
