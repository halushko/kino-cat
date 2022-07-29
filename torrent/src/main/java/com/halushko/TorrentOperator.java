package com.halushko;

import com.halushko.rabKot.handlers.input.InputMessageHandler;
import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.cli.ExecuteBash;
import com.halushko.rabKot.rabbit.RabbitUtils;

import java.util.List;
import java.util.stream.Collectors;

public class TorrentOperator extends InputMessageHandler {
    public static final String EXECUTE_TORRENT_COMMAND_QUEUE = System.getenv("EXECUTE_TORRENT_COMMAND_QUEUE");
    public static final String TELEGRAM_OUTPUT_TEXT = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");

    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        try {
            String text = rabbitMessage.getText();
            long userId = rabbitMessage.getUserId();

            List<String> result = ExecuteBash.executeViaCLI(text);
            String textResult = result.stream().map(a -> a + "\n").collect(Collectors.joining());
            RabbitUtils.postMessage(userId, textResult, TELEGRAM_OUTPUT_TEXT);
        } catch (Exception e) {
            System.out.println("During message handle got an error: " + e.getMessage());
        }
    }

    @Override
    protected String getQueue() {
        return EXECUTE_TORRENT_COMMAND_QUEUE;
    }
}
