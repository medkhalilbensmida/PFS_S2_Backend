package tn.fst.spring.backend_pfs_s2.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/send-dto")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<String> sendEmailForDTO(@RequestBody NotificationEmailDTO dto) {
        try {
            emailSendService.sendNotificationEmail(dto);
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(e.getMessage());
        }
    }
}
