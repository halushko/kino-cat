package com.halushko.kinocat.middleware.cli;

@SuppressWarnings("unused")
public interface Constants {
    interface Queues {
        interface Telegram {
            String TELEGRAM_INPUT_FILE = "TELEGRAM_INPUT_FILE_QUEUE";
            String TELEGRAM_OUTPUT_TEXT = "TELEGRAM_OUTPUT_TEXT_QUEUE";
            String TELEGRAM_INPUT_TEXT = "TELEGRAM_INPUT_TEXT_QUEUE";
        }

        interface Torrent {
            String EXECUTE_VOID_TORRENT_COMMAND = "EXECUTE_TORRENT_COMMAND_QUEUE";
            String EXECUTE_TORRENT_COMMAND_LIST = "EXECUTE_TORRENT_COMMAND_LIST";
            String EXECUTE_TORRENT_COMMAND_INFO = "EXECUTE_TORRENT_COMMAND_INFO";
            String EXECUTE_TORRENT_COMMAND_COMMANDS = "EXECUTE_TORRENT_COMMAND_FILE_COMMANDS";
            
        }

        interface MediaServer {
            String EXECUTE_MINIDLNA_COMMAND = "EXECUTE_MINIDLNA_COMMAND_QUEUE";
        }
    }

    interface Commands {
        interface Torrent {
            String START_TORRENT_FILE = "/start_torrent";
            String LIST_TORRENT_COMMANDS = "/more_";
            String LIST_TORRENT_RESUME ="/resume_";
            String LIST_TORRENT_PAUSE = "/pause_";
            String LIST_TORRENT_INFO ="/full_info_";
        }
    }
}
