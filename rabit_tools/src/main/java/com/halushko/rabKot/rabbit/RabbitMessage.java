package com.halushko.rabKot.rabbit;

public class RabbitMessage {
    public static final int ID_LENGTH;
    public static final String DELIMITER;
    private final String text;
    private final long userId;

    static {
        String str = System.getenv("ID_LENGTH");
        ID_LENGTH = str != null ? Integer.parseInt(str) : 11;
    }

    static {
        String str = System.getenv("DELIMITER");
        DELIMITER = str != null ? str : "#";
    }

    public RabbitMessage(long userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    public RabbitMessage(long userId, String text, String someId) {
        this.userId = userId;
        this.text = someId + DELIMITER + text;
    }

    public RabbitMessage(String text) {
        this.text = getTextWithoutId(text);
        this.userId = getIdFromString(text);
    }

    public String getText() {
        return text;
    }
    public String getTextWithoutSomeId() {
        String[] partsOfTextMessage = text.split(DELIMITER, 2);
        return partsOfTextMessage.length == 2 ? partsOfTextMessage[1] : text;
    }

    public long getUserId() {
        return userId;
    }

    public String getSomeId() {
        String[] partsOfTextMessage = text.split(DELIMITER, 2);
        return partsOfTextMessage.length == 2 ? partsOfTextMessage[0] : "";
    }

    public String getRabbitMessageText() {
        return String.format("%0" + ID_LENGTH + "d", getUserId()) + getText();
    }

    public byte[] getRabbitMessageBytes() {
        return getRabbitMessageText().getBytes();
    }

    private static long getIdFromString(String str) {
        return Long.parseLong(str.substring(0, ID_LENGTH));
    }

    private static String getTextWithoutId(String str) {
        return str.substring(ID_LENGTH);
    }
}
