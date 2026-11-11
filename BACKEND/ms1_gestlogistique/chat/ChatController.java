package com.groupeisi.ms1_gestschool.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;  // ← injecter service pour persistance

    // 🔌 Envoi de message
    @MessageMapping("/chat")
    public void sendMessage(Message message) {

        // 🔥 persist
        message.setDateEnvoi(LocalDateTime.now());
        messageService.save(message);

        // 🔥 envoyer au destinataire
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                message
        );
    }

    // 👁️ marquer comme vu
    @MessageMapping("/seen")
    public void markAsSeen(Message message) {
        messageService.markMessagesAsSeen(message.getSenderId(), message.getReceiverId());
    }
}