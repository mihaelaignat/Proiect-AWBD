package com.fitness.opp.config;

import com.fitness.opp.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> {})
                .authorizeHttpRequests(auth -> auth
                        // Resurse statice si pagini publice
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // 1. VIZUALIZARE (Accesibil pentru toți cei autentificați)
                        .requestMatchers("/users/list/**", "/coaches/list/**", "/workout-groups/list/**", "/nutrition/list/**").authenticated()

                        // 2. GESTIUNE UTILIZATORI (Doar ADMIN)
                        .requestMatchers("/users/showFormForAdd/**", "/users/save/**", "/users/delete/**", "/users/showFormForUpdate/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // 3. GESTIUNE ANTRENORI (Doar ADMIN)
                        .requestMatchers("/coaches/showFormForAdd/**", "/coaches/save/**", "/coaches/delete/**", "/coaches/showFormForUpdate/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // 4. GESTIUNE GRUPURI WORKOUT (Doar ADMIN)
                        .requestMatchers("/workout-groups/showFormForAdd/**", "/workout-groups/save/**", "/workout-groups/delete/**", "/workout-groups/showFormForUpdate/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        // 5. GESTIUNE NUTRITION PLAN (Doar ADMIN) - ADAUGAT ACUM
                        .requestMatchers("/nutrition/showFormForAdd/**", "/nutrition/save/**", "/nutrition/delete/**", "/nutrition/showFormForUpdate/**")
                        .hasAnyAuthority("ADMIN", "ROLE_ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/users/list", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("FitnessAppSecretKey_2024_Security")
                        .tokenValiditySeconds(86400)
                        .userDetailsService(userDetailsService)
                        .rememberMeParameter("remember-me")
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}