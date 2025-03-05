package com.example.bookmanage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /*
    // 注释 Spring Security 的认证逻辑
    @EnableWebSecurity
    public class SecurityConfigInner {

        @Bean
        public UserDetailsService userDetailsService(UserService userService) {
            return username -> {
                User user = userService.findByUsername(username);
                if (user == null) {
                    throw new UsernameNotFoundException("User not found");
                }
                return org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build();
            };
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers("/register", "/login").permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin(form -> form
                    .loginPage("/login")
                    .defaultSuccessUrl("/books", true)
                    .permitAll()
                )
                .logout(logout -> logout
                    .logoutSuccessUrl("/login")
                    .permitAll()
                );
            return http.build();
        }
    }
    */
}