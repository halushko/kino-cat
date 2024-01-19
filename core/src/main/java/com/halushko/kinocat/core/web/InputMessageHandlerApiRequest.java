package com.halushko.kinocat.core.web;

import com.halushko.kinocat.core.JsonConstants.WebKeys;
import com.halushko.kinocat.core.handlers.input.InputMessageHandler;
import com.halushko.kinocat.core.prcessors.ValueProcessor;
import com.halushko.kinocat.core.prcessors.ServicesInfoProcessor;
import com.halushko.kinocat.core.rabbit.SmartJson;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
@Slf4j
public abstract class InputMessageHandlerApiRequest extends InputMessageHandler {
    protected final Map<String, String> serverUrls;

    public InputMessageHandlerApiRequest(String protocol, String ip, int port, String suffix) {
        this(
                new SmartJson(
                        new HashMap<>() {
                            {
                                put(WebKeys.KEY_PROTOCOL, protocol);
                                put(WebKeys.KEY_IP, ip);
                                put(WebKeys.KEY_PORT, port);
                                put(WebKeys.KEY_SUFFIX, suffix);
                            }
                        }
                ).getRabbitMessageText()

        );
    }

    public InputMessageHandlerApiRequest(String json) {
        serverUrls = getServicesInfoProcessor(json).getServiceUrls();
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

    protected String getDefaultProtocol() {
        return "http";
    }

    protected String getDefaultIp() {
        return "localhost";
    }

    protected String getDefaultNameValue() {
        return "";
    }

    protected abstract String getDefaultPort();

    protected abstract String getDefaultSuffix();

    public String getDefaultUrlTemplate() {
        return "%s://%s:%s/%s";
    }

    protected ServicesInfoProcessor getServicesInfoProcessor(String json) {
        return new ApiInfoProcessor(json);
    }

    private class ApiInfoProcessor extends ServicesInfoProcessor {
        public ApiInfoProcessor(String json) {
            super(json);
        }

        @Override
        public ValueProcessor getNameProcessor() {
            return new ValueProcessor(WebKeys.KEY_NAME, getDefaultNameValue());
        }

        @Override
        public List<ValueProcessor> getServiceProcessors() {
            return new ArrayList<>() {
                {
                    //do not change the order
                    add(new ValueProcessor(WebKeys.KEY_PROTOCOL, getDefaultProtocol()));
                    add(new ValueProcessor(WebKeys.KEY_IP, getDefaultIp()));
                    add(new ValueProcessor(WebKeys.KEY_PORT, getDefaultPort()));
                    add(new ValueProcessor(WebKeys.KEY_SUFFIX, getDefaultSuffix()));
                }
            };
        }

        @Override
        public String getUrlTemplate() {
            return getDefaultUrlTemplate();
        }
    }
}
