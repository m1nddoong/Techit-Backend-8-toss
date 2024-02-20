package com.example.toss.config;

import com.example.toss.service.TossHttpService;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {
    @Value("{toss.secret}")
    private String tossSecret;

    @Bean
    public RestClient tossClient() {
        String basicAuth = Base64.getEncoder().encodeToString((tossSecret + ":").getBytes());
        return RestClient
                .builder()
                // 모든 요청들이 이 url 에서 시작한다.
                .baseUrl("https://api.tosspayments.com/v1")
                .defaultHeader("Authorization", String.format("Basic %s", basicAuth))
                .build();
    }
    @Bean
    public TossHttpService httpService() {
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(tossClient()))
                .build()
                .createClient(TossHttpService.class);
    }
}
