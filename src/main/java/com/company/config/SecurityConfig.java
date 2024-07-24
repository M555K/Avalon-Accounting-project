package com.company.config;

import com.company.service.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {
    private final SecurityService securityService;
    private final AuthSuccessHandler authSuccessHandler;

    public SecurityConfig(SecurityService securityService, AuthSuccessHandler authSuccessHandler) {
        this.securityService = securityService;
        this.authSuccessHandler = authSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http.authorizeHttpRequests()
                .antMatchers("/company/**").hasAuthority("Root User")// has to match with DB
                .antMatchers("/user/**").hasAnyAuthority("Root User","Admin")
                .antMatchers("/category/**").hasAnyAuthority("Admin","Manager","Employee")
                .antMatchers("/product/**").hasAnyAuthority("Admin","Manager","Employee")
                .antMatchers("/clientVendor/**").hasAnyAuthority("Admin","Manager","Employee")
                .antMatchers("/reporting/**").hasAnyAuthority("Admin","Manager")
                .antMatchers("/invoices/**").hasAnyAuthority("Admin","Manager","Employee")
                .antMatchers("/login","/fragments/**","/dashboard")//excluded pages
                .permitAll()//anybody can access pages
                .anyRequest().authenticated()
                .and()
                // .httpBasic()//pop up
                .formLogin()
                .loginPage("/login")
                //.defaultSuccessUrl("/login") //everyone will see every page
                .successHandler(authSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
                .and()
                .rememberMe()
                .tokenValiditySeconds(864000)
                .key("avalon")
                .userDetailsService(securityService)// to capture the user in the system
                .and().build();
    }

}
