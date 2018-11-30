package edu.tamu.iiif.config;

import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import edu.tamu.iiif.config.AdminConfig.Credentials;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AdminConfig adminConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
                .disable()
            .authorizeRequests()
                .antMatchers(POST, "/resources")
                    .hasRole("ADMIN")
                .antMatchers(PUT, "/resources")
                    .hasRole("ADMIN")
                .antMatchers(DELETE, "/resources/*")
                    .hasRole("ADMIN")
            .anyRequest()
                .permitAll()
            .and()
                .httpBasic()
            .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // @formatter:on
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        for (Credentials adminCredentials : adminConfig.getAdmins()) {
            // @formatter:off
            auth
                .inMemoryAuthentication()
                .withUser(adminCredentials.getUsername())
                .password(adminCredentials.getPassword())
                .roles("ADMIN");
            // @formatter:on
        }

    }

}