package edu.tamu.iiif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * Entry point to the IR IIIF Service.
 * 
 * @author wwelling
 */
@SpringBootApplication
public class IRIIIFService extends SpringBootServletInitializer {

    /**
     * Entry point for Tomcat deployment.
     * 
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(IRIIIFService.class, args);
    }

    /**
     * Entry point for Spring Boot.
     * 
     * @param application
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(IRIIIFService.class);
    }

}