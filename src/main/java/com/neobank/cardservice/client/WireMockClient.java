package com.neobank.cardservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@FeignClient(name = "wiremock-client", url = "${wiremock.url:http://localhost:9999}")
public interface WireMockClient {

    @PostMapping("/mock/3ds/initiate")
    Map<String, String> initiate3ds();

}
