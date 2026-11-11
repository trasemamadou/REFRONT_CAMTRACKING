package com.groupeisi.ms1_gestschool.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    // 📜 Historique conversation
    @GetMapping("/{u1}/{u2}")
    public List<Message> getConversation(@PathVariable String u1,
                                         @PathVariable String u2) {
        return messageService.getConversation(u1, u2);
    }

    // 👁️ Marquer messages vus
    @PutMapping("/seen")
    public void markAsSeen(@RequestParam String sender,
                           @RequestParam String receiver) {
        messageService.markMessagesAsSeen(sender, receiver);
    }
}