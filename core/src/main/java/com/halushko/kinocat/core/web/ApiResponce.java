package com.halushko.kinocat.core.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@SuppressWarnings("unused")
@Slf4j
public class ApiResponce {
    private final HttpResponse httpResponse;
    public ApiResponce(HttpResponse httpResponse) {
        if (httpResponse == null) {
            log.debug("[ApiResponce] Responce is null");
        }
        this.httpResponse = httpResponse;

    }

    public String getHeader(String headerKey) {
        if (httpResponse == null) {
            log.debug("[getHeader] Responce is null. Can't get header [{}]", headerKey);
            return "";
        }
        Header header = httpResponse.getFirstHeader(headerKey);
        String value = header.getValue();
        log.debug("[getHeader] Responce header {}=[{}]", headerKey, value);
        return value;
    }

    public String getBody() {
        log.debug("[getBody] Start to get body");

        if (httpResponse == null) {
            log.error("[getBody] Responce is null. Can't get body");
            return "";
        }
        try {
            String body = EntityUtils.toString(httpResponse.getEntity());
            log.debug("[getBody] Responce body=[{}]", body);
            return body;
        } catch (IOException e) {
            log.error("[getBody] Can't get body from responce", e);
        }
        return "";
    }
}
