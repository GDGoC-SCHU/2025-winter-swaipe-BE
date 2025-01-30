package com.donut.swaipe.domain.notification.controller;

import com.donut.swaipe.domain.notification.dto.NotificationRequest;
import com.donut.swaipe.domain.notification.dto.NotificationResponse;
import com.donut.swaipe.domain.notification.dto.NotificationHistoryResponse;
import com.donut.swaipe.domain.notification.service.NotificationService;
import com.donut.swaipe.global.common.ApiResponse;
import com.donut.swaipe.global.security.details.UserDetailsImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* 알림 관련 요청을 처리하는 컨트롤러입니다.
*
* @author donut
* @version 1.0
* @since 2024-01-28
*/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notification", description = "Notification API")
public class NotificationController {
   private final NotificationService notificationService;

   @PostMapping
   @Operation(summary = "send notification", description = "Send push notification to user")
   public ApiResponse<NotificationResponse> sendNotification(@Valid @RequestBody NotificationRequest request) {
       return notificationService.sendNotification(request);
   }

   @GetMapping("/users/me")
   @Operation(summary = "get my notifications", description = "Get current user's notification history")
   public ApiResponse<List<NotificationHistoryResponse>> getMyNotifications(
           @AuthenticationPrincipal UserDetailsImpl userDetails) {
       return notificationService.getUserNotifications(userDetails);
   }

   @GetMapping("/{notificationId}")
   @Operation(summary = "get notification", description = "Get specific notification details")
   public ApiResponse<NotificationHistoryResponse> getNotification(
           @PathVariable Long notificationId,
           @AuthenticationPrincipal UserDetailsImpl userDetails) {
       return notificationService.getNotification(notificationId, userDetails);
   }

   @PatchMapping("/{notificationId}/read")
   @Operation(summary = "mark as read", description = "Mark notification as read")
   public ApiResponse<Boolean> markAsRead(
           @PathVariable Long notificationId,
           @AuthenticationPrincipal UserDetailsImpl userDetails) {
       return notificationService.markAsRead(notificationId, userDetails);
   }

   @DeleteMapping("/{notificationId}")
   @Operation(summary = "delete notification", description = "Delete notification")
   public ApiResponse<Boolean> deleteNotification(
           @PathVariable Long notificationId,
           @AuthenticationPrincipal UserDetailsImpl userDetails) {
       return notificationService.deleteNotification(notificationId, userDetails);
   }
}