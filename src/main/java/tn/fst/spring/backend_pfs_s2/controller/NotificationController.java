package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import tn.fst.spring.backend_pfs_s2.dto.NotificationDTO;
import tn.fst.spring.backend_pfs_s2.model.Notification;
import tn.fst.spring.backend_pfs_s2.service.NotificationService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;

    public NotificationController(SimpMessagingTemplate messagingTemplate, 
                                NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
    }

    @MessageMapping("/notifications.load")
    public void loadInitialNotifications(Principal principal) {
        String email = principal.getName();
        List<Notification> notifications = notificationService.getNotificationsByUserId(email);
        
        // Convert to simple DTOs
        List<Map<String, Object>> notificationDtos = notifications.stream()
        .map(n -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", n.getId());
            map.put("message", n.getMessage());
            map.put("dateEnvoi", n.getDateEnvoi().getTime()); // Convert to timestamp
            map.put("estLue", n.getEstLue());
            map.put("type", n.getType().name());
            return map;
        })
        .collect(Collectors.toList());
    
        messagingTemplate.convertAndSendToUser(
            email,
            "/queue/notifications",
            notificationDtos
        );
    }

    @PostMapping("/mark-read")
    public ResponseEntity<?> markNotificationsAsRead(@RequestBody List<Long> notificationIds) {
        notificationService.markMultipleAsRead(notificationIds);
        return ResponseEntity.ok().build();
    }

    public void sendNotificationToUser(Long userId, Notification notification) {
        messagingTemplate.convertAndSendToUser(
            userId.toString(),
            "/queue/notifications",
            notification
        );
    }

    @GetMapping("/notifs")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }


    private  NotificationDTO toEntity (Notification notification ){
        NotificationDTO dto = new NotificationDTO(); 
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setDateEnvoi(notification.getDateEnvoi());
        dto.setEstLue(notification.getEstLue());
        dto.setType(notification.getType());
        return dto;
    }
}