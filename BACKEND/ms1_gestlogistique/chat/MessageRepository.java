package com.groupeisi.ms1_gestschool.chat;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // 🔹 Historique conversation
    @Query("""
        SELECT m FROM Message m
        WHERE m.conversationId = :convId
        ORDER BY m.dateEnvoi ASC
    """)
    List<Message> getConversation(@Param("convId") String convId);

    // 🔹 Marquer messages comme vus
    @Modifying
    @Transactional
    @Query("""
        UPDATE Message m
        SET m.vue = true
        WHERE m.senderId = :senderId
        AND m.receiverId = :receiverId
        AND m.vue = false
    """)
    void markAsSeen(@Param("senderId") String senderId,
                    @Param("receiverId") String receiverId);
}