package com.halushko.rabKot.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static com.halushko.rabKot.handlers.input.InputMessageHandler.MEDIUM_PAUSE_MILIS;

public class RabbitUtils {
    private final static String RABBIT_HOST_IP = System.getenv("RABBIT_HOST_IP");//"172.17.0.1";
    private final static String RABBIT_USERNAME = System.getenv("RABBITMQ_DEFAULT_USER");//"dima";
    private final static String RABBIT_PASSWORD = System.getenv("RABBITMQ_DEFAULT_PASS");//"dima";
    private final static int RABBIT_PORT = Integer.parseInt(System.getenv("RABBIT_PORT"));//5672;
//    final static int RABBIT_CONNECTION_TIMEOUT = 60000;

    private static Connection connection;

    private static Connection newConnection() throws IOException, TimeoutException {
        if (connection != null && !connection.isOpen()) {
            connection.close();
            connection = null;
        }
        if (connection == null) {
            System.out.println("RABBIT_USERNAME=" + RABBIT_USERNAME);
            System.out.println("RABBIT_PASSWORD=" + RABBIT_PASSWORD);
            System.out.println("RABBIT_HOST_IP=" + RABBIT_HOST_IP);
            System.out.println("RABBIT_PORT=" + RABBIT_PORT);
            connection = new ConnectionFactory() {
                {
                    setHost(RABBIT_HOST_IP);
                    setUsername(RABBIT_USERNAME);
                    setPassword(RABBIT_PASSWORD);
                    setPort(RABBIT_PORT);
//                    setConnectionTimeout(RABBIT_CONNECTION_TIMEOUT);
                    setRequestedHeartbeat(20);
                }
            }.newConnection();
        }
        return connection;
    }

    private static void postMessage(RabbitMessage message, String queue) throws IOException, TimeoutException {
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
                postMessage(new RabbitMessage(chatId, text, consumer), queue);
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
        }
    }
}
