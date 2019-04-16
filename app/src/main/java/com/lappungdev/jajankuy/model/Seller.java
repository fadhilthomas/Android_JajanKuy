package com.lappungdev.jajankuy.model;

public class Seller {
    private String sellerAddressState;
    private String sellerEmail;
    private String sellerID;
    private String sellerLicenseID;
    private String sellerNIK;
    private String sellerName;
    private String sellerPhone;
    private String sellerPhotoID;
    private String sellerPhotoPlace;
    private String sellerZID;
    private int sellerMeter;

    public Seller(String sellerAddressState, String sellerEmail, String sellerID, String sellerLicenseID, String sellerNIK, String sellerName, String sellerPhone, String sellerPhotoID, String sellerPhotoPlace, String sellerZID, int sellerMeter) {
        this.sellerAddressState = sellerAddressState;
        this.sellerEmail = sellerEmail;
        this.sellerID = sellerID;
        this.sellerLicenseID = sellerLicenseID;
        this.sellerNIK = sellerNIK;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.sellerPhotoID = sellerPhotoID;
        this.sellerPhotoPlace = sellerPhotoPlace;
        this.sellerZID = sellerZID;
        this.sellerMeter = sellerMeter;
    }

    public String getSellerAddressState() {
        return sellerAddressState;
    }

    public void setSellerAddressState(String sellerAddressState) {
        this.sellerAddressState = sellerAddressState;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerID() {
        return sellerID;
    }

    public void setSellerID(String sellerID) {
        this.sellerID = sellerID;
    }

    public String getSellerLicenseID() {
        return sellerLicenseID;
    }

    public void setSellerLicenseID(String sellerLicenseID) {
        this.sellerLicenseID = sellerLicenseID;
    }

    public String getSellerNIK() {
        return sellerNIK;
    }

    public void setSellerNIK(String sellerNIK) {
        this.sellerNIK = sellerNIK;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerPhotoID() {
        return sellerPhotoID;
    }

    public void setSellerPhotoID(String sellerPhotoID) {
        this.sellerPhotoID = sellerPhotoID;
    }

    public String getSellerPhotoPlace() {
        return sellerPhotoPlace;
    }

    public void setSellerPhotoPlace(String sellerPhotoPlace) {
        this.sellerPhotoPlace = sellerPhotoPlace;
    }

    public String getSellerZID() {
        return sellerZID;
    }

    public void setSellerZID(String sellerZID) {
        this.sellerZID = sellerZID;
    }

    public int getSellerMeter() {
        return sellerMeter;
    }

    public void setSellerMeter(int sellerMeter) {
        this.sellerMeter = sellerMeter;
    }
}
