package org.telegram.bot;

import liquibase.pro.packaged.S;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.bot.domain.CommandParent;
import org.telegram.bot.exception.BotException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@AllArgsConstructor
public class Parser extends Thread {

    private final Logger log = LoggerFactory.getLogger(Parser.class);

    private final Bot bot;
    private final CommandParent<?> command;
    private final Update update;

    @Override
    public void run() {
        if (command == null) {
            return;
        }
        log.debug("Find a command {}", command.toString());
        Message message = update.getMessage();
        if (message == null) {
            message = update.getEditedMessage();
            if (message == null) {
                message = update.getCallbackQuery().getMessage();
            }
        }

        try {
            PartialBotApiMethod<?> method = command.parse(update);
            if (method instanceof SendMessage) {
                SendMessage sendMessage = (SendMessage) method;
                log.info("To " + message.getChatId() + ": " + sendMessage.getText());
                bot.execute(sendMessage);
            } else if (method instanceof SendPhoto) {
                SendPhoto sendPhoto = (SendPhoto) method;
                log.info("To " + message.getChatId() + ": sending photo " + sendPhoto.getCaption());
                bot.execute(sendPhoto);
            } else if (method instanceof SendMediaGroup) {
                SendMediaGroup sendMediaGroup = (SendMediaGroup) method;
                log.info("To " + message.getChatId() + ": sending photos " + sendMediaGroup);
                bot.execute(sendMediaGroup);
            } else if (method instanceof EditMessageText) {
                EditMessageText editMessageText = (EditMessageText) method;
                log.info("To " + message.getChatId() + ": edited message " + editMessageText.getText());
                bot.execute(editMessageText);
            } else if (method instanceof SendDocument) {
                SendDocument sendDocument = (SendDocument) method;
                log.info("To " + message.getChatId() + ": sending document " + sendDocument.getCaption());
                bot.execute(sendDocument);
            }
        } catch (TelegramApiException e) {
            log.error("Error: cannot send response: {}", e.getMessage());
        } catch (BotException botException) {
            try {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setReplyToMessageId(message.getMessageId());
                sendMessage.setChatId(message.getChatId().toString());
                sendMessage.setText(botException.getMessage());

                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                log.error("Error: cannot send response: {}", e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
