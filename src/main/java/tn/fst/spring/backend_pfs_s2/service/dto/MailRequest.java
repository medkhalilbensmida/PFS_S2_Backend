package tn.fst.spring.backend_pfs_s2.service.dto;


import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailRequest {
    @JsonAlias("to_email")
    private String toEmail;

    private String subject;

    private String message;
    
    @JsonAlias("html")
    private Boolean isHtml;

    private String template;

    // private String attachment

    private Map<String, Object> context;
}
