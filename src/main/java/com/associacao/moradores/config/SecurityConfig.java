package com.associacao.moradores.config;

import com.associacao.moradores.model.Usuario;
import com.associacao.moradores.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
            .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository repo) {
        return username -> {
            Usuario u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return User.withUsername(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRole())
                .build();
        };
    }

    @Bean
    public CommandLineRunner initDatabase(UsuarioRepository uRepo, com.associacao.moradores.repository.MoradorRepository mRepo) {
        return args -> {
            if (uRepo.findByUsername("admin").isEmpty()) {
                uRepo.save(new Usuario("admin", passwordEncoder().encode("admin123"), "ADMIN"));
            }
            if (mRepo.count() == 0) {
                mRepo.save(new com.associacao.moradores.model.Morador("Morador Teste 1", "Ativo"));
                mRepo.save(new com.associacao.moradores.model.Morador("Morador Teste 2", "Ativo"));
            }
        };
    }
}
