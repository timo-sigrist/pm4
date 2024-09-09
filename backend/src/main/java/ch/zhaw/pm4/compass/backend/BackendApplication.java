package ch.zhaw.pm4.compass.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Entry point for the Compass Backend application.
 * This class configures the Spring Boot application and sets up the CORS configuration.
 * The application excludes the default Spring Security auto-configuration.
 *
 * @autor baumgnoa, bergecyr, brundar, cadowtil, elhaykar, sigritim, weberjas, zimmenoe
 * @version 26.05.2024
 */
@CommonsLog
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class BackendApplication {

	/**
	 * Main method to start the Spring Boot application.
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	/**
	 * Bean to log a message when the application starts.
	 *
	 * @param ctx the application context
	 * @return a CommandLineRunner
	 */
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			log.info("Compass Backend is running...");
		};
	}

	/**
	 * Configuration class to set up CORS (Cross-Origin Resource Sharing) settings.
	 */
	@Configuration
	public static class WebConfig implements WebMvcConfigurer {

		@Value("${cors.allowedOrigins:http://localhost:3000}") // Default to localhost:3000 if not set
		private String[] allowedOrigins;

		@Override
		public void addCorsMappings(CorsRegistry registry) {
			registry.addMapping("/api/**").allowedOrigins(allowedOrigins)
					.allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS").allowedHeaders("*")
					.allowCredentials(true);
		}
	}
}
