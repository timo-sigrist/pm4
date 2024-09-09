package ch.zhaw.pm4.compass.backend.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration class that sets up web security and OAuth2 resource server configuration.
 * This class enables conditional access to API documentation through Swagger based on application properties
 * and enforces authentication on all other HTTP requests.
 *
 * @EnableWebSecurity marks the application to use the default Spring Security configuration,
 * enhancing method security handling.
 * @Configuration indicates that this class is a source of bean definitions.
 *
 * @author baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Value("${springdoc.api-docs.enabled}")
	private Boolean swaggerOn;

	/**
	 * Defines the security filter chain that applies to incoming HTTP requests.
	 * This bean configures authorization aspects for the application, particularly focusing on OAuth2 as the security protocol.
	 *
	 * @param http the {@link HttpSecurity} to configure
	 * @return the configured {@link SecurityFilterChain}
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests((authorize) -> {
			if (swaggerOn) {
				authorize.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
			}
			authorize.anyRequest().authenticated();
		}).oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())).build();
	}
}