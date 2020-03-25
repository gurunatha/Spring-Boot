package com.javainuse.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String name;
    private String password;
    private String role;
    private boolean is2FAEnable;
    
    

    public User(String name, String password, String role,boolean fa) {
        this.name = name;
        this.password = password;
        this.role = role;
        this.is2FAEnable=fa;
    }
    public User(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
       
    }

    public boolean match(String name, String password) {
        return this.name.equals(name) && this.password.equals(password);
    }
}
