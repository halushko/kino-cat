package com.halushko.kinocat.file;

import com.halushko.kinocat.core.JsonConstants.SmartJsonKeys;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.input.CliCommandExecutor;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public class MoveToDestinationFolder extends CliCommandExecutor {
    @Override
    protected String getQueue() {
        return Queues.File.MOVE_TO_FOLDER;
    }

    @Override
    protected String getScript(SmartJson rabbitMessage) {
        List<Object> arguments = rabbitMessage.getSubMessage(SmartJsonKeys.COMMAND_ARGUMENTS).convertToList();
        String folder = (String) arguments.get(0);
        String file = (String) arguments.get(1);

        return String.format("bash -c \"mv -f %s/%s.torrent %s/%s.torrent\"",
                Constants.PATH_TO_UNAPPROVED_FOLDER,
                file,
                Constants.FOLDERS.get(folder),
                UUID.randomUUID()
        );
    }
}