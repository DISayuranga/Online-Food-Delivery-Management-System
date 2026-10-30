package com.fooddelivery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:[*]", "http://127.0.0.1:[*]", "*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        File frontendDir = new File("../frontend");
        if (!frontendDir.exists()) {
            frontendDir = new File("frontend");
        }
        if (frontendDir.exists()) {
            String frontendPath = frontendDir.toURI().toString();
            registry.addResourceHandler("/**")
                    .addResourceLocations(frontendPath, "classpath:/static/", "classpath:/public/");
        }

        File uploadsDir = new File("uploads");
        if (!uploadsDir.exists()) uploadsDir.mkdirs();
        File frontendUploads = new File("frontend/uploads").exists() ? new File("frontend/uploads") : new File("../frontend/uploads");
        if (!frontendUploads.exists()) frontendUploads.mkdirs();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsDir.toURI().toString(), frontendUploads.toURI().toString(), "file:uploads/", "file:frontend/uploads/", "file:../frontend/uploads/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
