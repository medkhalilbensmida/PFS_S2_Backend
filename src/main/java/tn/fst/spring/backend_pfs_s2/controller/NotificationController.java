package tn.fst.spring.backend_pfs_s2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.fst.spring.backend_pfs_s2.dto.NotificationDTO;
import tn.fst.spring.backend_pfs_s2.model.Notification;
import tn.fst.spring.backend_pfs_s2.service.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping
    public List<NotificationDTO> getAllNotifications() {
        return notificationService.getAllNotifications().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public NotificationDTO getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id);
        return convertToDTO(notification);
    }

    @PostMapping
    public NotificationDTO createNotification(@RequestBody NotificationDTO notificationDTO) {
        Notification notification = convertToEntity(notificationDTO);
        Notification createdNotification = notificationService.createNotification(notification);
        return convertToDTO(createdNotification);
    }

    @PutMapping("/{id}")
    public NotificationDTO updateNotification(@PathVariable Long id, @RequestBody NotificationDTO notificationDTO) {
        Notification notification = convertToEntity(notificationDTO);
        Notification updatedNotification = notificationService.updateNotification(id, notification);
        return convertToDTO(updatedNotification);
    }

    @DeleteMapping("/{id}")
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
        dto.setEnseignantId(notification.getDestinataire().getId());
        dto.setSurveillanceId(notification.getSurveillance().getId());
        return dto;
    }

    private Notification convertToEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setMessage(dto.getMessage());
        notification.setDateEnvoi(dto.getDateEnvoi());
        notification.setEstLue(dto.getEstLue());
        notification.setType(dto.getType());
        // Vous devez récupérer les entités associées par leurs IDs ici
        return notification;
    }
}
