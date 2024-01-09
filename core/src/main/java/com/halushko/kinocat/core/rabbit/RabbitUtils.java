package com.halushko.kinocat.core.rabbit;

import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RabbitUtils {
    private final static String RABBIT_IP = "192.168.50.132";
    private final static String RABBIT_USERNAME = "rabbit_user";
    private final static String RABBIT_PASSWORD = "rabbit_pswrd";
    private final static int RABBIT_PORT = 5672;

    private static final ConnectionFactory connectionFactory = new ConnectionFactory() {
        {
            setHost(RABBIT_IP);
            setUsername(RABBIT_USERNAME);
            setPassword(RABBIT_PASSWORD);
            setPort(RABBIT_PORT);
            setAutomaticRecoveryEnabled(false);
        }
    };


    private static Connection connection = createConnection();

    @SuppressWarnings("BusyWait")
    private static Connection createConnection() {
        synchronized (connectionFactory) {
            do {
                closeConnection();
                try {
                    connection = connectionFactory.newConnection();
                    connection.addShutdownListener(new MyShutdownListener());
                    return connection;
                } catch (Exception e) {
                    try {
                        Thread.sleep(InputMessageHandler.LONG_PAUSE_MILIS);
                    } catch (InterruptedException ignored) {
                    }
                    log.error("Error while open connection. ", e);
                    connection = null;
                }
            } while (true);
        }
    }

    private static Connection newConnection() {
        log.debug("[newConnection] Get Connection");
        synchronized (connectionFactory) {
            if (!connection.isOpen()) {
                log.debug("[newConnection] Connection is closed");
                closeConnection();
                connection = createConnection();
                log.debug("[newConnection] New connection created");
            }
            return connection;
        }
    }


    private static void closeConnection() {
        synchronized (connectionFactory) {
            if (connection != null) {
                try {
                    log.debug("[closeConnection] Closing of Connection");
                    connection.close();
                    log.debug("[closeConnection] Connection closed");
                } catch (Exception e) {
                    log.error("[closeConnection] Error while close connection. ", e);
                } finally {
                    connection = null;
                }
            }
        }
    }

    @Slf4j
    static class MyShutdownListener implements ShutdownListener {
        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {
            log.error("[shutdownCompleted] Error: ", cause);
        }
    }

    public static void postMessage(SmartJson message, String queue) {
        log.debug("[postMessage] Start post rabbit message. message={}, queue={}", message.getRabbitMessageText(), queue);
        try (Channel channel = newConnection().createChannel()) {
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getRabbitMessageBytes());
        } catch (Exception e) {
            log.error("[postMessage] Error while post message. ", e);
        }
    }

    public static void postMessage(long chatId, String text, String queue) {
        log.debug("[postMessage] Start post text message. text={}, queue={}", text, queue);
        postMessage(new SmartJson(chatId, text), queue);
    }

    public static void readMessage(String queue, DeliverCallback deliverCallback) {
        try {
            log.debug("[readMessage] Start read message for queue={}", queue);
            Channel channel = newConnection().createChannel();
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
            });
        } catch (Exception e) {
            log.error("[readMessage] Error while read message. ", e);
        }
    }
}
