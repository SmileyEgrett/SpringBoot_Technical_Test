package com.example.fileproc.infrastructure.ip;

import com.example.fileproc.config.IpApiProperties;
import com.example.fileproc.domain.IpInfo;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestClientIpApiClient implements IpApiClient {

    private final RestClient restClient;

    public RestClientIpApiClient(IpApiProperties ipApiProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(ipApiProperties.getConnectTimeout());
        requestFactory.setReadTimeout(ipApiProperties.getReadTimeout());

        this.restClient = RestClient.builder()
                .baseUrl(ipApiProperties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public IpInfo lookup(String ip) {
        return restClient.get()
                .uri("/{ip}?fields=status,message,country,countryCode,isp,query", ip)
                .retrieve()
                .body(IpInfo.class);
    }
}
