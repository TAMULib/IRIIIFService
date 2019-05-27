package edu.tamu.iiif.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "iiif")
public class AdminConfig {

    private List<Credentials> admins = new ArrayList<Credentials>();

    public List<Credentials> getAdmins() {
        return admins;
    }

    public void setAdmins(List<Credentials> admins) {
        this.admins = admins;
    }

    public static class Credentials {

        private String username;

        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

    }

}
