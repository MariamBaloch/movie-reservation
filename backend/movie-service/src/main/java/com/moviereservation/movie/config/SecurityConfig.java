package com.moviereservation.movie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//Enables @PreAuthorize annotations in your controllers.
@EnableMethodSecurity
public class SecurityConfig{

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/movies","/api/movies/*","/api/screens","/api/screens/*").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/movies","/api/screens").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/api/movies/*","/api/screens/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/api/movies/*","/api/screens/*").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    // Temporary ADMIN account for local development only
    @Bean
    @Profile("dev")
    public UserDetailsService devAdminUser(
            @Value("${DEV_ADMIN_PASSWORD}") String password) {

        return new InMemoryUserDetailsManager(
                User.withUsername("admin")
                        .password("{noop}" + password)
                        .roles("ADMIN")
                        .build()
        );
    }
}