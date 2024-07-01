package com.halushko.kinocat.file;

import com.halushko.kinocat.core.Queues;
import com.halushko.kinocat.core.handlers.input.CliCommandExecutor;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;

@Slf4j
public class CheckDiskFreeSpace extends CliCommandExecutor {
    @Override
    protected String getResultString(List<String> lines, SmartJson rabbitMessage) {
        log.debug(String.format("[CheckDiskFreeSpace] [%s]", String.join(", ", lines)));
        StringBuilder sb = new StringBuilder("Вільного місця у сховищі:");

        for (val device : Constants.FOLDERS.entrySet()) {
            sb.append("\nfolder: ").append(device.getKey()).append(": ").append(device.getValue());
        }
        for (val device : Constants.DEVICES.entrySet()) {
            sb.append("\n device: ").append(device.getKey()).append(": ").append(device.getValue());
        }
        for (String line : lines) {
            sb.append("\nres: ").append(line);
        }


        for (val device : Constants.FOLDERS.entrySet()) {
            val key = device.getKey();
            if (Constants.DEVICES.containsKey(key)) {
                val value = Constants.DEVICES.get(key);
                for (String line : lines) {
                    if (line.matches(value + ".*")) {
                        sb.append("\n")
                                .append(key)
                                .append(": ")
                                .append(
                                line.replaceAll("^\\S+\\s+\\S+\\s+\\S+\\s+", "")
                                        .replaceAll("\\S+\\s+\\S+\\s*$", "")
                        );
                    }
                }
            }
        }

        return sb.toString();
    }

    @Override
    protected String getQueue() {
        return Queues.File.SHOW_FREE_SPACE;
    }

    @Override
    protected String[] getScript(SmartJson rabbitMessage) {
        return new String[]{
                "/bin/df",
                "-h"
        };
    }
}
