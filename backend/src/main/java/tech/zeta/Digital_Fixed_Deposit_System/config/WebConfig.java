package tech.zeta.Digital_Fixed_Deposit_System.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author Priyanshu Mishra
 */


@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LogManager.getLogger(WebConfig.class);
    private static final String ALLOWED_ORIGINS_PROPERTY = "app.cors.allowed-origins";

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        String allowedOrigins = System.getProperty(ALLOWED_ORIGINS_PROPERTY);
        if (allowedOrigins == null || allowedOrigins.trim().isEmpty()) {
            configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        } else {
            configuration.setAllowedOrigins(List.of(allowedOrigins.split("\\s*,\\s*")));
        }
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE","PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        logger.info("CORS configuration registered");
        return source;
    }

}
