package com.lappungdev.jajankuy.model;

public class Order {
    private String orderCustomerID;
    private String orderCustomerName;
    private String orderFoodID;
    private String orderFoodName;
    private String orderFoodPrice;
    private String orderID;
    private String orderSellerID;
    private String orderSellerName;
    private String orderStatus;
    private String orderTimestamp;
    private String orderZID;

    public Order() {

    }

    public Order(String orderCustomerID, String orderCustomerName, String orderFoodID, String orderFoodName, String orderFoodPrice, String orderID, String orderSellerID, String orderSellerName, String orderStatus, String orderTimestamp, String orderZID) {
        this.orderCustomerID = orderCustomerID;
        this.orderCustomerName = orderCustomerName;
        this.orderFoodID = orderFoodID;
        this.orderFoodName = orderFoodName;
        this.orderFoodPrice = orderFoodPrice;
        this.orderID = orderID;
        this.orderSellerID = orderSellerID;
        this.orderSellerName = orderSellerName;
        this.orderStatus = orderStatus;
        this.orderTimestamp = orderTimestamp;
        this.orderZID = orderZID;
    }

    public String getOrderCustomerID() {
        return orderCustomerID;
    }

    public void setOrderCustomerID(String orderCustomerID) {
        this.orderCustomerID = orderCustomerID;
    }

    public String getOrderCustomerName() {
        return orderCustomerName;
    }

    public void setOrderCustomerName(String orderCustomerName) {
        this.orderCustomerName = orderCustomerName;
    }

    public String getOrderSellerID() {
        return orderSellerID;
    }

    public void setOrderSellerID(String orderSellerID) {
        this.orderSellerID = orderSellerID;
    }

    public String getOrderSellerName() {
        return orderSellerName;
    }

    public void setOrderSellerName(String orderSellerName) {
        this.orderSellerName = orderSellerName;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderFoodName() {
        return orderFoodName;
    }

    public void setOrderFoodName(String orderFoodName) {
        this.orderFoodName = orderFoodName;
    }

    public String getOrderFoodPrice() {
        return orderFoodPrice;
    }

    public void setOrderFoodPrice(String orderFoodPrice) {
        this.orderFoodPrice = orderFoodPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTimestamp() {
        return orderTimestamp;
    }

    public void setOrderTimestamp(String orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
    }

    public String getOrderZID() {
        return orderZID;
    }

    public void setOrderZID(String orderZID) {
        this.orderZID = orderZID;
    }

    public String getOrderFoodID() {
        return orderFoodID;
    }

    public void setOrderFoodID(String orderFoodID) {
        this.orderFoodID = orderFoodID;
    }
}