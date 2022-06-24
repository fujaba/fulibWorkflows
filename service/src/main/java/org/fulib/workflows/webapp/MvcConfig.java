package org.fulib.workflows.webapp;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/api/v1/workflows/**")
            .addResourceLocations(
                new FileSystemResource(System.getProperty("java.io.tmpdir") + "/fulib.org/workflows/"))
            .setCachePeriod(60 * 60);
    }
}
