package com.donut.swaipe.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
@Slf4j
public class FCMConfig {

    @Value("${FIREBASE_CREDENTIALS_PATH}")
    private String serviceAccountPath;

    @Value("${FIREBASE_SERVER_KEY}")
    private String serverKey;

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        try {
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new ClassPathResource(serviceAccountPath).getInputStream())
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("Firebase 앱 초기화 완료");
            }

            log.info("Firebase 메시징 인스턴스 생성 완료");
            return FirebaseMessaging.getInstance();

        } catch (IOException e) {
            log.error("Firebase 초기화 중 에러 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Firebase 초기화 중 에러가 발생했습니다", e);
        }
    }

    @Bean
    public GoogleCredentials googleCredentials() {
        try {
            return GoogleCredentials
                    .fromStream(new ClassPathResource(serviceAccountPath).getInputStream())
                    .createScoped(Arrays.asList("https://www.googleapis.com/auth/firebase.messaging"));
        } catch (IOException e) {
            log.error("GoogleCredentials 생성 중 에러 발생: {}", e.getMessage(), e);
            throw new RuntimeException("GoogleCredentials 생성 중 에러가 발생했습니다", e);
        }
    }
}