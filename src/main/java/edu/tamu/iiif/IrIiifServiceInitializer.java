package edu.tamu.iiif;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

import edu.tamu.weaver.messaging.config.MessagingConfig;

/**
 * Entry point to the IR IIIF service initializer.
 * 
 * @author wwelling
 */
@SpringBootApplication
@Import(MessagingConfig.class)
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