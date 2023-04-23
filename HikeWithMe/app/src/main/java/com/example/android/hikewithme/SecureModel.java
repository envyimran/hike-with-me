package com.example.android.hikewithme;

public class SecureModel {
    private String email;
    public SecureModel(String email) {
        this.email = email;
    }
    public SecureModel() {}
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
