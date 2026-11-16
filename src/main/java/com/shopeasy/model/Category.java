
package com.shopeasy.model;

import java.sql.Date;

public class Category {

    private int categoryId;
    private int shopId;

    private String categoryName;
    private String description;
    private String status;

    private Date createdDate;

    public Category() {
    }

    public Category(int categoryId, int shopId,
                    String categoryName, String description,
                    String status, Date createdDate) {

        this.categoryId = categoryId;
        this.shopId = shopId;
        this.categoryName = categoryName;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
