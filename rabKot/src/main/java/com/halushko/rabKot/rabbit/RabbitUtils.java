package com.halushko.rabKot.rabbit;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.halushko.rabKot.handlers.input.InputMessageHandler.MEDIUM_PAUSE_MILIS;

public class RabbitUtils {
    private final static String RABBIT_HOST_IP;
    private final static String RABBIT_USERNAME;
    private final static String RABBIT_PASSWORD;
    private final static int RABBIT_PORT;

    static {
        String str = System.getenv("RABBIT_HOST_IP");
        RABBIT_HOST_IP = str != null ? str : "127.0.0.1";
    }
    static {
        String str = System.getenv("RABBIT_USERNAME");
        RABBIT_USERNAME = str != null ? str : "rabbit_user";
    }
    static {
        String str = System.getenv("RABBIT_PASSWORD");
        RABBIT_PASSWORD = str != null ? str : "rabbit_pswrd";
    }
    static {
        String str = System.getenv("RABBIT_PORT");
        int portNo = 5672;
        try {
            portNo = Integer.getInteger(str);
        } catch (Exception ignore) {
        }
        RABBIT_PORT = portNo;
    }

    private static Connection connection;

    private static Connection newConnection() throws IOException, TimeoutException {
//        if (connection != null && !connection.isOpen()) {
//            connection.close();
//            connection = null;
//        }
        if (connection == null) {
            connection = new ConnectionFactory() {
                {
                    setHost(RABBIT_HOST_IP);
                    setUsername(RABBIT_USERNAME);
                    setPassword(RABBIT_PASSWORD);
                    setPort(RABBIT_PORT);
                    setRequestedHeartbeat(20);
                }
            }.newConnection();
            connection.addShutdownListener(new MyShutdownListener());
        }
        return connection;
    }

    static class MyShutdownListener implements ShutdownListener {

        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {
            cause.printStackTrace();
        }
    }

    public static void postMessage(RabbitMessage message, String queue) throws IOException, TimeoutException {
        Connection connection = newConnection();
        try (Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicPublish("", queue, null, message.getRabbitMessageBytes());
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

    public static void readMessage(String queue, DeliverCallback deliverCallback) throws IOException, TimeoutException {
        Connection connection = newConnection();
        try (Channel channel = connection.createChannel()) {
            channel.queueDeclare(queue, false, false, false, null);
            channel.basicConsume(queue, true, deliverCallback, consumerTag -> {
            });
            Thread.sleep(MEDIUM_PAUSE_MILIS);
        } catch (Exception e) {
            System.out.println("Consumer error! " + e.getMessage());
            e.printStackTrace();
        }
    }
}
