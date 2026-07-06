package sn.khoula.photographique.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Votre nom est obligatoire")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Votre email est obligatoire")
    @Email(message = "Email invalide")
    @Column(nullable = false)
    private String email;

    @Column(name = "phone")
    private String phone;

    @NotBlank(message = "Votre message est obligatoire")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_read")
    private boolean read;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }
}
