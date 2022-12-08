package com.halushko.kinocat.middleware.rabbit;

import com.halushko.kinocat.middleware.handlers.input.InputMessageHandler;
import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

public class RabbitUtils {
    private final static String RABBIT_HOST_IP = System.getenv("RABBIT_HOST_IP");
    private final static String RABBIT_USERNAME = System.getenv("RABBITMQ_DEFAULT_USER");
    private final static String RABBIT_PASSWORD = System.getenv("RABBITMQ_DEFAULT_PASS");
    private final static int RABBIT_PORT = Integer.parseInt(System.getenv("RABBIT_PORT"));

    private static final ConnectionFactory connectionFactory = new ConnectionFactory() {
        {
            setHost(RABBIT_HOST_IP);
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
                    Logger.getRootLogger().error("Error while open connection. ", e);
                    connection = null;
                }
            } while (true);
        }
    }

    private static Connection newConnection() {
        Logger.getRootLogger().debug("[newConnection] Get Connection");
        synchronized (connectionFactory) {
            if (!connection.isOpen()) {
                Logger.getRootLogger().debug("[newConnection] Connection is closed");
                closeConnection();
                connection = createConnection();
                Logger.getRootLogger().debug("[newConnection] New connection created");
            }
            return connection;
        }
    }


    private static void closeConnection() {
        synchronized (connectionFactory) {
            if (connection != null) {
                try {
                    Logger.getRootLogger().debug("[closeConnection] Closing of Connection");
                    connection.close();
                    Logger.getRootLogger().debug("[closeConnection] Connection closed");
                } catch (Exception e) {
                    Logger.getRootLogger().error("[closeConnection] Error while close connection. ", e);
                } finally {
                    connection = null;
                }
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
        Logger.getRootLogger().debug(String.format("[postMessage] Start post rabbit message. message=%s, queue=%s", message.getRabbitMessageText(), queue));
        try (Channel channel = newConnection().createChannel()) {
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getRabbitMessageBytes());
        } catch (Exception e) {
            Logger.getRootLogger().error("[postMessage] Error while post message. ", e);
        }
    }

    public static void postMessage(long chatId, String text, String queue) {
        Logger.getRootLogger().debug(String.format("[postMessage] Start post text message. text=%s, queue=%s", text, queue));
        text = RabbitJson.normalizedValue(text);
        Logger.getRootLogger().debug(String.format("[postMessage] Post text message. text=%s, queue=%s", text, queue));
        postMessage(new RabbitMessage(chatId, text), queue);
    }

    public static void readMessage(String queue, DeliverCallback deliverCallback) {
        try {
            Logger.getRootLogger().debug(String.format("[readMessage] Start read message for queue=%s", queue));
            Channel channel = newConnection().createChannel();
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
            });
        } catch (Exception e) {
            Logger.getRootLogger().error("[readMessage] Error while read message. ", e);
        }
    }
}
