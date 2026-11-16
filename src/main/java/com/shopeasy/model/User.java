
package com.shopeasy.model;

import java.sql.Date;

public class User {

    private int userId;
    private int shopId;
    private String fullName;
    private String username;
    private String password;
    private String role;
    private Date createdDate;
    private String status;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(int shopId, String fullName,
                String username, String password) {

        this.shopId = shopId;
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = "OWNER";
    }

    // Getters and Setters

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
