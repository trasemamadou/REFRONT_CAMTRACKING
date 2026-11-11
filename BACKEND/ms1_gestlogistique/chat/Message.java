package com.groupeisi.ms1_gestschool.chat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;

    private String senderId;       // 🔹 UUID
    private String receiverId;     // 🔹 UUID

    private String conversationId; // 🔹 généré automatiquement

    private LocalDateTime dateEnvoi;
    private boolean vue = false;
}