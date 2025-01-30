package com.donut.swaipe.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // JWT 보안 스키마 이름
        String jwtSchemeName = "JWT";

        // 보안 요구사항을 추가하여 모든 요청이 JWT 인증을 요구하도록 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

        // JWT 보안 스키마 구성
        Components components = new Components().addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                .name(jwtSchemeName)
                .type(SecurityScheme.Type.HTTP) // HTTP 방식으로 설정
                .scheme("Bearer") // HTTP Bearer 방식 사용
                .bearerFormat("JWT") // Bearer 토큰 형식은 JWT
        );

        // OpenAPI 객체 생성 및 보안 구성 추가
        return new OpenAPI()
                .info(apiInfo()) // API 정보 설정
                .addSecurityItem(securityRequirement) // 보안 요구사항 추가
                .components(components); // 보안 스키마 추가
    }

    // API의 기본 정보 설정
    private Info apiInfo() {
        return new Info()
                .title("프로젝트 공유용") // API의 제목
                .description("Let's practice Swagger UI with JWT Authentication") // API에 대한 설명
                .version("1.0.0"); // API의 버전
    }
}
