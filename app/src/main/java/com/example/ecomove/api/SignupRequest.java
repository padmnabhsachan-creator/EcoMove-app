package com.example.ecomove.api;

public class SignupRequest {
    private String name;
    private String email;
    private String phoneNumber;
    private String password;

    public SignupRequest(String name, String email, String phoneNumber, String password) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getPassword() { return password; }
}