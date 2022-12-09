package com.halushko.kinocat.middleware.cli;

public interface Constants {
    interface Queues {
        interface Telegram {
            String TELEGRAM_INPUT_FILE = System.getenv("TELEGRAM_INPUT_FILE_QUEUE");
            String TELEGRAM_OUTPUT_TEXT = System.getenv("TELEGRAM_OUTPUT_TEXT_QUEUE");
            String TELEGRAM_INPUT_TEXT = System.getenv("TELEGRAM_INPUT_TEXT_QUEUE");
        }

        interface Torrent {
            String EXECUTE_VOID_TORRENT_COMMAND = System.getenv("EXECUTE_TORRENT_COMMAND_QUEUE");
            String EXECUTE_TORRENT_COMMAND_LIST = System.getenv("EXECUTE_TORRENT_COMMAND_LIST");
            String EXECUTE_TORRENT_COMMAND_INFO = System.getenv("EXECUTE_TORRENT_COMMAND_INFO");
            String EXECUTE_TORRENT_COMMAND_COMMANDS = System.getenv("EXECUTE_TORRENT_COMMAND_FILE_COMMANDS");
            
        }

        interface MediaServer {
            String EXECUTE_MINIDLNA_COMMAND = System.getenv("EXECUTE_MINIDLNA_COMMAND_QUEUE");
        }
    }

    interface Commands {
        interface Torrent {
            String START_TORRENT_FILE = "/start_torrent";
            String LIST_TORRENT_COMMANDS = "/commands_";
            String LIST_TORRENT_RESUME ="/resume_";
            String LIST_TORRENT_PAUSE = "/pause_";
            String LIST_TORRENT_INFO ="/full_info_";
        }
    }
}
