package com.groupeisi.ms1_gestschool.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Message save(Message message) {
        message.setDateEnvoi(LocalDateTime.now());

        // 🔹 Génération conversationId
        String convId = generateConversationId(message.getSenderId(), message.getReceiverId());
        message.setConversationId(convId);

        return messageRepository.save(message);
    }

    public List<Message> getConversation(String u1, String u2) {
        return messageRepository.getConversation(generateConversationId(u1, u2));
    }

    public void markMessagesAsSeen(String sender, String receiver) {
        messageRepository.markAsSeen(sender, receiver);
    }

    // 🔹 Génère conversationId basé sur lexicographie UUID pour constance
    private String generateConversationId(String u1, String u2) {
        return (u1.compareTo(u2) < 0) ? u1 + "_" + u2 : u2 + "_" + u1;
    }
}