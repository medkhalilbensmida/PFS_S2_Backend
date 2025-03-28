package tn.fst.spring.backend_pfs_s2.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmailDTO {
    private String toEmail;
    private String subject;
    private String message;
    private Date date;
    private String template;
}
