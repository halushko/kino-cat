package com.halushko.rabKot.rabbit;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.halushko.rabKot.handlers.input.InputMessageHandler.LONG_PAUSE_MILIS;
import static com.halushko.rabKot.handlers.input.InputMessageHandler.MEDIUM_PAUSE_MILIS;

public class RabbitUtils {
    private final static String RABBIT_HOST_IP = System.getenv("RABBIT_HOST_IP");
    private final static String RABBIT_USERNAME = System.getenv("RABBIT_USERNAME");
    private final static String RABBIT_PASSWORD = System.getenv("RABBIT_PASSWORD");
    private final static int RABBIT_PORT = Integer.parseInt(System.getenv("RABBIT_PORT"));

    private static Connection connection;
    private static final ConnectionFactory connectionFactory = new ConnectionFactory() {
        {
            setHost(RABBIT_HOST_IP);
            setUsername(RABBIT_USERNAME);
            setPassword(RABBIT_PASSWORD);
            setPort(RABBIT_PORT);
            setRequestedHeartbeat(20);
        }
    };

    private static Connection newConnection() {
        do {
            closeConnectionIfNeeded();
            try {
                Thread.sleep(LONG_PAUSE_MILIS);
                connection = connectionFactory.newConnection();
                connection.addShutdownListener(new MyShutdownListener());
                return connection;
            } catch (Exception e) {
                System.out.println("Error while open connection. " + e.getMessage());
                e.printStackTrace();
                connection = null;
            }
        } while (true);
    }

    private static void closeConnectionIfNeeded() {
        if (connection != null && !connection.isOpen()) {
            try {
                connection.close();
            } catch (Exception e) {
                System.out.println("Error while close connection. " + e.getMessage());
                e.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }

    static class MyShutdownListener implements ShutdownListener {

        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {
            cause.printStackTrace();
        }
    }

    public static void postMessage(RabbitMessage message, String queue) {
        try (Channel channel = newConnection().createChannel()) {
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getRabbitMessageBytes());
        } catch (Exception e) {
            System.out.println("Error while post message. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void postMessage(long chatId, String text, String queue, String... consumersId) throws IOException, TimeoutException {
        if (consumersId == null || consumersId.length == 0) {
            postMessage(new RabbitMessage(chatId, text), queue);
        } else {
            for (String consumer : consumersId) {
                postMessage(new RabbitMessage(chatId, text).addValue(RabbitMessage.KEYS.CONSUMER, consumer), queue);
            }
        }
    }

    public static void readMessage(String queue, DeliverCallback deliverCallback) {
        try (Channel channel = newConnection().createChannel()) {
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});
            Thread.sleep(MEDIUM_PAUSE_MILIS);
        } catch (Exception e) {
            System.out.println("Error while read message. " + e.getMessage());
            e.printStackTrace();
        }
    }
}
