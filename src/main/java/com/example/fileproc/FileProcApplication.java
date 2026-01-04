package com.example.fileproc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FileProcApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileProcApplication.class, args);
    }
}
