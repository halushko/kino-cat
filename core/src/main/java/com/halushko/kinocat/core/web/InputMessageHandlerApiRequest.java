package com.halushko.kinocat.core.web;

import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.rabbit.SmartJson;
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
    private final Map<String, String> serverUrls = new LinkedHashMap<>();

    public InputMessageHandlerApiRequest(String protocol, String ip, int port, String suffix) {
        this.serverUrls.put("default", String.format("%s://%s:%s/%s", protocol, ip, port, suffix));
    }

    public InputMessageHandlerApiRequest(String urls) {
        //noinspection unchecked
        new SmartJson(urls)
                .convertToList()
                .stream()
                .map((x -> (Map<String, Object>) x))
                .forEach(
                        x -> this.serverUrls.put(
                                String.valueOf(x.containsValue("name") ? x.get("name") : "default"),
                                String.format("%s://%s:%s/%s",
                                        x.containsValue("protocol") ? x.get("protocol") : "http",
                                        x.get("ip"),
                                        x.get("port"),
                                        x.containsValue("suffix") ? x.get("suffix") : "transmission/rpc"
                                )
                        )
                );
    }

    protected ApiResponce send(String serverName, String body, Map<String, String> headers) {
        return new ApiResponce(requestPrivate(body, headers, serverUrls.get(serverName)));
    }

    private HttpResponse requestPrivate(String body, Map<String, String> headers, String url) {
        log.debug("[request] headers=[{}], body=[{}]", headers, body);
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(url);
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
            log.error(String.format("[request] Error during sending request to %s", url), e);
        }
        return null;
    }

    protected ApiResponce send(String serverName, String body, String... headers) {
        val headerMap = IntStream.iterate(0, i -> i < headers.length, i -> i + 2).
                filter(i -> i + 1 < headers.length).
                boxed().
                collect(Collectors.toMap(i -> headers[i], i -> headers[i + 1], (a, b) -> b, LinkedHashMap::new));
        return send(serverName, body, headerMap);
    }
    protected abstract String getRequest();

}
