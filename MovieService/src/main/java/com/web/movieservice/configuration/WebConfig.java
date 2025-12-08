package com.web.movieservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/posters}")
    private String uploadDir;

    /**
     * Cấu hình truy cập file tĩnh (ảnh upload)
     */
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry
                    .addResourceHandler("/uploads/**")
                    .addResourceLocations("file:/uploads/");
        }

//    /**
//     * Cấu hình CORS cho frontend (ví dụ localhost:3001)
//     */
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                // Cho phép frontend truy cập (có thể ghi rõ localhost:3001 nếu muốn)
//                .allowedOriginPatterns("*")
//                // Hoặc dùng:
//                // .allowedOrigins("http://localhost:3001")
//
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                .exposedHeaders("*")
//                .allowCredentials(false)
//                .maxAge(3600);
//    }
}
