package edu.tamu.iiif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Entry point to the IR IIIF service initializer.
 * 
 * @author wwelling
 */
@SpringBootApplication
public class IrIiifServiceInitializer extends SpringBootServletInitializer {

    /**
     * Entry point for Tomcat deployment.
     * 
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(IrIiifServiceInitializer.class, args);
    }

    /**
     * Entry point for Spring Boot.
     * 
     * @param application
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(IrIiifServiceInitializer.class);
    }

}