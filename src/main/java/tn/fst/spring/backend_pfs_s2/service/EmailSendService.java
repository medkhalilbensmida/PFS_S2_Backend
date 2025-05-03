package tn.fst.spring.backend_pfs_s2.service;

import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import tn.fst.spring.backend_pfs_s2.dto.MailRequest;
import tn.fst.spring.backend_pfs_s2.dto.NotificationDTO;
import tn.fst.spring.backend_pfs_s2.dto.NotificationEmailDTO;
import tn.fst.spring.backend_pfs_s2.model.Enseignant;
import tn.fst.spring.backend_pfs_s2.model.Notification;
import tn.fst.spring.backend_pfs_s2.model.SessionExamen;
import tn.fst.spring.backend_pfs_s2.model.Surveillance;
import tn.fst.spring.backend_pfs_s2.repository.NotificationRepository;
import tn.fst.spring.backend_pfs_s2.repository.SurveillanceRepository;
import tn.fst.spring.backend_pfs_s2.repository.EnseignantRepository;


@Service
@RequiredArgsConstructor
public class EmailSendService{
    private final MailService mailService;
    private final NotificationRepository notificationRepository;
    private final EnseignantRepository enseignantRepository;
    private final SurveillanceRepository surveillanceRepository;
    private final SessionExamenService sessionExamenService;
   
    
    public void sendEmailAll() {
        List<Notification> notifications = notificationRepository.findAll();
        sendNotificationEmailList(notifications);
    }

    public void sendNotificationEmailList(List<Notification> notifications){
        for (Notification notification : notifications) {
            if (notification.getDestinataire() != null && notification.getDestinataire().getEmail() != null) {
               try{
                   sendNotificationEmailFromNotification(notification);
               }
               catch (Exception e  ){
                e.printStackTrace();
               }
            }
        }
    }
    private NotificationEmailDTO toNotificatioDto(Notification notification){
        NotificationEmailDTO dto = new NotificationEmailDTO(
            notification.getDestinataire().getEmail(),
            "Nouvelle Notification: " + notification.getType(),
            notification.getMessage(),
            notification.getDateEnvoi(),
            notification.getType().toString(),
            null
        );
        return dto;
    }

    public void sendNotificationEmailFromNotification(Notification notification) throws Exception{
        NotificationEmailDTO dto = toNotificatioDto(notification);
        sendNotificationEmail(dto);
        notification.markEmailAsSent();
        notificationRepository.save(notification);
    }

        public void sendNotificationEmail(NotificationEmailDTO dto) throws Exception {
                // Create mail request
                MailRequest mailRequest = new MailRequest();
                mailRequest.setToEmail(dto.getToEmail());
                mailRequest.setSubject(dto.getSubject());
                mailRequest.setTemplate(dto.getTemplate());
                mailRequest.setIsHtml(true);

                // Context variablesy
                    System.out.println("THE DTO IS" + dto);
                    Map<String, Object> context = this.generateContext(dto.getSession(),dto.getTemplate(),dto.getToEmail());
                    context.put("message", dto.getMessage()); // Optionally still override or add some values
                    mailRequest.setContext(context);
        
                    // Send email

                    mailService.sendEmail(mailRequest);
                
        
        } 
        @Transactional
        private Map<String, Object> generateContext(Long sessionId, String template, String email) throws Exception  {
            try{
            HashMap<String, Object> context = new HashMap<>();
            
            if (sessionId == null) {
                return context;
            }
            System.out.println("Sending to: " + email);

        
            // Get professor data
            Enseignant prof = enseignantRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("No Enseignant found with email: " + email));
            

            // Add professor info to context
            context.put("professor",prof);


        
            // Get session with details
            SessionExamen session = sessionExamenService.getSessionWithDetails(sessionId);
            if (session != null) {
                // Find the professor's surveillance in this session

                Optional<Surveillance> professorSurveillance = session.getSurveillances().stream()
                    .filter(s -> (s.getEnseignantPrincipal() != null && s.getEnseignantPrincipal().getId().equals(prof.getId())) ||
                                (s.getEnseignantSecondaire() != null && s.getEnseignantSecondaire().getId().equals(prof.getId())))
                    .findFirst();
                
                if (professorSurveillance.isPresent()) {
                    Surveillance surveillance = professorSurveillance.get();
                    
                    // Build surveillance context
                    Map<String, Object> surveillanceContext = new HashMap<>();
                    surveillanceContext.put("id", surveillance.getId());
                    surveillanceContext.put("dateDebut", surveillance.getDateDebut());
                    surveillanceContext.put("dateFin", surveillance.getDateFin());
                    
                    if (surveillance.getMatiere() != null) {
                        surveillanceContext.put("matiere", Map.of(
                            "nom", surveillance.getMatiere().getNom(),
                            "code", surveillance.getMatiere().getCode()
                        ));
                    }
                    
                    if (surveillance.getSalle() != null) {
                        surveillanceContext.put("salle", Map.of(
                            "nom", surveillance.getSalle().getNumero(),
                            "numero", surveillance.getSalle().getEtage()
                        ));
                    }
                    
                    context.put("surveillance", surveillanceContext);
                    
                    // Calculate duration in hours
                    if (surveillance.getDateDebut() != null && surveillance.getDateFin() != null) {
                        long durationMillis = surveillance.getDateFin().getTime() - surveillance.getDateDebut().getTime();
                        double durationHours = durationMillis / (1000.0 * 60 * 60);
                        context.put("duree", String.format("%.1f", durationHours));
                    }
                    else {
                        context.put("duree", 0);
                    }
                }
                else {
                    throw new Exception("Please verify the Professor Email.");
                }
            }
        
            // Add common context variables
            context.put("baseUrl", "https://localhost:4200");
            context.put("currentDate", new Date());
            
            return context;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new Exception("Failed to send email: " + e.getMessage(), e);
        }
    }

    }
