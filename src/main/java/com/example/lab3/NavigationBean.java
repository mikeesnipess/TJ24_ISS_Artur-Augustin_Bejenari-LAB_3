package com.example.lab3;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;

@Named
@SessionScoped
public class NavigationBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Method to navigate to the Client page
    public String goToClient() {
        return "toClient"; // Must match the outcome in faces-config.xml
    }

    // Method to navigate to the Product page
    public String goToProduct() {
        return "toProduct";
    }



    // Other navigation methods can be added as needed
}
