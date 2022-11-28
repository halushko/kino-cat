package com.halushko.rabKot.rabbit;

import com.rabbitmq.client.*;
import org.apache.log4j.Logger;

import static com.halushko.rabKot.handlers.input.InputMessageHandler.LONG_PAUSE_MILIS;

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
                        Thread.sleep(LONG_PAUSE_MILIS);
                    } catch (InterruptedException ignored) {
                    }
                    Logger.getRootLogger().error("Error while open connection. ", e);
                    connection = null;
                }
            } while (true);
        }
    }

    private static Connection newConnection() {
        Logger.getRootLogger().debug("Get Connection");
        synchronized (connectionFactory) {
            if (!connection.isOpen()) {
                Logger.getRootLogger().debug("Connection is closed");
                closeConnection();
                connection = createConnection();
                Logger.getRootLogger().debug("New connection created");
            }
            return connection;
        }
    }


    private static void closeConnection() {
        synchronized (connectionFactory) {
            if (connection != null) {
                try {
                    Logger.getRootLogger().debug("Closing of Connection");
                    connection.close();
                    Logger.getRootLogger().debug("Connection closed");
                } catch (Exception e) {
                    Logger.getRootLogger().error("Error while close connection. ", e);
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
        try (Channel channel = newConnection().createChannel()) {
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getRabbitMessageBytes());
        } catch (Exception e) {
            Logger.getRootLogger().error("Error while post message. ", e);
        }
    }

    public static void postMessage(long chatId, String text, String queue, String... consumersId) {
        if (consumersId == null || consumersId.length == 0) {
            postMessage(new RabbitMessage(chatId, text), queue);
        } else {
            for (String consumer : consumersId) {
                postMessage(new RabbitMessage(chatId, text).addValue(RabbitMessage.KEYS.CONSUMER, consumer), queue);
            }
        }
    }

    public static void readMessage(String queue, DeliverCallback deliverCallback) {
        try {
            Channel channel = newConnection().createChannel();
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            Logger.getRootLogger().error("Error while read message. ", e);
        }
    }
}
