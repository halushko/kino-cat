package com.halushko.kinocat.core;

public interface JsonConstants {
    enum SmartJsonKeys {
        USER_ID, TEXT, COMMAND_ARGUMENTS, INPUT, OUTPUT, FILE_PATH, MIME_TYPE, SIZE, CAPTION, FILE_ID, SELECT_SERVER
    }
    interface WebKeys {
        String KEY_PROTOCOL = "protocol";
        String KEY_IP = "ip";
        String KEY_PORT = "port";
        String KEY_SUFFIX = "suffix";
        String KEY_NAME = "name";
    }
}
