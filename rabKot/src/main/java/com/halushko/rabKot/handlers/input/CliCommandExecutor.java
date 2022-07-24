package com.halushko.rabKot.handlers.input;

import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class CliCommandExecutor extends InputMessageHandler {
    private final String parserId;

    public CliCommandExecutor(String parserId) {
        this.parserId = parserId;
    }
    @Override
    protected void getDeliverCallbackPrivate(RabbitMessage rabbitMessage) {
        Process p = null;
        StringBuilder result = new StringBuilder();

        long userId = rabbitMessage.getUserId();
        String script = rabbitMessage.getText();

        try {
            //TODO sudo is needed or not?
            p = Runtime.getRuntime().exec("sudo " + script);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                for (String outputLine; (outputLine = br.readLine()) != null; )
                    result.append(outputLine).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (p != null) {
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                p.destroy();
            }
        }

        try {
            RabbitUtils.postMessage(userId,  result.toString(), getParserId(), getParserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getParserId() {
        return parserId;
    }
}
