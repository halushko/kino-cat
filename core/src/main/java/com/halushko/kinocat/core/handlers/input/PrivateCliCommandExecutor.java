package com.halushko.kinocat.core.handlers.input;

import com.halushko.kinocat.core.cli.ExecuteBash;
import com.halushko.kinocat.core.cli.ScriptsCollection;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public abstract class PrivateCliCommandExecutor {
    protected List<String> execute(String arg) {
        String text = String.format("%s%s", getScript(), arg);
        log.debug(String.format("[PrivateCliCommandExecutor.execute] %s", text));
        return ExecuteBash.executeViaCLI(getScriptsCollection().getCommand(text).getFinalCommand());
    }

    protected abstract String getScript();
    protected abstract ScriptsCollection getScriptsCollection();
}
