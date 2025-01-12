package com.hnnu.egospace.launcher.entity;

import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.persistence.Id;

@Table(name = "user_info")
@JsonIgnoreProperties(value = {"gameScore", "handler", "hibernateLazyInitializer", "fieldHandler"})
public class User {

    @Id
    private String id;
    private String username;
    private String password;
    private String gender;
    private Integer age;
    private String phone;
    private String address;
    private Integer profileMana;
// camelCase
    private String realName;
    private String studentId;
// camelCase
    private String email;

    // game related
    private Integer uid;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String gameToken;

    // token
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getProfileMana() {
        return profileMana;
    }

    public void setProfileMana(Integer profileMana) {
        this.profileMana = profileMana;
    }


    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGameToken() {
        return gameToken;
    }

    public void setGameToken(String gameToken) {
        this.gameToken = gameToken;
    }
}