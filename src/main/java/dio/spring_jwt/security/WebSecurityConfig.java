package dio.spring_jwt.security;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
// Imports do H2 e Servlet
import org.h2.server.web.WebServlet;
import jakarta.servlet.Servlet; // Note o 'jakarta' aqui se for Spring Boot 3


@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Substitui o antigo @EnableGlobalMethodSecurity
public class WebSecurityConfig {

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    private static final String[] SWAGGER_WHITELIST = {
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers.frameOptions(frame -> frame.disable())); // Permite o frame do H2 Console
        http.cors(cors -> cors.disable());
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers(HttpMethod.POST,"/login").permitAll()
                .requestMatchers(HttpMethod.POST,"/users").permitAll()
                .requestMatchers(HttpMethod.GET,"/users").hasAnyRole("USERS","MANAGERS")
                .requestMatchers("/managers").hasAnyRole("MANAGERS")
                .anyRequest().authenticated()
        );

        // Adiciona o seu filtro JWT antes de validar a autenticação padrão
        http.addFilterAfter(new JWTFilter(), UsernamePasswordAuthenticationFilter.class);

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /*
    NOTA: Nas versões mais recentes (como esta) a configuração
    para o H2 Database é feita implicitamente pelo @Configuration
    e habilitada no application.properties
    */
}