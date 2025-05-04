package tn.fst.spring.backend_pfs_s2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tn.fst.spring.backend_pfs_s2.controller.NotificationController;
import tn.fst.spring.backend_pfs_s2.model.Administrateur;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.model.Notification;
import tn.fst.spring.backend_pfs_s2.model.NotificationEventPublisher;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.model.TypeNotification;
import tn.fst.spring.backend_pfs_s2.repository.AdministrateurRepository;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;
import tn.fst.spring.backend_pfs_s2.repository.NotificationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationEventPublisher eventPublisher;
    private final EnseignantRepository enseignantRepository;
    private final AdministrateurRepository administrateurRepository;

    public List<Notification> getNotificationsByUserId(String email) {
        return notificationRepository.findByUserEmailWithFetch(email);
    }

    public Notification createNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        eventPublisher.publishNotificationCreated(saved);
        return saved;
    }
    
    @Transactional
    public void markMultipleAsRead(List<Long> notificationIds) {
        notificationRepository.markAsRead(notificationIds);
    }


    @Transactional
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id).orElse(null);
    }

    public Notification updateNotification(Long id, Notification notification) {
        if (notificationRepository.existsById(id)) {
            notification.setId(id);
            return notificationRepository.save(notification);
        }
        return null;
    }

    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }
    public void sendSessionDeletedNotification(Enseignant enseignant, Surveillance surveillance) {
        Notification notification = new Notification();
        notification.setType(TypeNotification.ANNULATION);
        notification.setMessage("La surveillance du " + surveillance.getDateDebut() + " vers " + surveillance.getDateFin() + " a été annulée");
        notification.setEnseignantDestinataire(enseignant);
        
        Notification saved = this.createNotification(notification);
        
        // Create DTO
        Map<String, Object> notificationDto = new HashMap<>();
        notificationDto.put("id", saved.getId());
        notificationDto.put("message", saved.getMessage());
        notificationDto.put("dateEnvoi", saved.getDateEnvoi().getTime());
        notificationDto.put("estLue", saved.getEstLue());
        notificationDto.put("type", saved.getType().name());
        
        // Send via WebSocket
        messagingTemplate.convertAndSendToUser(
            enseignant.getEmail(),
            "/queue/notifications",
            notificationDto
        );
    }


}