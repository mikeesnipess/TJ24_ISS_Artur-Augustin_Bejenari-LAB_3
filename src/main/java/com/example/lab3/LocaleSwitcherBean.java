package com.example.lab3;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Locale;

@Named
@SessionScoped
public class LocaleSwitcherBean implements Serializable {
    private static final long serialVersionUID = 1L;

    // Default to Romanian
    private Locale locale;

    @PostConstruct
    public void init() {
        // Initialize to Romanian
        locale = new Locale("ro");
        updateLocale();
    }

    // Get the current locale
    public Locale getLocale() {
        return locale;
    }

    // Get the current language for select menu
    public String getLanguage() {
        return locale.getLanguage();
    }

    // Set the language, updating the FacesContext as well
    public void setLanguage(String language) {
        locale = new Locale.Builder().setLanguage(language).build();
        updateLocale(); // Ensure the locale is applied
    }

    // Apply the locale on every page load
    public void updateLocale() {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }
}
