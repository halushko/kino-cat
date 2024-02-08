package com.halushko.kinocat.text;

import com.halushko.kinocat.core.Commands;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.text.commands.CommandProperties;
import com.halushko.kinocat.text.commands.CommandsCollection;

public interface Constants {
    CommandsCollection COMMANDS_COLLECTION = new CommandsCollection() {{
        addValue(Commands.Torrent.LIST_TORRENTS,
                Queues.Torrent.TORRENTS_LIST,
                "відобразити всі торенти. Якщо після команди додати слова, то торенти будуть відфільтровані за іменами з цими словами",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER,
                CommandProperties.CAN_BE_NOT_TORRENT
        );
        addValue(Commands.Torrent.LIST_TORRENT_COMMANDS,
                Queues.Torrent.TORRENT_COMMANDS,
                "<номер торента> перелічити можливі команди для вказаного торента",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER
        );
        addValue(Commands.Torrent.LIST_FILES,
                Queues.Torrent.FILES_LIST,
                "<номер торента> відобразити всі файли, що будуть скачані в цьому торенті",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER,
                CommandProperties.CAN_BE_NOT_TORRENT
        );
        addValue(Commands.Torrent.RESUME,
                Queues.Torrent.RESUME_TORRENT,
                "<номер торента> почати закачувати цей торент",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER
        );
        addValue(Commands.Torrent.PAUSE,
                Queues.Torrent.PAUSE_TORRENT,
                "<номер торента> припинити закачувати цей торент",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER
        );
        addValue(Commands.Torrent.RESUME_ALL,
                Queues.Torrent.RESUME_ALL,
                "почати закачувати всі торенти",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER
        );
        addValue(Commands.Torrent.PAUSE_ALL,
                Queues.Torrent.PAUSE_ALL,
                "припинити закачувати всі тореенти",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER
        );
//        addValue(Torrent.TORRENT_INFO, "", "info_torrent.sh", Queues.Torrent.TORRENT_INFO);
        addValue(Commands.Torrent.REMOVE_WITH_FILES,
                Queues.Torrent.DELETE_WITH_FILES,
                "<номер торента> видалити вказаний торент разом з файлами",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER
        );
        addValue(Commands.Torrent.REMOVE_JUST_TORRENT,
                Queues.Torrent.DELETE_ONLY_TORRENT,
                "<номер торента> видалити вказаний торент, але залишити файли",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER
        );
        addValue(Commands.Torrent.REMOVE_COMMAND,
                Queues.Torrent.DELETE,
                "<номер торента> почати процедуру видалення вказаного торента",
                CommandProperties.CAN_CONTAIN_SERVER_NUMBER
        );

        addValue(Commands.Text.HELP,
                Queues.Text.HELP,
                "вивести інформацію по всім командам"
        );
    }};


}