package org.customtasklist.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    /**
     * Configures the security settings for the application to use OAuth 2.0 stateless authentication.
     * Disables CSRF protection, as the application uses token-based authentication, and secures all endpoints.
     *
     * @param http the {@link HttpSecurity} object to configure HTTP security
     * @return a {@link SecurityFilterChain} representing the security configuration
     * @throws Exception if any configuration errors occur
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()  // Ensure all requests are authenticated
                )
                .oauth2Login();  // Enable OAuth 2.0 login
        return http.build();
    }
}