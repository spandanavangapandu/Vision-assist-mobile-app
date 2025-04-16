package com.programminghut.realtime_object;

public class User {
    private String name;
    private Long mobile;
    private String address;
    public User(String name, Long mobile, String address) {
        this.name = name;
        this.mobile = mobile;
        this.address = address;
    }

    // Getters and setters (if needed)
    public String getName() {
        return name;
    }

    public Long getMobile() {
        return mobile;
    }

    public String getAddress() {
        return address;
    }

}

