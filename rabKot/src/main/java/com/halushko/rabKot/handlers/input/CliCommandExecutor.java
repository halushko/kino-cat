package com.halushko.rabKot.handlers.input;

import com.halushko.rabKot.rabbit.RabbitMessage;
import com.halushko.rabKot.rabbit.RabbitUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class CliCommandExecutor extends InputMessageHandler {
    public static final String PARSER_QUEUE = System.getenv("PARSER_QUEUE");

    public static final long USE_SUDO;

    static {
        String str = System.getenv("USE_SUDO");
        USE_SUDO = str != null ? Long.parseLong(str) : 10000L;
    }
    private final String parserId;

    private CliCommandExecutor(){
        this("");
    }
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
            RabbitUtils.postMessage(userId,  result.toString(), PARSER_QUEUE, getParserId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getParserId() {
        return parserId;
    }
}
