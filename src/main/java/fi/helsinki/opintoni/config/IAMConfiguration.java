package fi.helsinki.opintoni.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import fi.helsinki.opintoni.integration.iam.IAMClient;
import fi.helsinki.opintoni.integration.iam.IamMockClient;
import fi.helsinki.opintoni.integration.interceptor.LoggingInterceptor;
import fi.helsinki.opintoni.util.NamedDelegatesProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class IAMConfiguration {

    @Autowired
    private AppConfiguration appConfiguration;

    @Value("${httpClient.keystoreLocation:null}")
    private String keystoreLocation;

    @Value("${httpClient.keystorePassword:null}")
    private String keystorePassword;

    @Value("${iam.useHttpClientCertificate:false}")
    private boolean useHttpClientCertificate;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate iamRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(getConverters());
        restTemplate.setInterceptors(Lists.newArrayList(new LoggingInterceptor()));
        return restTemplate;
    }

    private List<HttpMessageConverter<?>> getConverters() {
        final MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return Lists.newArrayList(converter);
    }

    @Bean
    public IAMClient iamMockClient() {
        return new IamMockClient();
    }

    @Bean
    public IAMClient iamClient() {
        return NamedDelegatesProxy.builder(
            IAMClient.class,
            () -> appConfiguration.get("iam.client.implementation")
        )
        .with("mock", iamMockClient())
        .build();
    }
}
