package com.halushko.kinocat.text.handlers;

import com.halushko.kinocat.core.JsonConstants.SmartJsonKeys;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.RabbitUtils;
import com.halushko.kinocat.core.rabbit.SmartJson;
import com.halushko.kinocat.text.Constants;
import com.halushko.kinocat.text.commands.Command;
import com.halushko.kinocat.text.commands.CommandProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserTextMessageHandler extends InputMessageHandler {
    @Override
    protected String getDeliverCallbackPrivate(SmartJson rabbitMessage) {
        log.debug("[UserMessageHandler] Start DeliverCallbackPrivate for " + getQueue());
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();
            log.debug("[UserMessageHandler] user_id={} text={}", userId, text);
            Command command = Constants.COMMANDS_COLLECTION.getCommand(text);
            log.debug("[UserMessageHandler] Command: queue={}, arguments={}", command.getQueue(), command.getArguments());

            SmartJson message = new SmartJson(userId).addValue(SmartJsonKeys.COMMAND_ARGUMENTS, command.getArguments());

            if(command.getAdditionalProperties().contains(CommandProperties.EMPTY_INSTANCE)){
                message.addValue(SmartJsonKeys.TEXT, "Такої команди не знайдено");
            }
            if(command.getAdditionalProperties().contains(CommandProperties.CONTAINS_SERVER_NUMBER) && !command.getArguments().isEmpty()){
                String serverNumber = command.getArguments().get(0);
                try {
                    Integer.parseInt(serverNumber);
                    message.addValue(SmartJsonKeys.SELECT_SERVER, serverNumber);
                } catch (NumberFormatException ignored) {
                }
            }

            RabbitUtils.postMessage(message, command.getQueue());
            log.debug("[UserMessageHandler] Finish DeliverCallbackPrivate for {}", getQueue());
        } catch (Exception e) {
            log.error("[UserMessageHandler] During message handle got an error: ", e);
        }
        return "";
    }

    @Override
    protected String getQueue() {
        return Queues.Telegram.TELEGRAM_INPUT_TEXT;
    }
}