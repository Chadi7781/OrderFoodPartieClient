package com.example.chadi.orderfood.Model;

public class Rating {
    private String userPhone;
    private String foodId;
    private String comment;
    private String rateValue;

    public Rating(){}

    public Rating(String userPhone, String foodId, String comment, String rateValue) {
        this.userPhone = userPhone;
        this.foodId = foodId;
        this.comment = comment;
        this.rateValue = rateValue;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }
}
