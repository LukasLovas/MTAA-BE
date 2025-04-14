package com.example.mtaa.config;

import com.example.mtaa.utils.JwtAuthenticationFilter;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "MTAA Application API", version = "v1", description = "For access to any endpoints, the user first needs to authenticate. Create a user with /auth/register endpoint, and afterwards use the /auth/login to get a bearer token, which should be copied into the Authorize modal window accessible just to the right below this text."),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)public class SecurityConfig {
    @Autowired
    public SecurityConfig() {
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs.yaml",
                                "/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/swagger-resources/**",
                                "/webjars/**",
                                "/auth/login",
                                "/auth/logout",
                                "/auth/register",
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
