package com.rbp.bookmymovie.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.rbp.bookmymovie.security.jwt.AuthEntryPointJwt;
import com.rbp.bookmymovie.security.jwt.AuthTokenFilter;
import com.rbp.bookmymovie.security.services.UserDetailsServiceImpl;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http.cors().and().csrf().disable()
//                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
//                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
////                .authorizeRequests().antMatchers("/api/v1.0/moviebooking/all").hasRole("GUEST").anyRequest().authenticated().and()
////                .authorizeRequests().antMatchers("/api/v1.0/moviebooking/login",
////                        "/api/v1.0/moviebooking/register",
////                        "/api-docs",
////                        "/**",
////                        "/swagger-ui.html",
////                        "/actuator/**").permitAll()
//                .authorizeRequests().antMatchers(
//                        "/**").permitAll()
//                .antMatchers("/api/v1.0/moviebooking/**").permitAll()
//                .anyRequest().authenticated();
//
//        http.authenticationProvider(authenticationProvider());
//
//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
    
    
    @Bean
	public  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors().and().csrf().disable()
		.exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("/api/v1.0/moviebooking/getallbookedtickets/{movieName}", "/api/v1.0/moviebooking/{movieName}/update","/api/v1.0/moviebooking//{movieName}/delete")
				.hasRole("ADMIN")
				//.antMatchers("/api/v1.0/moviebooking/{movieName}/add").hasAnyRole("ROLE_ADMIN", "ROLE_USER")
				.antMatchers("/api/v1.0/moviebooking/all","/bookedSeats/{movieName}/{theaterName}","/seats/{totalSeats}","/api/v1.0/moviebooking/login", "/api/v1.0/moviebooking/register", "/api-docs", "/**",
						"/swagger-ui.html", "/actuator/**")
				.permitAll().anyRequest().authenticated();

		http.authenticationProvider(authenticationProvider());

		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}
    
}
