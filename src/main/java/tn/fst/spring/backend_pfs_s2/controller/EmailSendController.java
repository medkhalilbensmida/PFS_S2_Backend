package tn.fst.spring.backend_pfs_s2.controller;

import java.util.List; 

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tn.fst.spring.backend_pfs_s2.dto.NotificationEmailDTO;
import tn.fst.spring.backend_pfs_s2.model.Notification;
import tn.fst.spring.backend_pfs_s2.service.EmailSendService;

@RestController
@RequestMapping("/api/emails")    
@RequiredArgsConstructor
public class EmailSendController {
    private final EmailSendService emailSendService;

    @PostMapping("/send-all")
    @Secured("ROLE_ADMIN")
    public void sendEmailsToAll() {
        emailSendService.sendEmailAll();
    }


    @PostMapping("/send-list")
    @Secured("ROLE_ADMIN")
    public void sendEmailsForNotifications(@RequestBody List<Notification> notifications) {
        emailSendService.sendNotificationEmailList(notifications);
    }

    @PostMapping("/send-notif")
    @Secured("ROLE_ADMIN")
    public void sendEmailForNotification(@RequestBody Notification notification) {
        emailSendService.sendNotificationEmailFromNotification(notification);
    }

    @PostMapping("/send-dto")
    @Secured("ROLE_ADMIN")
    public void sendEmailForDTO(@RequestBody NotificationEmailDTO dto) {
        emailSendService.sendNotificationEmail(dto);
    }
}
