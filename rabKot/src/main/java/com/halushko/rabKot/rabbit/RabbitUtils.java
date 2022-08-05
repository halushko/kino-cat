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
                    String text = "Error while open connection. " + e.getMessage();
                    System.out.println(text);
                    e.printStackTrace();
                    Logger.getRootLogger().error(text);
                    Logger.getRootLogger().error(e);
                    connection = null;
                }
            } while (true);
        }
    }

    private static Connection newConnection() {
        synchronized (connectionFactory) {
            if (!connection.isOpen()) {
                closeConnection();
                connection = createConnection();
            }
            return connection;
        }
    }


    private static void closeConnection() {
        synchronized (connectionFactory) {
            if (connection != null) {
                System.out.println("Connection closed");
                try {
                    connection.close();
                } catch (Exception e) {
                    String text = "Error while close connection. " + e.getMessage();
                    System.out.println(text);
                    e.printStackTrace();
                    Logger.getRootLogger().error(text);
                    Logger.getRootLogger().error(e);
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
            String text = "Error while post message. " + e.getMessage();
            System.out.println(text);
            e.printStackTrace();
            Logger.getRootLogger().error(text);
            Logger.getRootLogger().error(e);
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
            String text = "Error while read message. " + e.getMessage();
            System.out.println(text);
            e.printStackTrace();
            Logger.getRootLogger().error(text);
            Logger.getRootLogger().error(e);
        }
    }
}
