package com.halushko.kinocat.core.files;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Scanner;

@SuppressWarnings("unused")
@Slf4j
public class ResourceReader {
    public static String readResourceContent(String resourceFileName) {
        log.debug("[readResourceContent] resourceFileName={}", resourceFileName);
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try(InputStream inputStream = classLoader.getResourceAsStream(resourceFileName)) {
            if (inputStream == null) {
                log.error("[readResourceContent] result is: Cant read such resource file name={}. The input stream is NULL", resourceFileName);
            } else {
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                if (scanner.hasNext()) {
                    String result = scanner.next();
                    log.debug("[readResourceContent] result={}", result);
                    return result;
                } else {
                    log.error("[readResourceContent] result is: Cant read such resource file name={}. The scanner has no content", resourceFileName);
                }
            }
        } catch (Exception e) {
            log.error(String.format("[readResourceContent] Error: Cant read such resource file name=%s", resourceFileName), e);
        }
        return "";
    }
}
