package com.evolve.app;

import com.evolve.gui.Registration;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.evolve")
public class EfkaSpringApp {

    public static void main(String[] args) {
        Application.launch(Registration.class, args);
    }

}
