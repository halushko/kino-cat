package com.halushko.kinocat.middleware.handlers.input;

import com.halushko.kinocat.middleware.cli.ExecuteBash;
import com.halushko.kinocat.middleware.cli.ScriptsCollection;

import java.util.List;

public abstract class PrivateCliCommandExecutor {
    protected List<String> execute(String arg) {
        return ExecuteBash.executeViaCLI(getScriptsCollection().getCommand(String.format("%s%s", getScript(), arg)).getFinalCommand());
    }

    protected abstract String getScript();
    protected abstract ScriptsCollection getScriptsCollection();
}
