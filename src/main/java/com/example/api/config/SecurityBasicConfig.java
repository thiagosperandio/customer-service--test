package com.example.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityBasicConfig {

	@Value("${app.auth.user}")
	private String userName;

	@Value("${app.auth.password}")
	private String password;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
		String appHealthCheck = "/actuator/health";
		String staticWebAssets = "/resources/**";
		httpSecurity.csrf(CsrfConfigurer::disable)
				.authorizeHttpRequests(requests -> {
					requests
							.antMatchers(appHealthCheck).permitAll()
							.antMatchers(staticWebAssets).permitAll()
							.anyRequest().authenticated();
				})
				.httpBasic(Customizer.withDefaults());

		return httpSecurity.build();
	}

	/**
	 * Basic authentication to sample application only, NOT to production
	 * applications.
	 *
	 * @see User#withDefaultPasswordEncoder
	 */
	@Bean
	public InMemoryUserDetailsManager userDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder()
				.username(userName)
				.password(password)
				.roles("USER")
				.build();
		return new InMemoryUserDetailsManager(user);
	}
}
