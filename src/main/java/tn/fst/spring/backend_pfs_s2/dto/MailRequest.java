package tn.fst.spring.backend_pfs_s2.dto;


import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.List;
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

    private List<Attachment> attachments; // New field

    @Data
    public static class Attachment {
        private String fileName;
        private byte[] fileData; // You can also use InputStreamSource
    }

    private Map<String, Object> context;
}
