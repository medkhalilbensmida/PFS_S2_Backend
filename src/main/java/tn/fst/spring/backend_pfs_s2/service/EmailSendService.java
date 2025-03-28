package tn.fst.spring.backend_pfs_s2.service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;


import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import tn.fst.spring.backend_pfs_s2.dto.MailRequest;
import tn.fst.spring.backend_pfs_s2.dto.NotificationDTO;
import tn.fst.spring.backend_pfs_s2.dto.NotificationEmailDTO;
import tn.fst.spring.backend_pfs_s2.model.Notification;
import tn.fst.spring.backend_pfs_s2.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
public class EmailSendService{
    private final MailService mailService;
    private final NotificationRepository notificationRepository;
   
    
    public void sendEmailAll() {
        List<Notification> notifications = notificationRepository.findAll();
        sendNotificationEmailList(notifications);
    }

    public void sendNotificationEmailList(List<Notification> notifications){
        for (Notification notification : notifications) {
            if (notification.getDestinataire() != null && notification.getDestinataire().getEmail() != null) {
                sendNotificationEmailFromNotification(notification);
            }
        }
    }
    private NotificationEmailDTO toNotificatioDto(Notification notification){
        NotificationEmailDTO dto = new NotificationEmailDTO(
            notification.getDestinataire().getEmail(),
            "Nouvelle Notification: " + notification.getType(),
            notification.getMessage(),
            notification.getDateEnvoi(),
            notification.getType().toString()
        );
        return dto;
    }

    public void sendNotificationEmailFromNotification(Notification notification){
        NotificationEmailDTO dto = toNotificatioDto(notification);
        sendNotificationEmail(dto);
        notification.markEmailAsSent();
        notificationRepository.save(notification);
    }

    public void sendNotificationEmail(NotificationEmailDTO dto) {
        try {
            // Create mail request
            MailRequest mailRequest = new MailRequest();
            mailRequest.setToEmail(dto.getToEmail());
            mailRequest.setSubject(dto.getSubject());
            mailRequest.setTemplate(dto.getTemplate());
            mailRequest.setIsHtml(true);

            // Context variables
            Map<String, Object> context = new HashMap<>();
            context.put("message", dto.getMessage());
            mailRequest.setContext(context);

            // Send email
            mailService.sendEmail(mailRequest);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
            
    } 

}
