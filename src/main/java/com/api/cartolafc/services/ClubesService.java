package com.api.cartolafc.services;

import com.api.cartolafc.dtos.ClubesDTO;
import com.api.cartolafc.utils.Utils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static com.api.cartolafc.utils.Utils.BASE_URL;

@Service
public class ClubesService {

    private final RestTemplate restTemplate;

    public ClubesService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public Map<Integer, ClubesDTO> buscarClubes() {
        String url = BASE_URL + "/clubes";

        ResponseEntity<Map<Integer, ClubesDTO>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                Utils.createHttpEntityWithUserAgent(),
                new ParameterizedTypeReference<Map<Integer, ClubesDTO>>() {
                }
        );

        Map<Integer, ClubesDTO> clubes = response.getBody();
        if (clubes == null || clubes.isEmpty()) {
            throw new RuntimeException("Nenhum clube encontrado");
        }

        return clubes;
    }
}
