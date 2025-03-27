package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.NotificationDTO;
import tn.fst.spring.backend_pfs_s2.model.Notification;
import tn.fst.spring.backend_pfs_s2.service.CustomUserDetails;
import tn.fst.spring.backend_pfs_s2.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@Secured({"ROLE_ADMIN", "ROLE_ENSEIGNANT"})
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/all")
    @Secured("ROLE_ADMIN")
    public List<NotificationDTO> getAllNotifications() {
        return notificationService.getAllNotifications().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/my")
    @Secured("ROLE_ENSEIGNANT")
    public List<NotificationDTO> getMyNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long enseignantId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        return notificationService.getNotificationsByEnseignantId(enseignantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public NotificationDTO getNotificationById(@PathVariable Long id) {
        return convertToDTO(notificationService.getNotificationById(id));
    }

    @PostMapping
    @Secured("ROLE_ADMIN")
    public NotificationDTO createNotification(@RequestBody NotificationDTO notificationDTO) {
        Notification created = notificationService.createNotification(convertToEntity(notificationDTO));
        return convertToDTO(created);
    }

    @PutMapping("/{id}")
    public NotificationDTO updateNotification(@PathVariable Long id, @RequestBody NotificationDTO notificationDTO) {
        Notification updated = notificationService.updateNotification(id, convertToEntity(notificationDTO));
        return convertToDTO(updated);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public void deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setDateEnvoi(notification.getDateEnvoi());
        dto.setEstLue(notification.getEstLue());
        dto.setType(notification.getType());
        if (notification.getDestinataire() != null) {
            dto.setEnseignantId(notification.getDestinataire().getId());
        }
        if (notification.getSurveillance() != null) {
            dto.setSurveillanceId(notification.getSurveillance().getId());
        }
        return dto;
    }

    private Notification convertToEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setMessage(dto.getMessage());
        notification.setDateEnvoi(dto.getDateEnvoi());
        notification.setEstLue(dto.getEstLue());
        notification.setType(dto.getType());
        return notification;
    }
}