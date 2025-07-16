package com.keylee1110.eventapp_backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {
    @Id
    private String id;
    private String username;
    private String password;       // sẽ lưu dưới dạng mã hóa
    private List<String> roles;    // e.g. ["ROLE_USER", "ROLE_ADMIN"]
}