package com.halushko.kinocat.file;

import com.halushko.kinocat.core.JsonConstants.SmartJsonKeys;
import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.input.CliCommandExecutor;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class MoveToDestinationFolder extends CliCommandExecutor {
    @Override
    protected String getQueue() {
        return Queues.File.MOVE_TO_FOLDER;
    }

    @Override
    protected String[] getScript(SmartJson rabbitMessage) {
        List<Object> arguments = rabbitMessage.getSubMessage(SmartJsonKeys.COMMAND_ARGUMENTS).convertToList();
        String folder = String.valueOf(arguments.get(0));
        String folderPath = Constants.FOLDERS.get(folder);
        if(folderPath == null || folderPath.isEmpty()) {
            folderPath = Constants.FOLDERS.get("");
        }

        String file = arguments.stream().
                skip(1).
                map(Object::toString).
                collect(Collectors.joining("_")).
                replaceAll("\\s+", "\\\\ ");

        return new String[]{
                "mv",
                "-f",
                String.format("%s/%s.torrent", Constants.PATH_TO_UNAPPROVED_FOLDER, file),
                String.format("%s/%s.torrent", folderPath, UUID.randomUUID())
        };
    }
}