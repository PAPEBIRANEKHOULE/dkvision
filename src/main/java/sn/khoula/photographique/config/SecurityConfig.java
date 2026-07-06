package sn.khoula.photographique.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin123}")
    private String adminPassword;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    PathPatternRequestMatcher.pathPattern("/galleries/new"),
                    PathPatternRequestMatcher.pathPattern("/galleries/{id}/upload"),
                    PathPatternRequestMatcher.pathPattern("/galleries/{id}/delete"),
                    PathPatternRequestMatcher.pathPattern("/photos/{id}/delete")
                ).authenticated()
                .requestMatchers(
                    PathPatternRequestMatcher.pathPattern("/"),
                    PathPatternRequestMatcher.pathPattern("/galleries"),
                    PathPatternRequestMatcher.pathPattern("/galleries/**"),
                    PathPatternRequestMatcher.pathPattern("/uploads/**"),
                    PathPatternRequestMatcher.pathPattern("/contact"),
                    PathPatternRequestMatcher.pathPattern("/login"),
                    PathPatternRequestMatcher.pathPattern("/css/**"),
                    PathPatternRequestMatcher.pathPattern("/js/**"),
                    PathPatternRequestMatcher.pathPattern("/health"),
                    PathPatternRequestMatcher.pathPattern("/h2-console/**")
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(PathPatternRequestMatcher.pathPattern("/h2-console/**"))
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
            .username(adminUsername)
            .password(encoder.encode(adminPassword))
            .roles("ADMIN")
            .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
