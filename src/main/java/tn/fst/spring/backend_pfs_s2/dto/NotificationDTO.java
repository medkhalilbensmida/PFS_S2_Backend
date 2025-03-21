package tn.fst.spring.backend_pfs_s2.dto;

import lombok.Data;
import tn.fst.spring.backend_pfs_s2.model.TypeNotification;

import java.util.Date;

@Data
public class NotificationDTO {
    private Long id;
    private String message;
    private Date dateEnvoi;
    private Boolean estLue;
    private TypeNotification type;
    private Long enseignantId;
    private Long surveillanceId;
}
