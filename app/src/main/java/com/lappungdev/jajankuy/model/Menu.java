package com.lappungdev.jajankuy.model;

public class Menu {

    private String menuCategory;
    private String menuID;
    private String menuName;
    private String menuPhoto;
    private String menuPrice;
    private String menuSellerName;
    private String menuSellerZID;
    private String menuState;
    private String menuTimestamp;
    private String menuZID;
    private int menuMeter;

    public Menu(){}

    public Menu(String menuCategory, String menuID, String menuName, String menuPhoto, String menuPrice, String menuSellerName, String menuSellerZID, String menuState, String menuTimestamp, String menuZID, int menuMeter) {
        this.setMenuCategory(menuCategory);
        this.setMenuID(menuID);
        this.setMenuName(menuName);
        this.setMenuPhoto(menuPhoto);
        this.setMenuPrice(menuPrice);
        this.setMenuSellerName(menuSellerName);
        this.setMenuSellerZID(menuSellerZID);
        this.setMenuState(menuState);
        this.setMenuTimestamp(menuTimestamp);
        this.setMenuZID(menuZID);
        this.setMenuMeter(menuMeter);
    }


    public String getMenuCategory() {
        return menuCategory;
    }

    public void setMenuCategory(String menuCategory) {
        this.menuCategory = menuCategory;
    }

    public String getMenuID() {
        return menuID;
    }

    public void setMenuID(String menuID) {
        this.menuID = menuID;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuPhoto() {
        return menuPhoto;
    }

    public void setMenuPhoto(String menuPhoto) {
        this.menuPhoto = menuPhoto;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(String menuPrice) {
        this.menuPrice = menuPrice;
    }

    public String getMenuSellerName() {
        return menuSellerName;
    }

    public void setMenuSellerName(String menuSellerName) {
        this.menuSellerName = menuSellerName;
    }

    public String getMenuSellerZID() {
        return menuSellerZID;
    }

    public void setMenuSellerZID(String menuSellerZID) {
        this.menuSellerZID = menuSellerZID;
    }

    public String getMenuState() {
        return menuState;
    }

    public void setMenuState(String menuState) {
        this.menuState = menuState;
    }

    public String getMenuTimestamp() {
        return menuTimestamp;
    }

    public void setMenuTimestamp(String menuTimestamp) {
        this.menuTimestamp = menuTimestamp;
    }

    public String getMenuZID() {
        return menuZID;
    }

    public void setMenuZID(String menuZID) {
        this.menuZID = menuZID;
    }

    public int getMenuMeter() {
        return menuMeter;
    }

    public void setMenuMeter(int menuMeter) {
        this.menuMeter = menuMeter;
    }
}
