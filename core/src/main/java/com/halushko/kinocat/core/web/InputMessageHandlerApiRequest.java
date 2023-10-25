package com.halushko.kinocat.core.web;

import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
@Slf4j
public abstract class InputMessageHandlerApiRequest extends InputMessageHandler {
    private final String serverUrl;

    public InputMessageHandlerApiRequest(String protocol, String ip, int port, String suffix) {
        this.serverUrl = String.format("%s://%s:%s/%s", protocol, ip, port, suffix);
    }

    protected ApiResponce send(String body, Map<String, String> headers) {
        return new ApiResponce(requestPrivate(body, headers));
    }
    private HttpResponse requestPrivate(String body, Map<String, String> headers) {
        log.debug("[request] headers=[{}], body=[{}]", headers, body);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(serverUrl);
            if (headers != null) {
                headers.forEach(postRequest::addHeader);
            }
            if (body != null && !body.isEmpty() && !body.equalsIgnoreCase("null")) {
                postRequest.setEntity(new StringEntity(body));
            }
            HttpResponse response = httpClient.execute(postRequest);
            log.debug("[request] output={}", response);
            return response;
        } catch (IOException e) {
            log.error(String.format("[request] Error during sending request to %s", serverUrl), e);
        }
        return null;
    }

    protected ApiResponce send(String body, String... headers) {
        val headerMap = IntStream.iterate(0, i -> i < headers.length, i -> i + 2).
                filter(i -> i + 1 < headers.length).
                boxed().
                collect(Collectors.toMap(i -> headers[i], i -> headers[i + 1], (a, b) -> b, LinkedHashMap::new));
        return send(body, headerMap);
    }
    protected abstract String getRequest();

}
