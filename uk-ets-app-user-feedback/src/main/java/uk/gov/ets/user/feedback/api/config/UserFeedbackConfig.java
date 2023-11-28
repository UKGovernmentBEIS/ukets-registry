package uk.gov.ets.user.feedback.api.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class UserFeedbackConfig {
	 @Value("${application.url}")
	    String applicationUrl;

	    @Bean
	    CorsConfigurationSource corsConfigurationSource() {
	        CorsConfiguration configuration = new CorsConfiguration();
	        configuration.setAllowedOrigins(List.of(applicationUrl));
	        configuration.setAllowedMethods(List.of("*"));
	        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	        source.registerCorsConfiguration("/**", configuration);
	        return source;
	    }
}
