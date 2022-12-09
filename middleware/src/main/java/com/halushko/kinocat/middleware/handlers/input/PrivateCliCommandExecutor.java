package com.halushko.kinocat.middleware.handlers.input;

import com.halushko.kinocat.middleware.cli.ExecuteBash;
import com.halushko.kinocat.middleware.cli.ScriptsCollection;
import org.apache.log4j.Logger;

import java.util.List;

public abstract class PrivateCliCommandExecutor {
    protected List<String> execute(String arg) {
        String text = String.format("%s%s", getScript(), arg);
        Logger.getRootLogger().debug(String.format("[PrivateCliCommandExecutor.execute] %s", text));
        return ExecuteBash.executeViaCLI(getScriptsCollection().getCommand(text).getFinalCommand());
    }

    protected abstract String getScript();
    protected abstract ScriptsCollection getScriptsCollection();
}
