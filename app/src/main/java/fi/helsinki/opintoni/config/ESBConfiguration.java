package fi.helsinki.opintoni.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.helsinki.opintoni.integration.esb.ESBClient;
import fi.helsinki.opintoni.integration.esb.ESBMockClient;
import fi.helsinki.opintoni.integration.esb.ESBRestClient;
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Configuration
public class ESBConfiguration
{
    private static final String CLIENT_IMPLEMENTATION_PROPERTY = "esb.client.implementation";
    private static final String ESB_BASE_URL_PROPERTY = "esb.base.url";
    private static final String REST_IMPLEMENTATION = "rest";
    private static final String MOCK_IMPLEMENTATION = "mock";

    @Autowired
    private AppConfiguration appConfiguration;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate esbRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(newArrayList(new LoggingInterceptor()));
        restTemplate.setMessageConverters(getConverters());
        return restTemplate;
    }

    private List<HttpMessageConverter<?>> getConverters() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return newArrayList(converter);
    }

    private ESBClient esbRestClient() {
        return new ESBRestClient(appConfiguration.get(ESB_BASE_URL_PROPERTY), esbRestTemplate());
    }

    private ESBClient esbMockClient() {
        return new ESBMockClient(objectMapper);
    }

    private String getClientImplementation() {
        return appConfiguration.get(CLIENT_IMPLEMENTATION_PROPERTY);
    }

    @Bean
    public ESBClient esbClient() {
        switch(getClientImplementation()) {
            case REST_IMPLEMENTATION:
                return esbRestClient();
            case MOCK_IMPLEMENTATION:
                return esbMockClient();
            default:
                return esbMockClient();
        }
    }


}
