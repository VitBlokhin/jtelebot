package org.telegram.bot.services.impl;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.bot.domain.entities.Chat;
import org.telegram.bot.domain.enums.AccessLevels;
import org.telegram.bot.repositories.ChatRepository;
import org.telegram.bot.services.ChatService;


@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final Logger log = LoggerFactory.getLogger(ChatServiceImpl.class);

    private final ChatRepository chatRepository;

    @Override
    public Chat get(Long chatId) {
        log.debug("Request to get Chat by chatId: {} ", chatId);
        return chatRepository.findByChatId(chatId);
    }

    @Override
    public Chat save(Chat chat) {
        log.debug("Request to save Chat: {} ", chat);
        return chatRepository.save(chat);
    }

    @Override
    public Integer getChatAccessLevel(Long chatId) {
        log.debug("Request to get chatLevel for chatId {} ", chatId);
        if (chatId > 0) {
            return AccessLevels.NEWCOMER.getValue();
        }
        Chat chat = get(chatId);
        if (chat == null) {
            Chat newChat = new Chat();
            newChat.setChatId(chatId);
            newChat.setAccessLevel(AccessLevels.NEWCOMER.getValue());
            return save(newChat).getAccessLevel();
        }

        return chat.getAccessLevel();
    }
}
