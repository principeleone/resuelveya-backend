package com.resuelveya.resuelve_api.security.config;

import com.resuelveya.resuelve_api.security.domain.service.CustomUserDetailsService;
import com.resuelveya.resuelve_api.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;
    private final JwtAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtAuthenticationEntryPoint authenticationEntryPoint,
            JwtAccessDeniedHandler accessDeniedHandler
    ) {
        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;

        this.userDetailsService =
                userDetailsService;

        this.passwordEncoder =
                passwordEncoder;

        this.authenticationEntryPoint =
                authenticationEntryPoint;

        this.accessDeniedHandler =
                accessDeniedHandler;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService
                );

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    // Angular
    // Configura CORS para permitir peticiones desde el frontend (localhost:4200),
    // usando métodos HTTP y headers necesarios para la autenticación con JWT.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:4200")
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept"
                )
        );

        // No usamos cookies; el JWT se envía en Authorization.
        configuration.setAllowCredentials(false);

        // El navegador conserva el resultado del preflight una hora.
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }

    // Configuración principal de Spring Security.

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception{
        return  http
                .csrf(csrf->csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        ))
                .exceptionHandling(exceptions ->
                        exceptions
                                .authenticationEntryPoint(
                                        authenticationEntryPoint
                                )
                                .accessDeniedHandler(
                                        accessDeniedHandler
                                )
                )

                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas (Visitantes y Autenticación)
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/api/v1/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/categorias/**", "/api/especialidades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/resenias/**").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Rutas de Administración
                        .requestMatchers(HttpMethod.POST, "/api/v1/categorias/**", "/api/especialidades/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/categorias/**", "/api/especialidades/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/categorias/**", "/api/especialidades/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/usuarios/**").hasRole("ADMIN")

                        // Rutas exclusivas para Técnicos
                        .requestMatchers("/api/v1/tecnico/**").hasRole("TECNICO")

                        // Rutas para Clientes (Crear y editar reseñas)
                        .requestMatchers(HttpMethod.POST, "/api/v1/resenias/**").hasRole("CLIENTE")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/resenias/**").hasRole("CLIENTE")

                        // Rutas de Solicitudes de Servicio
                        .requestMatchers(HttpMethod.POST, "/api/v1/solicitudes").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/solicitudes/cliente/**").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/solicitudes/tecnico/**").hasRole("TECNICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/solicitudes/*/aceptar").hasRole("TECNICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/solicitudes/*/rechazar").hasRole("TECNICO")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/solicitudes/*/cancelar").hasRole("CLIENTE")
                        .requestMatchers("/api/v1/solicitudes/**").authenticated()

                        // Rutas para usuarios autenticados (Perfil, Detalle Técnico Completo)
                        .requestMatchers("/api/v1/perfil/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/tecnicos/*/completo").authenticated()


                        .anyRequest().authenticated()
                ).authenticationProvider(
                authenticationProvider()
        )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }
}
